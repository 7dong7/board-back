package mystudy.study.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.comment.dto.CommentDto;
import mystudy.study.domain.member.dto.*;
import mystudy.study.domain.member.service.MemberQueryService;
import mystudy.study.domain.member.service.MemberService;
import mystudy.study.domain.post.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping
public class MemberController {

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;

    // 회원 가입 : 페이지
    @GetMapping("/members/new")
    public String newMemberForm(@ModelAttribute("memberForm") RegisterMemberForm memberForm) {
        return "pages/member/memberRegister";
    }

    // 회원 가입 : 기능
    @PostMapping("/members/new")
    public String saveMember(@Validated @ModelAttribute("memberForm") RegisterMemberForm memberForm, BindingResult bindingResult) {
        log.info("memberForm = {}", memberForm);
        log.info("bindingResult = {}", bindingResult);

        // 비밀번호 다시 입력 검증
        if (!memberForm.getPassword().equals(memberForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword","confirmPassword.noMatch", "비밀번호가 같지 않습니다.");
        }

        // 필드 에러가 담겨 있는 경우 다시 회원가입 페이지로
        if (bindingResult.hasErrors()) {
            return "pages/member/memberRegister";
        }

        // 회원 가입 서비스
        try {
            memberService.registerMember(memberForm);
            return "redirect:/posts";
        } catch (IllegalArgumentException e) { // 이미 회원이 존재하는 경우
            bindingResult.reject("existMember","이미 사용중인 이메일입니다");
            return "pages/member/memberRegister";
        }
    }

    // 사용자 정보 조회 (아무나) : 페이지
    @GetMapping("/members/{id}")
    public String getMemberInfoAndPosts(@PathVariable("id") Long memberId,
                                        @RequestParam(defaultValue = "1") int postPage, // 게시글 페이지 번호
                                        @RequestParam(defaultValue = "id") String ps,   // 게시글 정렬 조건
                                        @RequestParam(defaultValue = "DESC") String pd, // 정렬 방향 (내림차순, 오름차순)
                                        @RequestParam(defaultValue = "1") int commentPage,  // 댓글 페이지 번호
                                        @RequestParam(defaultValue = "id") String cs,       // 댓글 정렬 조건 
                                        @RequestParam(defaultValue = "DESC") String cd,     // 정렬 방향
                                        @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable, // client 요청 pageable
                                        Model model) {
    // 검색 조건 검증
        Set<String> ALLOWED_POST = Set.of("id", "title", "viewCount", "nickname", "createdAt");
        Set<String> ALLOWED_COMMENT = Set.of("id", "content", "nickname", "createdAt");

        if(!ALLOWED_POST.contains(ps)) {
            ps = "id";
        }

        if(!ALLOWED_COMMENT.contains(cs)) {
            cs = "id";
        }

    // 사용자 정보 조회
        try {
            InfoMemberDto InfoMemberDto = memberService.getInfoMemberDto(memberId);
            // 사용자 정보 담기
            model.addAttribute("member", InfoMemberDto);
        } catch (IllegalArgumentException e) { // 존재하지 않은 사용자
            log.info("잘못된 사용자 memberId = {}", memberId);
            return "redirect:/posts";
        }
        
    // 작성한 게시글 페이징
        // post Pageable 생성
        Pageable postPageable = PageRequest.of(
                // 페이지 최솟값 0 지정1
                Math.max(postPage - 1, 0), // 페이지 최소 0 지정  -1->0페이지 0->0페이지, 1->0페이지 2->1페이지
                Math.max(1, Math.min(clPageable.getPageSize(), 50)), // 페이지 사이즈
//                clPageable.getSort()
                pd.equals("ASC") ? Sort.by(ps).ascending() : Sort.by(ps).descending() // 정렬 조건
        );
        
        // 게시글 페이징
        Page<PostDto> pagePost = memberService.getMemberPosts(memberId, postPageable);
        // 사용자 게시글 담기
        model.addAttribute("pagePost", pagePost);

    // 작성 댓글 페이징

        // comment Pageable 생성
        Pageable commentPageable = PageRequest.of(
                Math.max(commentPage-1, 0),
                Math.max(1, Math.min(clPageable.getPageSize(), 50)),
//                clPageable.getSort()
                cd.equals("ASC") ? Sort.by(cs).ascending() : Sort.by(cs).descending()
        );
        // 댓글 페이징
        Page<CommentDto> PageComment = memberService.getMemberComments(memberId, commentPageable);
        // 사용자 댓글 담기
        model.addAttribute("pageComment", PageComment);

        // 정렬 조건 담기
        model.addAttribute("ps", ps); // 게시글 정렬 조건
        model.addAttribute("pd", pd); // 게시글 저렬 방향
        model.addAttribute("cs", cs); // 댓글 정렬 조건
        model.addAttribute("cd", cd); // 댓글 정렬 방향
        return "pages/member/viewMember";
    }
    
    
    



    // 사용자 검색 : 페이지
    @GetMapping("/members")
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
        return "pages/member/members";
    }


    

}
