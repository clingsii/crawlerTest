package com.lc.crawler.processor;

import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.lc.crawler.http.CrawlerHttpClient;
import com.lc.crawler.parser.HtmlParser;
import com.lc.crawler.utils.UrlUtil;
import com.lc.crawler.dao.PageInfoDAO;
import com.lc.crawler.domain.CrawlerHttpResponse;
import com.lc.crawler.domain.PageInfoDO;
import com.lc.crawler.utils.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * Created by Ling Cao on 2016/8/16.
 */
public class CrawlerWorker {

    private int queueSize;
    private int threadNum;

    private BlockingQueue<String> todoQueue;
    private BlockingQueue<String> bufferQueue;
    private TreeSet<String> secondaryBuffer;
    private ExecutorService pool;
    private List<String> skipUrlList;

    private AtomicInteger counter;
    private AtomicInteger failCounter;
    private AtomicInteger skipCounter;

    private String domain;

    /**
     * must be volatile to ensure visibility
     */
    private volatile boolean flag = true;

    public static final float SIMILARITY_THRESHOLD = 0.95F;


    @Autowired
    private CrawlerHttpClient crawlerHttpClient;

    @Autowired
    private PageInfoDAO pageInfoDAO;

    @Autowired
    private Container container;

    public void init() {
        todoQueue = Queues.newLinkedBlockingQueue(queueSize);
        bufferQueue = Queues.newLinkedBlockingQueue(queueSize * 2);
        secondaryBuffer = Sets.newTreeSet();
        pool = Executors.newFixedThreadPool(threadNum);
        skipUrlList = Lists.newLinkedList();
        counter = new AtomicInteger();
        failCounter = new AtomicInteger();
        skipCounter = new AtomicInteger();
    }

    public FutureTask<Integer> doFetch(String seed) {

        if (Strings.isNullOrEmpty(seed)) {
            throw new IllegalArgumentException("seed can't be null");
        }

        todoQueue.offer(seed);
        domain = UrlUtil.getDomain(seed);


        /**
         * monitor thread
         * output running status,and stop all threads when task is over
         */
        Callable<Integer> monitor = () -> {
            try {
                System.out.println("Monitor start............");
                while (flag) {
                    Thread.sleep(5000);
                    int queueSize = todoQueue.size();
                    int cnt = counter.get();
                    System.out.println("queueSize is : " + queueSize + " | success cnt: " + cnt + " | fail cnt : " + failCounter.get()
                            + " | skip cnt: " + skipCounter.get() + " | skipUrlNum: " + skipUrlList.size()
                            + "| buffer size :" + bufferQueue.size() + " | 2nd bufferSize : " + secondaryBuffer.size());
                    if (queueSize == 0) {
                        flag = false;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return counter.get();
        };


        /**
         * move data from buffer to todoQueue.
         * here I designed two buffer queues to allow fetching pages
         * with priority, some pages are surely more important than others.
         * while for this test, it's not very necessary to download irrelevant pages
         * and the number of total pages is not very big
         * so I commented the secondary buffer
         */
        Runnable dataMover = () -> {
            System.out.println("Data mover start............");
            while (flag) {
                try {
                    String url;
                    if (bufferQueue.size() == 0) {
//                            if (secondaryBuffer.size() > 0) {
//                                url = secondaryBuffer.first();
//                                secondaryBuffer.remove(url);
//                            } else {
                        Thread.sleep(100);
                        continue;
//                            }
                    } else {
                        url = bufferQueue.poll(1, TimeUnit.SECONDS);
                    }

                    if (Strings.isNullOrEmpty(url)) {
                        continue;
                    }

                    String d = UrlUtil.getDomain(url);
                    if (!domain.equals(d)) {
                        continue;
                    }

                    if (container.exists(url)) {
                        continue;
                    }
                    if (testSkipList(url, false)) {
                        skipCounter.incrementAndGet();
                        continue;
                    }
                    if (UrlUtil.goodEnds(url)) {
                        todoQueue.offer(url);
                        container.add(url);
                    } else {
//                            secondaryBuffer.add(url);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        pool.execute(dataMover);


        /**
         * worker thread, fetch html from website and insert results into database.
         * use multiple threads to do task faster
         */
        for (int i = 1; i <= threadNum; i++) {
            pool.execute(() -> {
                while (flag) {
                    try {
                        if (todoQueue.isEmpty()) {
                            Thread.sleep(2000);
                        }
                        String url = todoQueue.poll(5, TimeUnit.SECONDS);
                        if (Strings.isNullOrEmpty(url)) {
                            continue;
                        }

                        container.add(url);

                        if (testSkipList(url, false)) {
                            skipCounter.incrementAndGet();
                            System.out.println("skip url : " + url);
                            continue;
                        }

                        CrawlerHttpResponse response = crawlerHttpClient.queryURL(url);


                        if (response.isSuccess()) {
                            String html = response.getHtml();

                            html = html.replaceAll("\\s{2,}", " ");

                            PageInfoDO pageInfoDO = new PageInfoDO();
                            pageInfoDO.setUrl(url);
                            pageInfoDO.setContent(html);
                            pageInfoDO.setChecksum(UrlUtil.getMD5(html));
                            pageInfoDO.setSiteId(1);

                            pageInfoDAO.insertPageInfo(pageInfoDO);

                            counter.incrementAndGet();

                            List<String> links = HtmlParser.parseLinks(html);
                            bufferQueue.addAll(links);
                        } else {
                            if (response.getResponseCode() == HttpStatusCodes.STATUS_CODE_MULTIPLE_CHOICES
                                    || response.getResponseCode() == HttpStatusCodes.STATUS_CODE_MOVED_PERMANENTLY
                                    || response.getResponseCode() == HttpStatusCodes.STATUS_CODE_FOUND
                                    || response.getResponseCode() == HttpStatusCodes.STATUS_CODE_SEE_OTHER
                                    || response.getResponseCode() == HttpStatusCodes.STATUS_CODE_NOT_MODIFIED
                                    || response.getResponseCode() == HttpStatusCodes.STATUS_CODE_TEMPORARY_REDIRECT) {
                                System.out.println("redirect url : " + url);
                                testSkipList(url, true);
                            } else {
                                failCounter.incrementAndGet();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        FutureTask<Integer> futureTask = new FutureTask<>(monitor);
        Thread t = new Thread(futureTask);
        t.start();
        return futureTask;
    }

    /**
     * skip url if they are similar to known bad urls
     * the similarities are computed by levenshtein distance
     * @param url
     * @param doAdd
     * @return
     */
    public synchronized boolean testSkipList(String url, boolean doAdd) {
        Predicate<String> p = s -> StrUtil.levenshteinDistancePercent(url, s) >= SIMILARITY_THRESHOLD;
        boolean match = skipUrlList.stream().anyMatch(p);
        if (!match && doAdd) {
            skipUrlList.add(url);
        }
        return match;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }
}
