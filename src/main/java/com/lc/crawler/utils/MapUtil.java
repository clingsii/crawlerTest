package com.lc.crawler.utils;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Ling Cao on 2016/8/20.
 */
public class MapUtil {

    private static final Joiner joiner = Joiner.on(",");

    /**
     * get simple signature of a map
     * @param map
     * @return
     */
    public static String getSignature(Map<String, String> map) {
        List<String> values = map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(Collectors.toList());
        return joiner.join(values);
    }

}
