package com.lc.crawler;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.lc.crawler.dao.PageInfoDAO;
import com.lc.crawler.domain.PageInfoDO;
import com.lc.crawler.utils.MapUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Created by Ling Cao on 2016/8/19.
 */
public class SimpleExtractor {

    private final static Pattern pattern = Pattern.compile("^\\#");

    @Autowired
    private PageInfoDAO pageInfoDAO;

    public static final int PAGE_SIZE = 50;

    public static final String CATEGORY_PATH = "Category Path";


    public void run(String outputFilePath) {
        Properties prop = new Properties();
        int pageNum = pageInfoDAO.countPageInfo(null);
        if (pageNum > 0) {
            int totalPageNum = pageNum / PAGE_SIZE + 1;
            //load rule file
            try (InputStream is = SimpleExtractor.class.getClassLoader().getResourceAsStream("sli-products.properties")) {
                prop.load(is);

            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Map<String, ProductInfo> resultMap = Maps.newHashMap();

            IntStream.range(1, totalPageNum + 1).forEach(
                    (i) -> {
                        List<PageInfoDO> pages = pageInfoDAO.queryPageInfo(null, i, PAGE_SIZE);
                        pages.forEach(p -> {
                            ProductInfo info = parseInfoFromPage(p, prop);
                            if (info.infos.size() > prop.size() / 2 && info.url.indexOf("?") == -1) {//remove list pages
                                ProductInfo pi = resultMap.get(info.signature);
                                if (pi == null) {
                                    resultMap.put(info.signature, info);
                                } else if (Strings.isNullOrEmpty(pi.catPath)) {
                                    resultMap.put(info.signature, info);
                                } else if (!Strings.isNullOrEmpty(info.catPath) && info.catPath.length() > pi.catPath.length()) {
                                    resultMap.put(info.signature, info); // select the longest category path
                                }
                            }
                        });
                    }
            );

            List<String> lines = Lists.newArrayList();

            resultMap.values().forEach(p -> {
                        p.infos.forEach((k, v) -> {
                            String l = k + ":" + v;
                            System.out.println(l);
                            lines.add(l);
                        });
                        //display url and --- in console for debugging
                        System.out.println("url: " + p.url);
                        System.out.println("-----------------");
                    }
            );
            if (!Strings.isNullOrEmpty(outputFilePath)) {
                try {
                    Files.write(Paths.get(outputFilePath), lines, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private static ProductInfo parseInfoFromPage(PageInfoDO pageInfoDO, Properties prop) {

        Map<String, String> infoMap = Maps.newTreeMap();

        Document doc = Jsoup.parse(pageInfoDO.getContent());
        Elements links = doc.select("a[href]");
        Iterator<Element> linkIter = links.iterator();
        while (linkIter.hasNext()) {
            Element l = linkIter.next();
            String u = l.attr("href");
            if (pattern.matcher(u).find()) {
                continue;
            }
        }


        for (String key : prop.stringPropertyNames()) {
            Elements elements = doc.select(prop.getProperty(key));
            if (elements.size() > 0) {
                if ("AttributesTable".equals(key)) {
                    Iterator<Element> tIte = elements.iterator();
                    while (tIte.hasNext()) {
                        Element e = tIte.next();
                        String label = StringEscapeUtils.unescapeHtml4(e.child(0).html());
                        String data = StringEscapeUtils.unescapeHtml4(e.child(1).html());
                        infoMap.put(label, data);
                    }
                } else {
                    infoMap.put(key, StringEscapeUtils.unescapeHtml4(elements.first().html()));
                }
            }

        }
        String categoryPath = pageInfoDO.getUrl().split("\\?")[0].replace("http://www.sli-demo.com", "Home").replace(".html", "");
        infoMap.put(CATEGORY_PATH, categoryPath);

        ProductInfo info = new ProductInfo(categoryPath, infoMap, MapUtil.getSignature(infoMap));
        info.url = pageInfoDO.getUrl();
        return info;
    }

    static class ProductInfo {
        String catPath;
        Map<String, String> infos;
        String signature;
        String url;

        public ProductInfo(String catPath, Map<String, String> infos, String signature) {
            this.catPath = catPath;
            this.infos = infos;
            this.signature = signature;
        }
    }

}
