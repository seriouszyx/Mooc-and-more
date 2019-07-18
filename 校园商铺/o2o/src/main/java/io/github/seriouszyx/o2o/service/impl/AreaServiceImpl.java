package io.github.seriouszyx.o2o.service.impl;

import io.github.seriouszyx.o2o.dao.AreaDao;
import io.github.seriouszyx.o2o.entity.Area;
import io.github.seriouszyx.o2o.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：Yixiang Zhao
 * @date ：Created in 2019/7/18 16:58
 * @description：
 */
@Service
public class AreaServiceImpl implements AreaService {

    @Autowired
    private AreaDao areaDao;

    @Override
    public List<Area> getAreaList() {
        return areaDao.queryArea();
    }
}
