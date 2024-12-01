package com.kong.cc.service;

import com.kong.cc.dto.AskDto;
import com.kong.cc.dto.ComplainDto;
import com.kong.cc.dto.NoticeDto;
import com.kong.cc.dto.StoreDto;
import com.kong.cc.entity.Ask;
import com.kong.cc.entity.Complain;
import com.kong.cc.entity.Notice;
import com.kong.cc.entity.Store;
import com.kong.cc.repository.AskDslRepository;
import com.kong.cc.repository.ComplainRepository;
import com.kong.cc.repository.NoticeRepository;
import com.kong.cc.repository.StoreRepository;
import com.kong.cc.util.PageInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityMainServiceImpl implements CommunityMainService {

    private final NoticeRepository noticeRepository;
    private final AskDslRepository askDslRepository;
    
    @Override
    public List<NoticeDto> noticeListMain() {
        List<Notice> noticeListMain = this.noticeRepository.findAll();

        return noticeListMain.stream().map(Notice::toDto).collect(Collectors.toList());

    }

    @Override
    public NoticeDto noticeDetailMain(Integer noticeNum) {
        Notice noticeInfoMain = this.noticeRepository.findByNoticeNum(noticeNum)
                .orElseThrow( () -> new RuntimeException("noticeNum에 해당하는 정보가 없습니다."));
        return noticeInfoMain.toDto();

    }

    @Override
    public void noticeWriteMain(NoticeDto noticeDto) {

//        Notice notice = noticeRepository.findByNoticeNum(noticeNum)
//                .orElseThrow( () -> new RuntimeException("noticeNum에 해당하는 정보가 없습니다."));

        noticeRepository.save(noticeDto.builder()
                        .noticeType(noticeDto.getNoticeType())
                        .noticeTitle(noticeDto.getNoticeTitle())
                        .noticeContent(noticeDto.getNoticeContent())
                        .build().toEntity());
    }

	@Override
	public List<AskDto> askListMain(PageInfo page) throws Exception {
		PageRequest pageRequest = PageRequest.of(page.getCurPage()-1, 10);
		List<AskDto> askDtoList = askDslRepository.findAskListByPaging(pageRequest)
				.stream().map(s -> s.toDto()).collect(Collectors.toList());
		Long allCnt = 0L;
        allCnt = askDslRepository.findAskCount();
		return askDtoList;
	}

    };