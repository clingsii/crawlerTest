package com.lc.crawler.unit;

import com.lc.crawler.BaseTest;
import com.lc.crawler.analyzer.PageClassifier;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Ling Cao on 2016/8/17.
 */
public class ClassifierTest extends BaseTest {

    @Autowired
    private PageClassifier pageClassifier;

    @Test
    public void classify(){
        pageClassifier.execute();
    }
}
