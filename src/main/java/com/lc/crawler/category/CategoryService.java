package com.lc.crawler.category;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.api.client.util.Maps;
import com.lc.crawler.dao.CategoryDAO;
import com.lc.crawler.domain.CategoryDO;
import com.lc.crawler.domain.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Ling Cao on 2016/8/19.
 */
public class CategoryService {

    public static final Joiner joiner = Joiner.on("/");
    public static final String HOME = "Home";

    @Autowired
    private CategoryDAO categoryDAO;

    /**
     * build category path, which will be used in the final result
     * @return
     */
    public Map<Long, String> getCategoryPathMap() {
        List<CategoryDO> cats = categoryDAO.queryAllCategories(Constants.SITE_ID);
        Map<Long, CategoryDO> categoryDOMap = cats.stream().collect(Collectors.toMap(CategoryDO::getCatId,
                Function.identity()));
        Map<Long, String> catPathMap = Maps.newHashMap();
        cats.forEach(c -> {
            long catId = c.getCatId();
            if (catId != Constants.ROOT_CAT_ID) {
                LinkedList<String> catNameList = new LinkedList<>();
                int cnt = 0;
                while (true) {
                    catNameList.addFirst(c.getName());
                    cnt++;
                    if (cnt >= 2) {//in case of dead iteration caused by dirty data
                        break;
                    }
                    if (c.getParent_id() == Constants.ROOT_CAT_ID) {
                        break;
                    } else {
                        c = categoryDOMap.get(c.getParent_id());
                    }
                }
                catNameList.addFirst(HOME);
                catPathMap.put(catId, joiner.join(catNameList));
            }
        });
        return catPathMap;
    }
}
