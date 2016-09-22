package com.lc.crawler.unit;

import com.lc.crawler.BaseTest;
import com.lc.crawler.dao.CategoryDAO;
import com.lc.crawler.domain.CategoryDO;
import com.lc.crawler.analyzer.CategoryAnalyzer;
import com.lc.crawler.category.CategoryService;
import com.lc.crawler.domain.Constants;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ling Cao on 2016/8/27.
 */
public class CategoryTest extends BaseTest {

    @Autowired
    private CategoryAnalyzer categoryAnalyzer;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private CategoryService categoryService;

    public static final long ROOT_CAT_ID = 0;

    @Ignore
    public void start() {
        categoryDAO.deleteAllCategories(Constants.SITE_ID);

        CategoryDO categoryDO = new CategoryDO();
        categoryDO.setParent_id(ROOT_CAT_ID);
        categoryDO.setName("sli root");
        categoryDO.setCatId(ROOT_CAT_ID);
        categoryDO.setSite_id(Constants.SITE_ID);
        categoryDAO.insertCategory(categoryDO);
        categoryAnalyzer.execute(ROOT_CAT_ID);
    }

    @Test
    public void catMap() {
        Map<Long, String> catPaths = categoryService.getCategoryPathMap();
        assertNotNull(catPaths);
        assertTrue(catPaths.size() > 0);
    }
}
