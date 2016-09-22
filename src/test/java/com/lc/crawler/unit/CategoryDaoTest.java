package com.lc.crawler.unit;

import com.lc.crawler.dao.CategoryDAO;
import com.lc.crawler.BaseTest;
import com.lc.crawler.domain.CategoryDO;
import com.lc.crawler.domain.Constants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Ling Cao on 2016/8/18.
 */
public class CategoryDaoTest extends BaseTest {

    @Autowired
    private CategoryDAO categoryDAO;

    @Test
    public void crud() {
        categoryDAO.deleteAllCategories(Constants.SITE_ID);

        CategoryDO categoryDO = new CategoryDO();
        categoryDO.setSite_id(Constants.SITE_ID);
        categoryDO.setCatId(1);
        categoryDO.setName("a");
        categoryDO.setParent_id(1);

        categoryDAO.insertCategory(categoryDO);

        categoryDO = categoryDAO.queryCategoryById(1, Constants.SITE_ID);
        assertNotNull(categoryDO);


        categoryDO = new CategoryDO();
        categoryDO.setSite_id(Constants.SITE_ID);
        categoryDO.setCatId(2);
        categoryDO.setName("b");
        categoryDO.setParent_id(1);

        categoryDAO.insertCategory(categoryDO);


        categoryDO = new CategoryDO();
        categoryDO.setSite_id(Constants.SITE_ID);
        categoryDO.setCatId(3);
        categoryDO.setName("c");
        categoryDO.setParent_id(1);

        categoryDAO.insertCategory(categoryDO);

        List<CategoryDO> cats = categoryDAO.queryAllCategories(Constants.SITE_ID);
        assertNotNull(categoryDO);
        assertEquals(cats.size(), 3);

        categoryDAO.deleteAllCategories(Constants.SITE_ID);
    }

}
