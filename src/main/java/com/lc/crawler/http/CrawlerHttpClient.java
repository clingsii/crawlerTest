package com.lc.crawler.http;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Sets;
import com.lc.crawler.domain.CrawlerHttpResponse;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.Set;

/**
 * Created by Ling Cao on 2016/8/16.
 */
public class CrawlerHttpClient {

    private Set<String> allowedContentTypes;


    private HttpRequestFactory requestFactory;
    private HttpTransport httpTransport;

    public void init() {
        allowedContentTypes = Sets.newHashSet();
        allowedContentTypes.add("text/html; charset=UTF-8");//only accept text/html, other types like multimedia are useless here

        httpTransport= new NetHttpTransport();
        requestFactory = httpTransport.createRequestFactory();
    }

    public void destroy() {
        try {
            httpTransport.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CrawlerHttpResponse queryURL(String urlStr) {
        GenericUrl url = new GenericUrl(urlStr);
        HttpRequest req = null;
        HttpResponse res = null;

        CrawlerHttpResponse response = new CrawlerHttpResponse();
        response.setSuccess(false);

        try {
            req = requestFactory.buildGetRequest(url);
            /**
             * not allow redirect, often happens when need login
             * since here we only need item detail pages and list pages.
             */
            req.setFollowRedirects(false);
            res = req.execute();

            response.setResponseCode(res.getStatusCode());

            if (!allowedContentTypes.contains(res.getContentType())) {
                response.setErrorMsg(CrawlerHttpResponse.ERROR_CONTENT_TYPE);
            } else {
                if (res.getStatusCode() == HttpStatusCodes.STATUS_CODE_OK ||
                        res.getStatusCode() == HttpStatusCodes.STATUS_CODE_NO_CONTENT) {
                    response.setSuccess(true);
                    response.setHtml(StringEscapeUtils.unescapeHtml4(res.parseAsString()));//unescape html like &
                } else {
                    response.setErrorMsg(res.getStatusMessage());
                    response.setSuccess(false);
                }
            }

            return response;
        } catch (HttpResponseException e) {
            response.setResponseCode(e.getStatusCode());
            response.setErrorMsg(e.getStatusMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (res != null) {
                try {
                    res.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

}
