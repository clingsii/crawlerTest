package com.lc.crawler.domain;

/**
 * Created by Ling Cao on 2016/8/16.
 */
public class CrawlerHttpResponse {


    public static final String ERROR_CONTENT_TYPE = "content type error";

    private boolean isSuccess;
    private String html;
    private int responseCode;
    private String errorMsg;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
