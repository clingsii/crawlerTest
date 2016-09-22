package com.lc.crawler.analyzer;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.lc.crawler.category.CategoryService;
import com.lc.crawler.dao.PageInfoDAO;
import com.lc.crawler.domain.Constants;
import com.lc.crawler.domain.PageInfoDO;
import com.lc.crawler.domain.PageType;
import com.lc.crawler.parser.HtmlParser;
import com.lc.crawler.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * extract useful information from item detail pages.
 * <p>
 * <p>
 * Created by Ling Cao on 2016/8/17.
 */
public class DetailInfoExtractor {

    private float numThreshold;
    private float diversityThreshold;

    @Autowired
    private PageStat pageStat;

    @Autowired
    private PageInfoDAO pageInfoDAO;

    @Autowired
    private CategoryService categoryService;

    public static final int PAGE_SIZE = 20;

    public void extract(String output) {

        System.out.println("Start to extract product info");

        Map<Long, String> catPathMap = categoryService.getCategoryPathMap();

        List<String> features = pageStat.stat(numThreshold, diversityThreshold, PageType.DETAIL, false);

        PageInfoDO pageInfoDO = new PageInfoDO();
        pageInfoDO.setSiteId(Constants.SITE_ID);
        pageInfoDO.setType(PageType.DETAIL.getType());
        int pageNum = pageInfoDAO.countPageInfo(pageInfoDO);
        if (pageNum > 0) {
            int totalPageNum = PAGE_SIZE + 1;

            Map<String, ItemInfo> itemMap = Maps.newHashMap();

            IntStream.range(1, totalPageNum + 1).forEach(
                    (i) -> {
                        List<PageInfoDO> pages = pageInfoDAO.queryPageInfo(pageInfoDO, i, PAGE_SIZE);
                        pages.forEach(p -> {
                            Map<String, String> f = FeatureExtractor.extractDesignatedFeatures(p.getmContent(), features);
                            Map<String, String> resultMap = Maps.newTreeMap();
                            f.forEach((k, v) -> {
                                        String name = resultMap.get(v);
                                        String displayName = HtmlParser.getDisplayName(k);
                                        if (!Strings.isNullOrEmpty(displayName)) {
                                            /**
                                             * remove same contents within a page, show the longest display name
                                             * for example. product name appear twice in a page.
                                             * in such cases.keep one product name is enough, just keep one with the longest display name
                                             */
                                            if (Strings.isNullOrEmpty(name) || displayName.length() > name.length()) {
                                                resultMap.put(v, displayName);
                                            }
                                        }
                                    }

                            );

                            /**
                             * signatures are used to identify same products in the website.
                             * for example: http://www.sli-demo.com/home-decor/electronics/8gb-memory-card.html
                             *        and   http://www.sli-demo.com/8gb-memory-card.html
                             *  are same item with different url.
                             *  assume that:  if all the extracted contents of two items are same, then consider them identical,
                             *                only keep one (page with category id is more preferable)
                             */
                            String signature = MapUtil.getSignature(resultMap);
                            ItemInfo itemInfo = itemMap.get(signature);
                            if (itemInfo == null || Strings.isNullOrEmpty(itemInfo.catPath)) {
                                String catPath = catPathMap.get(p.getCatId());
                                ItemInfo item = new ItemInfo(p.getUrl(), resultMap, catPath);
                                itemMap.put(signature, item);
                            }


                        });
                    }
            );

            /**
             * output the result into the console
             */
            List<String> outputList = Lists.newArrayList();
            itemMap.values().forEach(i -> {
                System.out.println(i.url);
                i.info.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((e) -> {
                            String line = e.getValue() + ":" + e.getKey();
                            outputList.add(line);
                            System.out.println(line);
                        }
                );

                if (!Strings.isNullOrEmpty(i.catPath)) {
                    outputList.add("category:" + i.catPath);
                    System.out.println("category:" + i.catPath);
                }
                System.out.println("-------------------------------------------------------------------------");
            });

            /**
             * output to text file
             */
            if (!Strings.isNullOrEmpty(output)) {
                try {
                    Files.write(Paths.get(output), outputList, Charset.defaultCharset());
                } catch (IOException e) {
                    System.out.println("output result error. the reason is : " + e.getMessage());
                }
            }
        }
        System.out.println("Finish extracting product info");
        System.out.println();

    }

    public void setNumThreshold(float numThreshold) {
        this.numThreshold = numThreshold;
    }

    public void setDiversityThreshold(float diversityThreshold) {
        this.diversityThreshold = diversityThreshold;
    }

    class ItemInfo {
        String url;
        Map<String, String> info;
        String catPath;

        public ItemInfo(String url, Map<String, String> info, String catPath) {
            this.url = url;
            this.info = info;
            this.catPath = catPath;
        }
    }

}
