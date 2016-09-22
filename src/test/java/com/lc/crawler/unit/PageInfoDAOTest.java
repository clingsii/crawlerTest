package com.lc.crawler.unit;

import com.lc.crawler.BaseTest;
import com.lc.crawler.dao.PageInfoDAO;
import com.lc.crawler.domain.PageInfoDO;
import com.lc.crawler.domain.PageType;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Ling Cao on 2016/8/16.
 */
public class PageInfoDAOTest extends BaseTest {

    @Autowired
    private PageInfoDAO pageInfoDAO;

    @Ignore
    public void simpleTest() {
//        String url = "http://www.sli-demo.com/abcdeg";
//
//        PageInfoDO pageInfoDO = pageInfoDao.findPageInfoByUrl(url);
//        assertNull(pageInfoDO);
//
//        PageInfoDO newPage = new PageInfoDO();
//        newPage.setUrl(url);
//        newPage.setContent("abdcsagas");
//        newPage.setType((byte) 1);
//        newPage.setChecksum("aadsgdsa");
//
//        pageInfoDaoImpl.insertPageInfo(newPage);

        String url = "http://www.sli-demo.com/accessories.html";

        PageInfoDO pageInfoDO = pageInfoDAO.findPageInfoByUrl(url);
        assertNotNull(pageInfoDO);
//        System.out.println(pageInfoDO.getContent());
    }

    @Ignore
    public void testCount() {
        int cnt = pageInfoDAO.countPageInfo(null);
        assertTrue(cnt > 0);

        PageInfoDO pageInfoDO = new PageInfoDO();
        pageInfoDO.setType(PageType.DETAIL.getType());
        pageInfoDO.setSiteId(1);
        int cnt1 = pageInfoDAO.countPageInfo(pageInfoDO);
        assertTrue(cnt1 > 0);
        assertTrue(cnt > cnt1);


        int pageNo = 1;
        int pageSize = 20;
        List<PageInfoDO> pageInfoDOList = pageInfoDAO.queryPageInfo(null, pageNo, pageSize);
        assertNotNull(pageInfoDOList);
        assertTrue(pageInfoDOList.size() == pageSize);




        List<PageInfoDO> pageInfoDOList1 = pageInfoDAO.queryPageInfo(pageInfoDO, pageNo, pageSize);
        assertNotNull(pageInfoDOList1);
        assertTrue(pageInfoDOList1.size() == pageSize);

        pageInfoDOList1.forEach((p) -> System.out.println(p.getUrl()));
    }



    @Test
    public void testUpdate() {
        String url = "http://www.sli-demo.com/accessories.html";
        PageInfoDO pageInfoDO = pageInfoDAO.findPageInfoByUrl(url);
        assertNotNull(pageInfoDO);

        pageInfoDO.setFeatures("abc");
        pageInfoDO.setmContent("xxx");
        pageInfoDAO.updatePageInfo(pageInfoDO);

        pageInfoDO = pageInfoDAO.findPageInfoByUrl(url);
        assertNotNull(pageInfoDO);
        assertEquals(pageInfoDO.getFeatures(), "abc");
        assertEquals(pageInfoDO.getmContent(), "xxx");
    }
}
