package mystudy.study.controller;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.comment.CommentDto;
import mystudy.study.domain.dto.member.MemberInfoDto;
import mystudy.study.domain.dto.member.MemberSearchCondition;
import mystudy.study.domain.dto.member.SearchMemberDto;
import mystudy.study.domain.dto.post.PostDto;
import mystudy.study.repository.PostRepository;
import mystudy.study.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            @PageableDefault(size = 5, page = 1, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable) {

        // pageable 생성
        Pageable pageable = PageRequest.of(
                Math.max(clPageable.getPageNumber()-1, 0),
                Math.max(1, Math.min(clPageable.getPageSize(), 50)), // 1 이상, 50 이하로 페이지 크기 제한
                clPageable.getSort() // default 정렬 @PageableDefault 어노테이션으로 설정
        );

        // 사용자 검색 조건
        MemberSearchCondition condition = MemberSearchCondition.builder()
                .searchType(searchType)
                .searchWord(searchWord)
                .build();

        // 사용자 검색
        Page<SearchMemberDto> memberList = memberService.getMemberPage(condition, pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("searchType", searchType);
        map.put("searchWord", searchWord);

        // model 속성 추가
        model.addAttribute("memberList", memberList);
        model.addAttribute("searchParam", map);
        return "member/members";
    }


    // 사용자의 정보와 게시글을 확인 페이지
    @GetMapping("{id}")
    public String getMemberInfoAndPosts(@PathVariable("id") Long id,
            @RequestParam(defaultValue = "1") int postPage,
            @RequestParam(defaultValue = "id") String ps,
            @RequestParam(defaultValue = "DESC") String pd,
            @RequestParam(defaultValue = "1") int commentPage,
            @RequestParam(defaultValue = "id") String cs,
            @RequestParam(defaultValue = "DESC") String cd,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable,
            Model model) {

        System.out.println("ps = " + ps + ", pd = " + pd + ", cs = " + cs + ", cd = " + cd);
        model.addAttribute("ps", ps);
        model.addAttribute("pd", pd);

        // postPageable 생성
        Pageable postPageable = PageRequest.of(
                Math.max(postPage-1, 0), // 페이지 최소 0 지정  -1->0페이지 0->0페이지, 1->0페이지 2->1페이지
                Math.max(1, Math.min(clPageable.getPageSize(), 50)),
                clPageable.getSort()
        );

        // commentPageable 생성
        Pageable commentPageable = PageRequest.of(
                Math.max(commentPage-1, 0),
                Math.max(1, Math.min(clPageable.getPageSize(), 50)),
                clPageable.getSort()
        );

        // 사용자 정보 / 게시글 수 / 댓글 수 / 게시글 페이징 / 댓글 페이징 가져오기
        MemberInfoDto memberInfo = memberService.getMemberInfo(id, postPageable, commentPageable);

        Page<PostDto> postPage1 = memberInfo.getPostPage();

        model.addAttribute("memberInfo", memberInfo);
        return "member/memberAndPosts";
    }

}
