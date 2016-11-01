package com.lc.crawler.parser;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Sets;
import com.lc.crawler.utils.UrlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Ling Cao on 2016/8/16.
 */
public class HtmlParser {

    //invalid url pattern
    private final static Pattern pattern = Pattern.compile("^\\#");
    private final static Pattern goodPattern = Pattern.compile("htm(l?)$");

    private final static Set<String> htmlTagSet = Sets.newHashSet();

    static {
        htmlTagSet.addAll(Arrays.asList("a", "b", "blockquote", "br", "caption", "cite", "code", "col",
                "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
                "i", "img", "li", "ol", "p", "pre", "q", "small", "span", "strike", "strong",
                "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u",
                "ul"));
        htmlTagSet.addAll(Arrays.asList("a", "href", "title"));
        htmlTagSet.addAll(Arrays.asList("blockquote", "cite"));
        htmlTagSet.addAll(Arrays.asList("col", "span", "width"));
        htmlTagSet.addAll(Arrays.asList("colgroup", "span", "width"));
        htmlTagSet.addAll(Arrays.asList("img", "align", "alt", "height", "src", "title", "width"));
        htmlTagSet.addAll(Arrays.asList("ol", "start", "type"));
        htmlTagSet.addAll(Arrays.asList("q", "cite"));
        htmlTagSet.addAll(Arrays.asList("table", "summary", "width"));
        htmlTagSet.addAll(Arrays.asList("td", "abbr", "axis", "colspan", "rowspan", "width"));
        htmlTagSet.addAll(Arrays.asList("th", "abbr", "axis", "colspan", "rowspan", "scope", "width"));
        htmlTagSet.addAll(Arrays.asList("ul", "type"));

    }

    public static List<String> parseLinks(String html) {
        if (Strings.isNullOrEmpty(html)) {
            return Lists.newArrayList();
        }
        Document doc = Jsoup.parse(html);
        List<String> links = parseLinks(doc);
        return links.stream()
                .map(s -> UrlUtil.urlNormalize(s))
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }


    public static List<String> parseLinks(Document doc) {
        Elements links = doc.select("a[href]");
        return links.stream()
                .map(l -> l.attr("href"))
                .filter(u -> !pattern.matcher(u).find())
                .collect(Collectors.toList());
    }

    public static List<String> getUniqueLinks(String html) {
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a[href]");

        return links.stream()
                .map(e -> e.attr("href"))
                .filter(link -> !pattern.matcher(link).find())
                .filter(link -> goodPattern.matcher(link).find())
                .collect(Collectors.toList());
    }

    /**
     * remove all html tag in a coordinate
     * retain at most 2 className
     *
     * @param coordinate
     * @return
     */
    public static String getDisplayName(String coordinate) {
        if (Strings.isNullOrEmpty(coordinate)) {
            return null;
        }
        String[] ss = coordinate.split(" |\\.");
        String[] displayName = new String[2];
        displayName[0] = "";
        displayName[1] = "";
        int cnt = 0;
        for (int i = ss.length - 1; i > 0; i--) {
            if (!htmlTagSet.contains(ss[i])) {
                displayName[cnt] = ss[i];
                cnt++;
            }
            if (cnt >= 2) {
                break;
            }
        }
        return (displayName[1] + " " + displayName[0]).trim();
    }

    public static String removeAllHtmlTagsAndLineBreaks(String s) {
        return s.replaceAll("\\<.*?>", "").replaceAll("\\t|\\r|\\n", " ");
    }

    public static void main(String[] args) {
        String s = "div.main div.col-main div.product-view div.box-collateral ul.products-grid li.item h3.product-name a";
        System.out.println(getDisplayName(s));
    }


}
