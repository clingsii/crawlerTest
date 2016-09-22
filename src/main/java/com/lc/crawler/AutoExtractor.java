package com.lc.crawler;

import com.lc.crawler.analyzer.DetailInfoExtractor;
import com.lc.crawler.analyzer.CategoryAnalyzer;
import com.lc.crawler.analyzer.PageClassifier;
import com.lc.crawler.analyzer.RemoveCommonElements;
import com.lc.crawler.domain.Constants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Ling Cao on 2016/8/20.
 */
public class AutoExtractor {

    @Autowired
    private RemoveCommonElements mContent;

    @Autowired
    private PageClassifier pageClassifier;

    @Autowired
    private CategoryAnalyzer categoryAnalyzer;

    @Autowired
    private DetailInfoExtractor detailInfoExtractor;


    public void execute(String outputFilePath) {
        /**
         * Step 1
         * remove all common elements
         */
        mContent.execute();

        /**
         * Step 2
         * classify page into 2 group: List & Detail
         */
        pageClassifier.execute();

        /**
         * Step 3
         * analyze category hierachy and category name
         */
        categoryAnalyzer.execute(Constants.ROOT_CAT_ID);

        /**
         * Step 4
         * extract product information
         */
        detailInfoExtractor.extract(outputFilePath);


    }

}
