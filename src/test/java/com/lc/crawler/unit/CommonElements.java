package com.lc.crawler.unit;

import com.lc.crawler.BaseTest;
import com.lc.crawler.analyzer.RemoveCommonElements;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Ling Cao on 2016/8/18.
 */
public class CommonElements extends BaseTest {

    @Autowired
    private RemoveCommonElements mContent;

    @Test
    public void start() {
        mContent.execute();
    }
}
