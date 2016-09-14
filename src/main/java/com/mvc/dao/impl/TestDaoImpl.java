package com.mvc.dao.impl;

import com.mvc.annotations.Repository;
import com.mvc.dao.TestDao;

/**
 * Created by Administrator on 2016/9/13.
 */
@Repository
public class TestDaoImpl implements TestDao {
    private String test="aaa";
    public String findAll() {
        System.out.println("findAll");
        return "findAll";
    }
}
