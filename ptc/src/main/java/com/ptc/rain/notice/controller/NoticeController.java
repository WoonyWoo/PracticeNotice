package com.ptc.rain.notice.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ptc.rain.notice.dto.NoticeCd;
import com.ptc.rain.notice.dto.NoticeDto;
import com.ptc.rain.notice.dto.PageList;
import com.ptc.rain.notice.dto.PagingDto;
import com.ptc.rain.notice.dto.ResultDto;
import com.ptc.rain.notice.service.NoticeService;

@Controller
public class NoticeController {
	
	private static final Logger Log = LoggerFactory.getLogger(NoticeController.class);
	
	@Autowired
	private NoticeService noticeService;
	
	// 게시글 등록 화면 이동
	@RequestMapping(value = "/regist", method = RequestMethod.GET)
	public ModelAndView moveRegist() throws Exception{
		
		ModelAndView mv = new ModelAndView("layout/noticeRegist");
		
		
		return mv;
	}
	
	// 게시글 목록 화면 이동
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView moveList(@RequestBody(required = false) NoticeDto nd) throws Exception{
		
		ModelAndView mv = new ModelAndView("layout/noticeList");
		
		List<NoticeDto> list = noticeService.selectNoticeList(nd);
		
		
		mv.addObject("list", list);
		
		return mv;
	}
	
	// 게시글 상세 화면 이동
	/*@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public ModelAndView moveDetail(@RequestParam int notiNo, ModelAndView mv) throws Exception{
		
		mv = new ModelAndView("layout/noticeDetail");
		
		NoticeDto result = noticeService.selectNoticeOne(notiNo);
		
		mv.addObject("notice", result);
		
		return mv;
	}*/
	
	// 게시글 상세 화면 이동
	@RequestMapping(value = "/detail")
	public ModelAndView test(@RequestParam int notiNo, @RequestParam("page") Optional<Integer> page) throws Exception{
		
		ModelAndView mv = new ModelAndView("layout/noticeDetail");
		
		NoticeDto result = noticeService.selectNoticeOne(notiNo);
		
		int currentPage = page.orElse(1);
		
		mv.addObject("notice", result);
		mv.addObject("page", currentPage);
		
		return mv;
	}
	
	// 게시글 등록
	@RequestMapping(value = "/registNotice", method = RequestMethod.POST)
	@ResponseBody
	public void noticeRegist(@RequestBody NoticeDto notice) throws Exception{
		
		noticeService.insertNotice(notice);
		
	}
	
	// 게시글 수정
	@RequestMapping(value = "/updateNotice", method = RequestMethod.POST)
	@ResponseBody
	public void noticeUpdate(@RequestBody NoticeDto notice) throws Exception{
		
		noticeService.updateNotice(notice);
		
	}
	
	// 게시글 삭제
	@RequestMapping(value = "/deleteNotice", method = RequestMethod.POST)
	@ResponseBody
	public void noticeDelete(@RequestBody NoticeDto notice) throws Exception{
		
		noticeService.deleteNotice(notice.getNotiNo());
		
	}
	
	// 게시글 목록 조회(페이징)
	@RequestMapping(value = "/listPage", method = RequestMethod.GET)
	public ModelAndView selectNoticeList(NoticeDto notice) throws Exception{
		
		ModelAndView mv = new ModelAndView("layout/noticeList");
		
		PagingDto pg = new PagingDto();
		
		if(notice == null) {
			pg.setPerPage(5);
			pg.setCurrentPage(1);
		}
		
		pg.setPerPage(5); 
		pg.setCurrentPage(1);
		
		NoticeDto noticeDto = new NoticeDto();
		noticeDto.setPaging(pg);

		PageList<NoticeDto> res = noticeService.selectNoticePageList(noticeDto);
		
		mv.addObject("list", res.getItemList());
		mv.addObject("totalCount", pg.getFinalPageNo());
		
		return mv;
	}
	
	// 게시글 목록 조회(페이징)
	@RequestMapping(value = "/listNotices", method = RequestMethod.GET)
	public ModelAndView listNotices(@RequestBody(required = false) NoticeDto nd, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, ModelAndView mv) throws Exception {
		
		// orElse: Optional 클래스의 기능, 값이 null이면 대체 값으로 넣음
		int currentPage = page.orElse(1); // 현재 페이지(Default) : 1 페이지
		int pageSize = size.orElse(5); // 페이지당 게시물 수(Default) : 5 개
		
		// noticePage 정보: content(데이터 목록), number(현재 페이지), size(총 데이터 갯수), sort(정렬 정보), totalPages(총 페이지 수)
		Page<NoticeDto> noticePage = noticeService.findPaginated(PageRequest.of(currentPage - 1, pageSize), nd); // -1을 하는 이유는 index가 0부터 시작하기 때문
		
		mv.addObject("noticePage", noticePage); // 게시물 리스트 데이터
		// mv.addObject("currentPage", currentPage);
		
		int totalPages = noticePage.getTotalPages();
		if (totalPages > 0) {
			// IntStream.boxed().collect(Collectors.toList()) - IntStream을 List로 변환
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages) // IntStream.rangeClosed(start, end): start부터 end까지 차례대로 반복 
					.boxed() // int 자체로는 Collection에 못 담기 때문에 Integer 클래스로 변환하여 List<Integer> 클래스로 담기 위해 사용(int -> Integer)
					.collect(Collectors.toList()); // Collection을 List로 변환
			mv.addObject("pageNumbers", pageNumbers); 
		}
		
		mv.setViewName("layout/noticeListPage"); // 경로 설정
		
		return mv;
	}

}
