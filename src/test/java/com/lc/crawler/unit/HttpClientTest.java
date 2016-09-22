package com.lc.crawler.unit;

import com.lc.crawler.BaseTest;
import com.lc.crawler.http.CrawlerHttpClient;
import com.lc.crawler.domain.CrawlerHttpResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class HttpClientTest
        extends BaseTest {

    @Autowired
    private CrawlerHttpClient crawlerHttpClient;

    @Test
    public void testItemDetailPage() {
        String url = "http://www.sli-demo.com/home-decor/electronics/madison-rx3400.html";
        CrawlerHttpResponse response = crawlerHttpClient.queryURL(url);
        assertTrue(response.isSuccess());
        assertTrue(response.getHtml().length() > 0);
        System.out.println(response.getHtml());
    }

    @Ignore
    public void testMultiMediaPage() {
        String url = "http://www.sli-demo.com/downloadable/download/linkSample/link_id/13/";
        CrawlerHttpResponse response = crawlerHttpClient.queryURL(url);
        assertFalse(response.isSuccess());
    }

    @Ignore
    public void testRedirectedURL() {
        String url = "http://www.sli-demo.com/wishlist/";
        CrawlerHttpResponse response = crawlerHttpClient.queryURL(url);
        assertFalse(response.isSuccess());
    }

    @Ignore
    public void testServerExceptionURL() {
        String url = "http://www.sli-demo.com/large-camera-bag.html";
        CrawlerHttpResponse response = crawlerHttpClient.queryURL(url);
        assertFalse(response.isSuccess());
    }

}
