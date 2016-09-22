package com.lc.crawler.analyzer;

import com.lc.crawler.dao.PageInfoDAO;
import com.lc.crawler.domain.PageInfoDO;
import com.lc.crawler.domain.PageType;
import com.lc.crawler.parser.PageFeatures;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * classify all pages
 *
 * assuming there are only 2 types: ITEM PAGE & LIST PAGE
 *
 * the main difference between item pages and list pages is:
 * item pages have unique contents like product title, price which basically will not repeat in other pages
 * while list pages don't have these unique contents
 *
 * Created by Ling Cao on 2016/8/17.
 */
public class PageClassifier {

    private float numThreshold;
    private float diversityThreshold;
    private float detailThreshold;

    @Autowired
    private PageStat pageStat;

    @Autowired
    private PageInfoDAO pageInfoDAO;

    public static final int PAGE_SIZE = 50;

    public void execute() {

        System.out.println("Start to classify pages");

        List<String> selectors = pageStat.stat(numThreshold, diversityThreshold, null, false);
        int pageNum = pageInfoDAO.countPageInfo(null);
        int threshold = (int) (selectors.size() * detailThreshold);
        if (pageNum > 0) {
            int totalPageNum = pageNum / PAGE_SIZE + 1;

            IntStream.range(1, totalPageNum + 1).forEach(
                    (i) -> {
                        List<PageInfoDO> pages = pageInfoDAO.queryPageInfo(null, i, PAGE_SIZE);
                        pages.forEach(p -> {
                            int hitNum = PageFeatures.hitSelectors(selectors, p.getContent());
                            if (hitNum > threshold) {
                                p.setType(PageType.DETAIL.getType());
                            } else {
                                p.setType(PageType.LIST.getType());
                            }
                            pageInfoDAO.updatePageInfo(p);
                        });
                    }
            );
        }
        System.out.println("Finish classifying pages");
        System.out.println();
    }


    public void setDiversityThreshold(float diversityThreshold) {
        this.diversityThreshold = diversityThreshold;
    }

    public void setNumThreshold(float numThreshold) {
        this.numThreshold = numThreshold;
    }

    public void setDetailThreshold(float detailThreshold) {
        this.detailThreshold = detailThreshold;
    }
}
