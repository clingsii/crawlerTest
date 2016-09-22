package com.lc.crawler.unit;

import com.lc.crawler.analyzer.PageStat;
import com.lc.crawler.BaseTest;
import com.lc.crawler.domain.PageType;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Ling Cao on 2016/8/17.
 */
public class StatTest extends BaseTest {

    @Autowired
    private PageStat pageStat;

    @Ignore
    public void testCommon() {
        List<String> features = pageStat.stat(0.1f, 0.2f, null, false);
        assertTrue(features.size() > 0);
    }

    @Ignore
    public void testDetail() {
        List<String> features = pageStat.stat(0.001f, 0.001f, PageType.DETAIL, false);
        assertTrue(features.size() > 0);
    }

    @Test
    public void testAll() {
        List<String> features = pageStat.stat(0.8f, 0.0f, null, true);
        assertTrue(features.size() > 0);
    }


}
