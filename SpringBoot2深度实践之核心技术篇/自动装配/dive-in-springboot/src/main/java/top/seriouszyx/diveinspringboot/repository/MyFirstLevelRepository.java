package top.seriouszyx.diveinspringboot.repository;

import top.seriouszyx.diveinspringboot.annotation.FirstLevelRepository;
import top.seriouszyx.diveinspringboot.annotation.SecondLevelRepository;

/**
 * @author ：Yixiang Zhao
 * @date ：Created in 2019/7/11 20:19
 * @description：
 */
@SecondLevelRepository(value = "myFirstLevelRepository") // Bean 名称
public class MyFirstLevelRepository {



}
