package com.lc.crawler.analyzer;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Sets;
import com.google.common.collect.Maps;
import com.lc.crawler.dao.PageInfoDAO;
import com.lc.crawler.domain.Constants;
import com.lc.crawler.domain.PageInfoDO;
import com.lc.crawler.domain.PageType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Created by Ling Cao on 2016/8/17.
 */
public class PageStat {

    @Autowired
    private PageInfoDAO pageInfoDAO;

    public static final int PAGE_SIZE = 50;

    public List<String> stat(float numThreshold, float diversityThreshold, PageType pageType, boolean useFullHtml) {
        int recordNum;
        final PageInfoDO pageInfoDO = new PageInfoDO();
        if (pageType != null) {
            pageInfoDO.setType(pageType.getType());
            pageInfoDO.setSiteId(Constants.SITE_ID);
            recordNum = pageInfoDAO.countPageInfo(pageInfoDO);
        } else {
            recordNum = pageInfoDAO.countPageInfo(null);
        }

        List<String> features = Lists.newArrayList();
        if (recordNum > 0) {

            int minNum = (int) (recordNum * numThreshold);

            int totalPageNum = recordNum / PAGE_SIZE + 1;
            Map<String, Tuple> map = Maps.newConcurrentMap();

            IntStream.range(1, totalPageNum + 1).forEach(
                    (i) -> {
                        List<PageInfoDO> pages;
                        if (pageType != null) {
                            pages = pageInfoDAO.queryPageInfo(pageInfoDO, i, PAGE_SIZE);
                        } else {
                            pages = pageInfoDAO.queryPageInfo(null, i, PAGE_SIZE);
                        }

                        pages.forEach(
                                p -> {
                                    Map<String, String> kvs = FeatureExtractor.extractPageFeatures(
                                            useFullHtml ? p.getContent() : p.getmContent());
                                    kvs.forEach((k, v) -> {
                                        Tuple tuple = map.get(k);
                                        if (tuple == null) {
                                            tuple = new Tuple();
                                        }
                                        tuple.num = tuple.num + 1;
                                        tuple.set.add(v);
                                        map.put(k, tuple);
                                    });
                                }
                        );
                    }
            );

            map.forEach((k, v) -> {
                float ratio = (float) v.set.size() / v.num;
                if (v.num > minNum && ratio > diversityThreshold) {
//                    System.out.println(ratio + " ~~~~~~~ " + v.num + " ~~~~~~~ " + k);
                    features.add(k);
                }
            });
        }
        System.out.println("-------------------------------------------------------------------------");
        return features;
    }

    public static class Tuple {

        public Tuple() {
            num = 0;
            set = Sets.newHashSet();
        }

        int num;
        Set<String> set;
    }
}
