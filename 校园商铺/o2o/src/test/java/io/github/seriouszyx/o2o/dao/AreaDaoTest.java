package io.github.seriouszyx.o2o.dao;

import io.github.seriouszyx.o2o.BaseTest;
import io.github.seriouszyx.o2o.entity.Area;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author ：Yixiang Zhao
 * @date ：Created in 2019/7/18 15:53
 * @description：
 */
public class AreaDaoTest extends BaseTest {

    @Autowired
    private AreaDao areaDao;

    @Test
    public void testQueryArea() {
        List<Area> areaList = areaDao.queryArea();
        assertEquals(2, areaList.size());
    }

}
