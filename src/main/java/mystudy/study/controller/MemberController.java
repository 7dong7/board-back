package mystudy.study.controller;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.MemberSearchCondition;
import mystudy.study.domain.dto.SearchMemberDto;
import mystudy.study.domain.entity.Member;
import mystudy.study.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.StringUtils.hasText;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;


    // 사용자 검색
    @GetMapping
    public String getMemberPage(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchWord", required = false) String searchWord,
            Model model,
            @PageableDefault(size = 5, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable) {

        // pageable 생성
        Pageable pageable = PageRequest.of(
                clPageable.getPageNumber(),
                Math.max(1, Math.min(clPageable.getPageSize(), 50)), // 1 이상, 50 이하로 페이지 크기 제한
                clPageable.getSort() // default 정렬 @PageableDefault 어노테이션으로 설정
        );

//        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        System.out.println("pageable.getPageNumber() = " + pageable.getPageNumber());
//        System.out.println("pageable.getPageSize() = " + pageable.getPageSize());
//        System.out.println("pageable.getOffset() = " + pageable.getOffset());
//        System.out.println("pageable.getSort() = " + pageable.getSort());
//        System.out.println("searchType = " + searchType);
//        System.out.println("searchWord = " + searchWord);

        // 사용자 검색 조건
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setSearchType(searchType);
        condition.setSearchWord(searchWord);

        // 사용자 검색
        Page<SearchMemberDto> memberList = memberService.getMemberPage(condition, pageable);

//        for (SearchMemberDto memberDto : memberList) {
//            System.out.println("memberDto = " + memberDto);
//        }

        Map<String, Object> map = new HashMap<>();
        map.put("searchType", searchType);
        map.put("searchWord", searchWord);

        // model 속성 추가
        model.addAttribute("memberList", memberList);
        model.addAttribute("searchParam", map);
        return "member/members";
    }

}
