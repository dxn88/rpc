package com.dxn.bussiness;

import com.dxn.export.CompanyService;
import com.dxn.model.Company;
import com.dxn.rpc.annotation.Invoke;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ClientCompanyService {

    @Invoke
    private CompanyService companyService;

    public String test() {
        Company company = new Company();
        company.setAddress("大北京东三环");
        company.setName("云深不知处");
        String companyName = companyService.getCompanyName(company);
        return companyName;
    }
}
