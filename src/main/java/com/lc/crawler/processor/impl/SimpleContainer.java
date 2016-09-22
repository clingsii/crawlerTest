package com.lc.crawler.processor.impl;

import com.google.common.collect.Sets;
import com.lc.crawler.processor.Container;

import java.util.Set;

/**
 *
 * a simple implementation using Set
 * it may be replaced by other implementations when used in a larger data set;
 *
 * Created by Ling Cao on 2016/8/17.
 */
public class SimpleContainer implements Container {

    private Set<String> storage;

    public void init() {
        storage = Sets.newConcurrentHashSet();
    }

    @Override
    public void add(String url) {
        storage.add(url);
    }

    @Override
    public boolean exists(String url) {
        return storage.contains(url);
    }
}
