package io.github.seriouszyx.o2o.dao;

import io.github.seriouszyx.o2o.entity.Area;

import java.util.List;

/**
 * @author ：Yixiang Zhao
 * @date ：Created in 2019/7/18 11:23
 * @description：
 */
public interface AreaDao {

    /**
     * 列出区域列表
     * @return areaList
     */
    List<Area> queryArea();

}
