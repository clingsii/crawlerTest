package com.lc.crawler.utils;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ling Cao on 2016/8/16.
 */
public class UrlUtil {

    private static final Pattern p = Pattern.
            compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);

    private static final Pattern goodP = Pattern.compile(".*htm(l?)\\?*.*");

    public static final String QUESTION_MARK = "?";

    public static String getDomain(String url) {
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String removeParameters(String url) {
        int index = url.indexOf(QUESTION_MARK);
        if (index == -1) {
            return url;
        }
        return url.substring(0, index);
    }

    public static String urlNormalize(String url) {
        try {
            URI u = new URI(url);
            String newUurl = u.normalize().toURL().toString();
            int index = newUurl.indexOf("&");
            if (index < 0) {
                return newUurl;
            } else {
                return newUurl.substring(0, index); // retain only 1 query parameter
            }
        } catch (URISyntaxException e) {
            //Ignore
        } catch (MalformedURLException e) {
            //Ignore
        }
        return null;
    }

    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean goodEnds(String url) {
        Matcher matcher = goodP.matcher(url);
        return matcher.find();
    }

    public static void main(String[] args) {
        String s = "http://www.sli-demo.com/accessories/jewelry.html";
        System.out.println(getDomain(s));
    }

}
