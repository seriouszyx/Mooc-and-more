package io.github.seriouszyx.o2o.service;

import io.github.seriouszyx.o2o.BaseTest;
import io.github.seriouszyx.o2o.entity.Area;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author ：Yixiang Zhao
 * @date ：Created in 2019/7/18 17:01
 * @description：
 */
public class AreaServiceTest extends BaseTest {

    @Autowired
    private AreaService areaService;

    @Test
    public void testGetAreaList() {
        List<Area> areaList = areaService.getAreaList();
        assertEquals("西苑", areaList.get(0).getAreaName());
    }

}
