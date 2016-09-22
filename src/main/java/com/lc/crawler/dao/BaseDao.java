package com.lc.crawler.dao;

import org.mybatis.spring.SqlSessionTemplate;

import java.io.Serializable;

/**
 * Created by Ling Cao on 2016/8/16.
 */
public class BaseDao<T> {



    protected SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }



}
