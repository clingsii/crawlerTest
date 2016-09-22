package com.lc.crawler.dao.impl;

import com.lc.crawler.dao.BaseDao;
import com.lc.crawler.domain.PageInfoDO;
import com.lc.crawler.dao.PageInfoDAO;

import java.util.HashMap;
import java.util.List;


/**
 * Created by Ling Cao on 2016/8/16.
 */
public class PageInfoDAOImpl extends BaseDao<PageInfoDO> implements PageInfoDAO {
    @Override
    public void insertPageInfo(PageInfoDO pageInfoDO) {
        sqlSession.insert("insert-pageInfo", pageInfoDO);
    }

    @Override
    public int countPageInfo(PageInfoDO pageInfoDO) {
        return sqlSession.selectOne("count-pageInfo", pageInfoDO);
    }

    @Override
    public PageInfoDO findPageInfoByUrl(String url) {
        return sqlSession.selectOne("find-pageInfo-by-url", url);
    }

    @Override
    public List<PageInfoDO> queryPageInfo(PageInfoDO pageInfoDO, int pageNo, int pageSize) {
        if (pageNo <= 0) {
            throw new IllegalArgumentException("pageNo should be greater than 0");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize should be greater than 0");
        }
        int start = (pageNo - 1) * pageSize;
        HashMap<String, Object> map = new HashMap<>();
        map.put("start", start);
        map.put("limit", pageSize);
        if (pageInfoDO != null) {
            map.put("type", pageInfoDO.getType());
            map.put("siteId", pageInfoDO.getSiteId());
        }
        return sqlSession.selectList("query-page-info", map);
    }

    @Override
    public void updatePageInfo(PageInfoDO pageInfoDO) {
        sqlSession.update("update-pageInfo", pageInfoDO);
    }
}
