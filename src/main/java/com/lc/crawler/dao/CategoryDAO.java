package com.lc.crawler.dao;

import com.lc.crawler.domain.CategoryDO;

import java.util.List;

/**
 * Created by Ling Cao on 2016/8/18.
 */
public interface CategoryDAO {

    void insertCategory(CategoryDO categoryDO);

    CategoryDO queryCategoryById(long categoryId, long siteId);

    List<CategoryDO> queryAllCategories(long siteId);

    void deleteAllCategories(long siteId);
}
