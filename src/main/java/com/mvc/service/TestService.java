package com.mvc.service;

import com.mvc.annotations.Autowired;
import com.mvc.annotations.Service;
import com.mvc.dao.impl.TestDaoImpl;

/**
 * Created by Administrator on 2016/9/13.
 */
@Service
public class TestService {

    @Autowired("testDao")
    private TestDaoImpl testDao;
    public String findAll(){
        return this.testDao.findAll();
    }
}
