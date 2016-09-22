package com.lc.crawler.unit;

import com.lc.crawler.BaseTest;
import com.lc.crawler.analyzer.DetailInfoExtractor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Ling Cao on 2016/8/17.
 */
public class DetailInfoTest extends BaseTest {

    @Autowired
    private DetailInfoExtractor detailInfoExtractor;

    @Test
    public void testDetail() {
        detailInfoExtractor.extract(null);
    }
}
