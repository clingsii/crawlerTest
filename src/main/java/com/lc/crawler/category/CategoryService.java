package com.lc.crawler.category;
import com.google.api.client.util.Maps;
import com.lc.crawler.dao.CategoryDAO;
import com.lc.crawler.domain.CategoryDO;
import com.lc.crawler.domain.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Ling Cao on 2016/8/19.
 */
public class CategoryService {

    public static final String HOME = "/Home";

    @Autowired
    private CategoryDAO categoryDAO;

    /**
     * build category path, which will be used in the final result
     *
     * @return
     */
    public Map<Long, String> getCategoryPathMap() {
        List<CategoryDO> cats = categoryDAO.queryAllCategories(Constants.SITE_ID);
        Map<Long, CategoryDO> categoryDOMap = cats.stream().collect(Collectors.toMap(CategoryDO::getCatId,
                Function.identity()));
        Map<Long, String> catPathMap = Maps.newHashMap();


        cats.stream()
                .filter(c -> c.getCatId() == Constants.ROOT_CAT_ID)
                .forEach(c -> catPathMap.put(c.getCatId(), getCatName(c, categoryDOMap) + HOME));
        return catPathMap;
    }

    private static String getCatName(CategoryDO c, Map<Long, CategoryDO> categoryDOMap) {
        return c.getParent_id() == Constants.ROOT_CAT_ID ?
                c.getName() :
                c.getName() + "/" + getCatName(categoryDOMap.get(c.getParent_id()), categoryDOMap);
    }
}
