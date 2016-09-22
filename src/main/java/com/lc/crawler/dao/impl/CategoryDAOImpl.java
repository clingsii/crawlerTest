package com.lc.crawler.dao.impl;

import com.lc.crawler.dao.BaseDao;
import com.lc.crawler.dao.CategoryDAO;
import com.lc.crawler.domain.CategoryDO;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Ling Cao on 2016/8/18.
 */
public class CategoryDAOImpl extends BaseDao<CategoryDO> implements CategoryDAO {
    @Override
    public void insertCategory(CategoryDO categoryDO) {
        sqlSession.insert("insert-category", categoryDO);
    }

    @Override
    public CategoryDO queryCategoryById(long categoryId, long siteId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("cat_id", categoryId);
        map.put("site_id", siteId);
        return sqlSession.selectOne("query-category-by-id", map);
    }

    @Override
    public List<CategoryDO> queryAllCategories(long siteId) {
        return sqlSession.selectList("query-all-categories", siteId);
    }

    @Override
    public void deleteAllCategories(long siteId) {
        sqlSession.delete("delete-all-category", siteId);
    }
}
