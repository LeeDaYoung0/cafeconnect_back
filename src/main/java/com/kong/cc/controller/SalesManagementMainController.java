package com.kong.cc.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kong.cc.dto.SalesDetailDto;
import com.kong.cc.service.SalesManagementMainService;

import lombok.RequiredArgsConstructor;

//매출 관리(본사)
@RestController
@RequiredArgsConstructor
public class SalesManagementMainController {

    private final SalesManagementMainService salesManagementMainService;

    // 가맹점별 상세 주문내역
    @GetMapping("/itemRevenue") // StoreItemRevenue.js
    public ResponseEntity<List<SalesDetailDto>> itemRevenue(
            @RequestParam Date startDate,
            @RequestParam Date endDate,
            @RequestParam Integer storeCode) {
        try {
            // 서비스에서 결과 가져오기
            List<SalesDetailDto> result = salesManagementMainService.itemRevenue(storeCode, startDate, endDate);
            return ResponseEntity.ok(result);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

    }};
