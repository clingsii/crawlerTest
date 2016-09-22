package com.lc.crawler.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by Ling Cao on 2016/8/17.
 */
public class PageFeatures {


    /**
     * count the number of selector in a page
     *
     * @param selectors
     * @param html
     * @return
     */
    public static int hitSelectors(List<String> selectors, String html) {
        Document doc = Jsoup.parse(html);
        int cnt = 0;
        for (String s : selectors) {
            Elements elements = doc.select(s);
            if (elements.size() > 0) {
                cnt++;
            }
        }
        return cnt;
    }
}
