package com.kong.cc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kong.cc.dto.ComplainDto;
import com.kong.cc.dto.MemberDto;
import com.kong.cc.dto.MenuCategoryDto;
import com.kong.cc.dto.MenuDto;
import com.kong.cc.dto.StoreDto;
import com.kong.cc.repository.AlarmDslRepository;
import com.kong.cc.repository.ComplainDslRepository;
import com.kong.cc.repository.ComplainRepository;
import com.kong.cc.repository.MemberRepository;
import com.kong.cc.repository.MenuCategoryRepository;
import com.kong.cc.repository.MenuRepository;
import com.kong.cc.repository.StoreRepository;
import com.kong.cc.util.PageInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
	
	private final MenuRepository menuRepository;
	private final MenuCategoryRepository menuCategoryRepository;
	private final StoreRepository storeRepository;
	private final ComplainRepository comlainRepository;
	private final ComplainDslRepository comlainDslRepository;
	private final AlarmDslRepository alarmDslRepository;
	private final MemberRepository memberRepository;
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public List<MenuDto> selectMenu() throws Exception {
//		return menuRepository.findByMenuStatusIsNotNull().stream().map(m->m.toDto()).collect(Collectors.toList());
		return menuRepository.findByMenuStatusNot("normal").stream().map(m->m.toDto()).collect(Collectors.toList());
	}

	@Override
	public List<StoreDto> selectStoreByStoreAddress(String address) throws Exception {
		// 해당 위치 근처의 가맹점을 가져온다.
		return null;
	}

	@Override
	public List<StoreDto> selectStoreByName(String storeName) throws Exception {
		// 검색한 이름이 들어간 가맹점을 찾는다.
		return storeRepository.findByStoreNameContaining(storeName).stream().map(s->s.toDto()).collect(Collectors.toList());
	}
	
	@Override
	public List<MenuCategoryDto> selectMenuCategory() throws Exception {
		return menuCategoryRepository.findAll().stream().map(m->m.toDto()).collect(Collectors.toList());
	}
	
	@Override
	public List<MenuDto> selectMenuByCategory(Integer categoryNum) throws Exception {
		return alarmDslRepository.selectMenuByCategory(categoryNum).stream().map(m->m.toDto()).collect(Collectors.toList());
	}

	@Override
	public List<ComplainDto> complainList(PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage()-1, 10);
		//Sort.by(Sort.Direction.DESC, "complainDate")
		List<ComplainDto> complainDtoList = comlainDslRepository.findComplainListByPaging(pageRequest).stream().map(c->c.toDto()).collect(Collectors.toList());
		Long allCnt = comlainDslRepository.findComplainCount();
		
		// 페이지 계산
		Integer allPage = (int)(Math.ceil(allCnt.doubleValue()/pageRequest.getPageSize()));
		Integer startPage = (pageInfo.getCurPage()-1)/ 10*10+1;
		Integer endPage = Math.min(startPage+10-1, allPage);
		
		pageInfo.setAllPage(allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		return complainDtoList;
	}

	@Override
	public String complainWrite(ComplainDto complainDto) throws Exception {
		complainDto.setStoreCode(storeRepository.findByStoreName(complainDto.getStoreName()).getStoreCode());
		complainDto.setComplainStatus(false);
		System.out.println(complainDto);
		comlainRepository.save(complainDto.toEntity());
		return "true";
	}

	@Override
	public List<StoreDto> allStoreList() throws Exception {
		return storeRepository.findByStoreStatus("active").stream().map(s->s.toDto()).collect(Collectors.toList());
	}

	@Override
	public MemberDto checkUsername(String username) throws Exception {
		return memberRepository.findByUsername(username).get().toDto();
	}

	@Override
	public String changePassword(MemberDto memberDto) throws Exception {
		MemberDto memberBefore = memberRepository.findByUsername(memberDto.getUsername()).get().toDto();
		memberBefore.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
		memberRepository.save(memberBefore.toEntity());
		return "true";
	}
}
