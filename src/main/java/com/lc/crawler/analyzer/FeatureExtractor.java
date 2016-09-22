package com.lc.crawler.analyzer;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Maps;
import com.google.api.client.util.Sets;
import com.google.common.base.Joiner;
import com.lc.crawler.parser.HtmlParser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ling Cao on 2016/8/17.
 */
public class FeatureExtractor {

    public static final int MAX_LENGTH = 8;

    private static Set<String> skipTags = Sets.newHashSet();

    static {
        skipTags.add("script");
    }


    public static final String SPACE = " ";
    public static final Joiner joiner = Joiner.on(SPACE);

    /**
     * extract features from a page
     * the result key is the CSS style selector(e.g. p.old-price span.price)
     * here only use class names and tag names, not id,
     * because sometimes class names are more meaningful for an element
     * and tag names allow to locate elements more precisely
     * meanwhile ids sometimes make confusion(e.g. id="product-price-395")
     * @param doc
     * @return
     */
    public static Map<String, String> extractPageFeatures(Document doc) {
        Map<String, String> result = Maps.newHashMap();

        for (Element e : doc.body().getAllElements()) {
            if (e.childNodeSize() == 1) {
                if (skipTags.contains(e.tagName())) {
                    continue;
                }
                Node child = e.childNodes().get(0);
                if (child.childNodeSize() > 0) {
                    continue;
                }
                String s = e.html();
                if (!Strings.isNullOrEmpty(s)) {
                    LinkedList<String> coordinates = new LinkedList<>();
                    while (true) {
                        if (e == null) {
                            break;
                        }
                        String eClass = e.attr("class");
                        String tagName = e.tagName();

                        if (!Strings.isNullOrEmpty(eClass)) {
                            eClass = eClass.trim();
                            int index = eClass.indexOf(SPACE);
                            if (index > 0) {
                                eClass = eClass.substring(0, index); //only retain first class
                            }
                            coordinates.addFirst(tagName + "." + eClass.trim());
                        } else {
                            coordinates.addFirst(tagName);
                        }
                        if (coordinates.size() > 1 && FeatureExtractor.canLocateElement(doc.body(), coordinates)) {
                            break;
                        }
                        e = e.parent();
                        if (e == doc.body()) {
                            break;
                        }
                        if (coordinates.size() >= MAX_LENGTH) {
                            break;
                        }
                    }
                    result.put(joiner.join(coordinates), s);
                }
            }
        }
        return result;
    }

    public static Map<String, String> extractPageFeatures(String content) {
        Document doc = Jsoup.parse(content);
        return extractPageFeatures(doc);
    }

    /**
     * extractor contents by selectors
     * @param html
     * @param selectors
     * @return
     */
    public static Map<String, String> extractDesignatedFeatures(String html, List<String> selectors) {
        Document doc = Jsoup.parse(html);
        Map<String, String> result = Maps.newHashMap();
        if (selectors != null && selectors.size() > 0) {
            selectors.forEach(f -> {
                Elements elements = doc.select(f);
                if (elements.size() == 1) {
                    String text = HtmlParser.removeAllHtmlTagsAndLineBreaks(StringEscapeUtils.unescapeHtml4(elements.html()));
                    if (!Strings.isNullOrEmpty(text)) {
                        result.put(f, text.trim());
                    }

                }
            });
        }
        return result;
    }

    public static boolean canLocateElement(Element element, List<String> coordinates) {
        if (coordinates.size() == 0) {
            return false;
        }
        String selector = joiner.join(coordinates);
        Elements elements = element.select(selector);
        return elements.size() == 1;
    }


    public static void main(String[] args) throws IOException {
        String s ="\"Always the right size. \n";
        System.out.println(s.trim());
        System.out.printf("====");
    }
}
