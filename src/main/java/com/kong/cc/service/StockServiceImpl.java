package com.kong.cc.service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.kong.cc.dto.ItemDto;
import com.kong.cc.dto.ShopOrderDto;
import com.kong.cc.dto.StockDto;
import com.kong.cc.dto.StoreDto;
import com.kong.cc.entity.ItemMajorCategory;
import com.kong.cc.entity.ItemMiddleCategory;
import com.kong.cc.entity.ItemSubCategory;
import com.kong.cc.entity.Stock;
import com.kong.cc.entity.Store;
import com.kong.cc.repository.ItemMajorCategoryRepository;
import com.kong.cc.repository.ItemMiddleCategoryRepository;
import com.kong.cc.repository.ItemRepository;
import com.kong.cc.repository.ItemSubCategoryRepository;
import com.kong.cc.repository.ShopOrderRepository;
import com.kong.cc.repository.StockDslRepository;
import com.kong.cc.repository.StockRepository;
import com.kong.cc.repository.StoreRepository;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
	
	public final ShopOrderRepository shopOrderRepository;
	public final ItemRepository itemRepository;
	public final StoreRepository storeRepository;
	public final StockRepository stockRepository;
	public final StockDslRepository stockDslRepository;
	
	public final ItemMajorCategoryRepository itemMajorCategoryRepository;
	public final ItemMiddleCategoryRepository itemMiddleCategoryRepository;
	public final ItemSubCategoryRepository itemSubCategoryRepository;
	
	@Override
	public List<ShopOrderDto> selectOrderList(Integer storeCode) throws Exception {
		return stockDslRepository.selectOrderList(storeCode).stream().map(s->s.toDto()).collect(Collectors.toList());
	}

	@Override
	public String addStockByOrderNum(List<Integer> orderNumList) throws Exception {
		for(Integer orderNum : orderNumList) {
			// ShopOrder에서 Stock으로 변경해서 추가
			// ShopOrder 찾기
			ShopOrderDto shopOrderDto = shopOrderRepository.findById(orderNum).get().toDto();
			
			// ShopOrder를 Stock으로 바꿔서 저장
			StockDto stockDto = new StockDto();
			stockDto.setStoreCode(shopOrderDto.getStoreCode());
			stockDto.setItemCode(shopOrderDto.getItemCode());
			stockDto.setStockReceiptDate(new Date(System.currentTimeMillis())); // 입고 날짜는 행이 들어가는 날짜로
			stockDto.setStockCount(shopOrderDto.getOrderCount());
			stockRepository.save(stockDto.toEntity());
			
			// ShopOrder orderState '입고완료'로 처리
			shopOrderDto.setOrderState("입고완료");
			shopOrderRepository.save(shopOrderDto.toEntity());
		}
		
		return "true";
	}

	@Override
	public Map<String, List<StockDto>> selectStockByStoreCode(Integer storeCode) throws Exception {
		List<StockDto> stockDtoList = stockDslRepository.selectStockByStoreCode(storeCode).stream().map(s->s.toDto()).collect(Collectors.toList()); 
		Map<String, List<StockDto>> stock = new HashMap<>();
		System.out.println(stockDtoList);
		List<StockDto> newStockList = new ArrayList<>();
		
		for(StockDto stockDto : stockDtoList) {
			// 아이템 코드가 같은 행을 리스트로 묶어준다.
			if(stock.containsKey(stockDto.getItemCode())) {
				newStockList = new ArrayList<>();
				newStockList = stock.get(stockDto.getItemCode());
				newStockList.add(stockDto);
				stock.put(stockDto.getItemCode(), newStockList);
			} else {
				newStockList = new ArrayList<>();
				newStockList.add(stockDto);
				stock.put(stockDto.getItemCode(), newStockList);
			}
		}
		System.out.println(")))))))))))))");
		System.out.println(newStockList);
		
		return stock;
	}

	@Override
	public String addStock(StockDto stockDto) throws Exception {
		stockRepository.save(stockDto.toEntity());
		return "true";
	}

	@Override
	public String updateStock(StockDto stockDto) throws Exception {
		System.out.println("stockDto : " + stockDto);
		Stock stock = stockRepository.findById(stockDto.getStockNum()).get();
		System.out.println("stock : " + stock);
		// 유통기한 수정
		if(stockDto.getStockExpirationDate()!=null) {
			stock.setStockExpirationDate(stockDto.getStockExpirationDate());			
		}
		// 입고날짜 수정
		if(stockDto.getStockReceiptDate()!=null) {
			stock.setStockReceiptDate(stockDto.getStockReceiptDate());
		}
		// 수량 수정
		if(stockDto.getStockCount()!=0) {
			stock.setStockCount(stockDto.getStockCount());			
		}
		stockRepository.save(stock);
		return "true";
	}

	@Override
	public Integer deleteStock(Integer stockNum) throws Exception {
		StockDto stock = stockRepository.findById(stockNum).orElseThrow(()->new Exception("해당 재고 없음")).toDto();
		if(stock!=null) stockRepository.deleteById(stockNum);
		return stock.getStoreCode();
	}

	@Override
	public Map<String, List<StockDto>> selectStockByCategory(@ModelAttribute Map<String, String> param) throws Exception {
		Integer storeCode = Integer.parseInt(param.get("storeCode"));
		String expirationDate = param.get("expirationDate");
		System.out.println(storeCode);
		System.out.println(expirationDate);
		
		List<StockDto> stockDtoList = stockDslRepository.selectStockByCategory(storeCode, param, expirationDate).stream().map(s->s.toDto()).collect(Collectors.toList());
		Map<String, List<StockDto>> stock = new HashMap<>();
		
		List<StockDto> newStockList = new ArrayList<>();
		
		for(StockDto stockDto : stockDtoList) {
			if(stock.containsKey(stockDto.getItemCode())) {
				newStockList = new ArrayList<>();
				newStockList = stock.get(stockDto.getItemCode());
				newStockList.add(stockDto);
				stock.put(stockDto.getItemCode(), newStockList);
			} else {
				newStockList = new ArrayList<>();
				newStockList.add(stockDto);
				stock.put(stockDto.getItemCode(), newStockList);
			}
		}
		return stock;
	}

	@Override
	public Map<String, List<StockDto>> selectStockByKeyword(Integer storeCode, String keyword) throws Exception {
		// keyword가 들어있는 상품명 찾기
		List<StockDto> stockDtoList = stockDslRepository.selectStockByKeyword(storeCode, keyword).stream().map(s->s.toDto()).collect(Collectors.toList());
		
Map<String, List<StockDto>> stock = new HashMap<>();
		
		List<StockDto> newStockList = new ArrayList<>();
		
		for(StockDto stockDto : stockDtoList) {
			if(stock.containsKey(stockDto.getItemCode())) {
				newStockList = new ArrayList<>();
				newStockList = stock.get(stockDto.getItemCode());
				newStockList.add(stockDto);
				stock.put(stockDto.getItemCode(), newStockList);
			} else {
				newStockList = new ArrayList<>();
				newStockList.add(stockDto);
				stock.put(stockDto.getItemCode(), newStockList);
			}
		}
		return stock;
	}

	@Override
	public List<ItemDto> selectItemList() throws Exception {
		return itemRepository.findAll().stream().map(i->i.toDto()).collect(Collectors.toList());
	}

	@Override
	public List<ItemDto> selectItemByName(String itemName) throws Exception {
		return itemRepository.findByItemNameContaining(itemName).stream().map(i->i.toDto()).collect(Collectors.toList());
	}

	@Override
	public List<StoreDto> selectStoreByItemCode(String itemCode) throws Exception {
		List<Tuple> tupleList = stockDslRepository.selectStoreByItemCode(itemCode);
		List<StoreDto> storeDtoList = new ArrayList<>();
		for(Tuple tuple : tupleList) {
			StoreDto storeDto = tuple.get(0, Store.class).toDto();
			storeDto.setStockCount(tuple.get(1, Integer.class));
			System.out.println("========================");
			System.out.println(storeDto);
			
			storeDtoList.add(storeDto);
		}
		return storeDtoList;
	}

	@Override
	public Map<String, Object> selectCategory() throws Exception {
		List<ItemMajorCategory> majorCategoryList = itemMajorCategoryRepository.findAll();
		List<ItemMiddleCategory> middleCategoryList = itemMiddleCategoryRepository.findAll();
		List<ItemSubCategory> subCategoryList = itemSubCategoryRepository.findAll();
		
		Map<String, Object> categoryList = new HashMap<>();
		
		
		// 대분류 카테고리 원하는 정보만 가져오기
		List<Map<String, Object>> categoryMajorList = new ArrayList<>();
		for(ItemMajorCategory majorCategory : majorCategoryList) {
			Map<String, Object> mCategory = new HashMap<>();
			mCategory.put("itemCategoryNum", majorCategory.getItemCategoryNum());
			mCategory.put("itemCategoryName", majorCategory.getItemCategoryName());
			categoryMajorList.add(mCategory);
		}
		categoryList.put("major", categoryMajorList);
		
		
		// 중분류 카테고리 원하는 정보만 가져오기
		List<Map<String, Object>> categoryMiddleList = new ArrayList<>();
		for(ItemMiddleCategory middleCategory : middleCategoryList) {
			Map<String, Object> miCategory = new HashMap<>();
			miCategory.put("itemCategoryNum", middleCategory.getItemCategoryNum());
			miCategory.put("itemCategoryName", middleCategory.getItemCategoryName());
			miCategory.put("itemCategoryMajorNum", middleCategory.getItemMajorCategoryMd().getItemCategoryNum());
			categoryMiddleList.add(miCategory);
		}
		categoryList.put("middle", categoryMiddleList);
		
		// 소분류 카테고리 원하는 정보만 가져오기
		List<Map<String, Object>> categorySubList = new ArrayList<>();
		for(ItemSubCategory subCategory : subCategoryList) {
			Map<String, Object> sCategory = new HashMap<>();
			sCategory.put("itemCategoryNum", subCategory.getItemCategoryNum());
			sCategory.put("itemCategoryName", subCategory.getItemCategoryName());
			sCategory.put("itemCategoryMiddleNum", subCategory.getItemMiddleCategorySb().getItemCategoryNum());
			categorySubList.add(sCategory);
		}
		categoryList.put("sub", categorySubList);
		return categoryList;
	}
}
