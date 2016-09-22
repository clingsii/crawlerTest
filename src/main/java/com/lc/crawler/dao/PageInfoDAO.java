package com.lc.crawler.dao;

import com.lc.crawler.domain.PageInfoDO;

import java.util.List;

/**
 * Created by Ling Cao on 2016/8/16.
 */
public interface PageInfoDAO {

    void insertPageInfo(PageInfoDO pageInfoDO);

    int countPageInfo(PageInfoDO pageInfoDO);

    PageInfoDO findPageInfoByUrl(String url);

    List<PageInfoDO> queryPageInfo(PageInfoDO pageInfoDO, int pageNo, int pageSize);

    void updatePageInfo(PageInfoDO pageInfoDO);
}
