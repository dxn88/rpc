package com.dxn.service.impl;


import com.dxn.rpc.annotation.Remote;
import com.dxn.export.CompanyService;
import com.dxn.model.Company;

//@Service
@Remote
public class CompanyServiceImpl implements CompanyService {


    @Override
    public String getCompanyName(Company company) {
        return "服务端确认公司名字：" + company.getName();
    }
}
