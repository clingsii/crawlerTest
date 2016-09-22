package com.lc.crawler.processor;

/**
 *
 * provide a container to store processed urls,
 * so they won't be processed again
 *
 * Created by Ling Cao on 2016/8/17.
 */
public interface Container {

    /**
     * add a string to container
     * @param url
     */
    void add(String url);

    /**
     * return if the string is already in the container
     * @param url
     * @return
     */
    boolean exists(String url);
}
