package com.kong.cc.service;

import com.kong.cc.dto.MenuDto;
import com.kong.cc.dto.SalesDto;
import com.kong.cc.dto.SalesMenuDto;

import java.util.Date;
import java.util.List;

public interface SalesManagementService {

    List<MenuDto> menuList() throws Exception;
    void salesWrite(List<SalesDto> salsList) throws Exception;

    List<SalesMenuDto> salesAnalysis(Integer storeCode,
//                                     String periodType,
                                     Integer categoryId) throws Exception;
    List<SalesDto> salesTemp(Date salesDate, Integer storeCode) throws Exception;
//    SalesDto anualSales(Integer itemCategoryNum) throws Exception;
//    SalesDto quarterlySales(Integer itemCategoryNum) throws Exception;
//    SalesDto monthlySales(Integer itemCategoryNum) throws Exception;
//    SalesDto customSales(Integer itemCategoryNum) throws Exception;

}
