//package com.dxn;
//
//import com.dxn.rpc.ClientApplication;
//import com.dxn.rpc.annotation.Invoke;
//import com.dxn.rpc.client.Client;
//import com.dxn.export.CompanyService;
//import com.dxn.model.Company;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * Unit test for simple App.
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {ClientApplication.class})
//public class AppTest {
//
//    @Invoke
//    private CompanyService companyService;
//
//    @Test
//    public void test() {
//        Company company = new Company();
//        company.setAddress("大北京东三环1");
//        company.setName("云深不知处1");
//        String companyName = companyService.getCompanyName(company);
//        System.out.println("companyName = " + companyName);
//        Client.close();
//    }
//}
