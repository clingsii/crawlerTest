package com.lc.crawler.analyzer;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.lc.crawler.dao.PageInfoDAO;
import com.lc.crawler.domain.PageInfoDO;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * remove all common contents(e.g. navigation bar) in each page
 * and header, scripts, comments
 * so they won't interfere following operations.
 *
 * Created by Ling Cao on 2016/8/18.
 */
public class RemoveCommonElements {

    private float numThreshold;
    private float diversityThreshold;

    public static final int PAGE_SIZE = 100;

    @Autowired
    private PageStat pageStat;

    @Autowired
    private PageInfoDAO pageInfoDAO;


    public void execute() {

        System.out.println("Start to remove common elements");

        List<String> features = pageStat.stat(numThreshold, diversityThreshold, null, true);

        int pageNum = pageInfoDAO.countPageInfo(null);
        if (pageNum > 0) {
            int totalPageNum = PAGE_SIZE + 1;
            IntStream.range(1, totalPageNum + 1).parallel().forEach(
                    (i) -> {
                        List<PageInfoDO> pages = pageInfoDAO.queryPageInfo(null, i, PAGE_SIZE);
                        pages.forEach(p -> {
                            String mContent = minifyHtml(p.getContent(), features);
                            p.setmContent(mContent);
                            pageInfoDAO.updatePageInfo(p);
                        });

                    }
            );
        }
        System.out.println("Finish removing common elements");
        System.out.println();
    }

    private static String minifyHtml(String html, List<String> features) {
        if (Strings.isNullOrEmpty(html)) {
            return null;
        }
        Document doc = Jsoup.parse(html);
        features.forEach(f -> {
            Elements elements = doc.select(f);
            if (elements != null) {
                elements.remove();
            }
        });
        removeEmptyElements(doc);
        removeComments(doc);
        Elements elements = doc.getElementsByTag("script");
        elements.remove();
        return StringEscapeUtils.unescapeHtml4(doc.body().html());
    }


    private static void removeEmptyElements(Document doc) {
        for (Element element : doc.getAllElements()) {
            if (!element.hasText() && element.isBlock()) {
                element.remove();
            }
        }
    }

    private static void removeComments(Node node) {
        for (int i = 0; i < node.childNodes().size(); ) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment")) child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }


    public void setNumThreshold(float numThreshold) {
        this.numThreshold = numThreshold;
    }

    public void setDiversityThreshold(float diversityThreshold) {
        this.diversityThreshold = diversityThreshold;
    }
}
