package com.lc.crawler.analyzer;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.lc.crawler.dao.CategoryDAO;
import com.lc.crawler.domain.CategoryDO;
import com.lc.crawler.domain.PageInfoDO;
import com.lc.crawler.domain.PageType;
import com.lc.crawler.parser.HtmlParser;
import com.lc.crawler.dao.PageInfoDAO;
import com.lc.crawler.domain.Constants;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generate category hierachy and decide which category items belong to
 * to simplify the code. assuming that there will be at most 2 levels (apart of home page)in category hierachy.
 * which is Home -> Level1 -> Level2
 *
 * Created by Ling Cao on 2016/8/17.
 */
public class CategoryAnalyzer {


    @Autowired
    private PageInfoDAO pageInfoDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    public static final int PAGE_SIZE = 1000; //to load data once

    public void execute(long rootCatId) {

        System.out.println("Start to analyse category hierarchy");

        PageInfoDO pageInfoDO = new PageInfoDO();
        pageInfoDO.setSiteId(Constants.SITE_ID);
        pageInfoDO.setType(PageType.LIST.getType());
        List<PageInfoDO> listPageInfos = pageInfoDAO.queryPageInfo(pageInfoDO, 1, PAGE_SIZE);


        listPageInfos = listPageInfos.stream().filter(p ->
                p.getUrl().indexOf("?") == -1
        ).collect(Collectors.toList());

        Map<String, Long> catMap = listPageInfos.stream().collect(Collectors.toMap(PageInfoDO::getUrl, PageInfoDO::getId));
        Map<Long, Long> idGroupMap = Maps.newHashMap();
        Map<Long, Long> cntMap = Maps.newHashMap();
        Map<Long, String> catNameMap = Maps.newHashMap();
        Map<Long, String> catContentMap = Maps.newHashMap();

        Map<String, PageStat.Tuple> map = com.google.common.collect.Maps.newConcurrentMap();


        /**
         * Step 1.
         * find the selector(css style) of category name,
         * which are basically different in every page(only a few category names are same)
         * so just find the element with most values in all category pages
         */
        listPageInfos.forEach(p -> {
            catContentMap.put(p.getId(), p.getmContent());
            Map<String, String> kvs = FeatureExtractor.extractPageFeatures(
                    p.getmContent());
            kvs.forEach((k, v) -> {
                PageStat.Tuple tuple = map.get(k);
                if (tuple == null) {
                    tuple = new PageStat.Tuple();
                }
                tuple.num = tuple.num + 1;
                tuple.set.add(v);
                map.put(k, tuple);
            });
        });

        String selector = null;
        int maxNum = 0;

        final int catPageSize = listPageInfos.size();

        for (Map.Entry<String, PageStat.Tuple> entry : map.entrySet()) {
            if (entry.getValue().num == catPageSize) {
                if (entry.getValue().set.size() > maxNum) {
                    selector = entry.getKey();
                    maxNum = entry.getValue().set.size();
                }
            }
        }

        final String selector1 = new String(selector); //to use it in lambda


        /**
         * Step 2.
         * put all categories in their group, and calculate the number of links
         * based on the links in each page.
         * for example.If page A has 3 links to page B,C,D
         * then page A,B,C,D are in one group, and page A has a value of 3
         */
        listPageInfos.forEach(p -> {
            List<String> links = HtmlParser.getUniqueLinks(p.getmContent());
            String catName = getName(p.getmContent(), selector1);
            catNameMap.put(p.getId(), catName);
            cntMap.put(p.getId(), getCatLinkNum(links, catMap));
            //first iterate, check if the group exists
            links.forEach(s -> {
                Long sid = catMap.get(s);
                if (sid != null) {
                    Long groupId = idGroupMap.get(sid);
                    if (groupId != null) {
                        idGroupMap.put(p.getId(), groupId);
                    }
                }
            });
            //second iterate(if needed), put all id in one group
            if (idGroupMap.get(p.getId()) == null) {
                idGroupMap.put(p.getId(), p.getId());
                links.forEach(s -> {
                    Long sid = catMap.get(s);
                    if (sid != null) {
                        idGroupMap.put(sid, p.getId());
                    }
                });
            }
        });


        /**
         * still step 3.
         * put each group to a list
         */
        Map<Long, List<CatInfo>> catGroupMap = Maps.newHashMap();
        idGroupMap.forEach((k, v) -> {
                    List<CatInfo> l = catGroupMap.get(v);
                    if (l == null) {
                        l = Lists.newArrayList();
                    }
                    CategoryDO categoryDO = new CategoryDO();
                    categoryDO.setCatId(k);
                    categoryDO.setName(catNameMap.get(k));
                    categoryDO.setSite_id(Constants.SITE_ID);
                    CatInfo catInfo = new CatInfo();
                    catInfo.categoryDO = categoryDO;
                    catInfo.val = cntMap.get(k);
                    l.add(catInfo);
                    catGroupMap.put(v, l);
                }
        );


        /**
         * Step 4
         * decide the parent category and the children.
         * categories with the highest value in each group will be selected as the parent category.
         * others will be children
         * then store the result to database.
         */
        List<CategoryDO> leftCatList = Lists.newArrayList();
        for (List<CatInfo> list : catGroupMap.values()) {
            long maxVal = 0;
            long parentId = rootCatId;
            if (list.size() == 1) {
                CategoryDO categoryDO = list.get(0).categoryDO;
                categoryDO.setParent_id(rootCatId);
                categoryDAO.insertCategory(categoryDO);

            } else {
                for (CatInfo catInfo : list) {
                    if (catInfo.val > maxVal) {
                        parentId = catInfo.categoryDO.getCatId();
                        maxVal = catInfo.val;
                    }
                }
                for (CatInfo catInfo : list) {
                    CategoryDO categoryDO = catInfo.categoryDO;
                    if (categoryDO.getCatId() == parentId) {
                        categoryDO.setParent_id(rootCatId);
                    } else {
                        categoryDO.setParent_id(parentId);
                        leftCatList.add(categoryDO);
                    }
                    categoryDAO.insertCategory(categoryDO);
                }
            }
        }

        /**
         * Step 5
         * calculate items' category.
         * if a category page A has links to item pages B,C,D.
         * then items B,C,D are classified into category A
         */
        leftCatList.forEach(c -> {
            String content = catContentMap.get(c.getCatId());
            List<String> links = HtmlParser.parseLinks(content);
            links.forEach(l-> {
                PageInfoDO p = pageInfoDAO.findPageInfoByUrl(l);
                if (p != null && p.getType()== PageType.DETAIL.getType()){
                    p.setCatId(c.getCatId());
                    pageInfoDAO.updatePageInfo(p);
                }
            });
        });

        System.out.println("Finish analysing category hierachy");
        System.out.println();
    }

    private static String getName(String html, String selector) {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(selector);
        return StringEscapeUtils.unescapeHtml4(elements.first().html());
    }

    private static long getCatLinkNum(List<String> links, Map<String, Long> catMap) {
        return links.stream().filter(s -> catMap.get(s) != null).count();
    }

    class CatInfo {
        CategoryDO categoryDO;
        Long val;
    }
}
