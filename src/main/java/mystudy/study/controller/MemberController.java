package mystudy.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.comment.dto.CommentDto;
import mystudy.study.domain.member.dto.search.SearchMemberInfoDto;
import mystudy.study.domain.member.dto.*;
import mystudy.study.domain.member.dto.MemberSearch;
import mystudy.study.domain.member.dto.search.MemberSearchType;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        return "pages/member/registerMember";
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

        // 날짜 검증
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            LocalDate.parse(memberForm.getFrontNum(), formatter);
        } catch (DateTimeParseException e) {
            bindingResult.rejectValue("frontNum","frontNum.noMatch", "정상적인 날짜가 아닙니다.");
        }

        // 필드 에러가 담겨 있는 경우 다시 회원가입 페이지로
        if (bindingResult.hasErrors()) {
            return "pages/member/registerMember";
        }

        // 회원 가입 서비스
        try {
            memberService.registerMember(memberForm);
            return "redirect:/posts";
        } catch (IllegalArgumentException e) { // 이미 회원이 존재하는 경우
            bindingResult.reject("existMember","이미 사용중인 이메일입니다");
            return "pages/member/registerMember";
        }
    }

    // 회원 정보 조회 (아무나) : 페이지
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

    // 회원 정보 조회
        try {
            InfoMemberDto InfoMemberDto = memberService.getInfoMemberDto(memberId);
            // 회원 정보 담기
            model.addAttribute("member", InfoMemberDto);
        } catch (IllegalArgumentException e) { // 존재하지 않은 회원
            log.info("잘못된 회원 memberId = {}", memberId);
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
        // 회원 게시글 담기
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
        // 회원 댓글 담기
        model.addAttribute("pageComment", PageComment);

        // 정렬 조건 담기
        model.addAttribute("ps", ps); // 게시글 정렬 조건
        model.addAttribute("pd", pd); // 게시글 저렬 방향
        model.addAttribute("cs", cs); // 댓글 정렬 조건
        model.addAttribute("cd", cd); // 댓글 정렬 방향
        return "pages/member/viewMember";
    }
    
    // 회원 정보 수정 (본인만) : 페이지
    @GetMapping("/members/{id}/edit")
    public String editMemberPage(@PathVariable("id") Long memberId,
                                 Model model) {
        log.info("editMemberPage memberId = {}", memberId);
        /**
         *  회원정보 수정 페이지
         *  보인의 정보만 수정 가능하게 (로그인 본인 체크)
         *  수정 가능한 정보만 수정할 수 있게
         */
        try {
            EditMemberDto memberDto = memberQueryService.getEditMemberDto(memberId);
            model.addAttribute("member", memberDto);
        } catch (IllegalArgumentException e) {
            return "redirect:/posts";
        }

        return "pages/member/editMember";
    }

    // 회원 정보 수정 (본인만) : 처리
    @PostMapping("/members/{id}/edit")
    public String editMember(@PathVariable("id") Long memberId,
                             @ModelAttribute("member") EditMemberDto memberDto) {
        /**
         *  보인의 정보만 수정 가능하게 (로그인 본인 체크)
         *  수정 가능한 정보만 수정할 수 있게
         */
        // 정보 수정 처리
        try {
            memberService.editMember(memberId, memberDto);
        } catch (IllegalArgumentException e) {
            return "redirect:/posts";
        }
        return "redirect:/members/"+memberId;
    }

    // 회원 탈퇴 : 처리
    @PostMapping("/members/{id}/delete")
    public String deleteMember(@PathVariable("id") Long memberId,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        log.info("deleteMember memberId = {}", memberId);

        try {
            memberService.deleteMember(memberId, request, response);
        } catch (IllegalArgumentException e) { // 잘못된 memberId가 넘어옴 (존재하지 않음)
            return "redirect:/posts";
        } catch (IllegalStateException e2) { // 다른 사람의 계정을 탈퇴시키려는 경우
            return "redirect:/posts";
        }

        return "redirect:/posts";
    }
    
    // 회원 검색 : 페이지
    @GetMapping("/members")
    public String getMemberPage(
            @ModelAttribute("memberSearch") MemberSearch memberSearch,
//            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchWord", required = false) String searchWord,
            @PageableDefault(size = 10, page = 1, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable,
            Model model) {
        log.info("getMemberPage memberSearch = {}", memberSearch);
        log.info("clPageable: {}", clPageable);

        // 검색 타입 검증
        String searchType = switch (memberSearch.getSearchType()) {
            case "NAME" -> MemberSearchType.NAME.name().toLowerCase();
            case "EMAIL" -> MemberSearchType.EMAIL.name().toLowerCase();
            case "NICKNAME" -> MemberSearchType.NICKNAME.name().toLowerCase();
            default -> "id";
        };

        // pageable 생성
        Pageable pageable = PageRequest.of(
                Math.max(clPageable.getPageNumber()-1, 0),
                Math.max(1, Math.min(clPageable.getPageSize(), 50)), // 1 이상, 50 이하로 페이지 크기 제한
                clPageable.getSort() // default 정렬 @PageableDefault 어노테이션으로 설정
        );

        // 검색 조건에 맞는 회원리스트 조회
        Page<SearchMemberInfoDto> memberListPage = memberQueryService.getMemberList(searchType, memberSearch, pageable);

        // 검색결과를 모델에 넣을 dto 에 넣기
        memberSearch.addSearchMembers(memberListPage);

        // 회원 검색 조건
//        MemberSearchCondition condition = MemberSearchCondition.builder()
//                .searchType(searchType)
//                .searchWord(searchWord)
//                .build();
//         회원 검색
//        Page<SearchMemberDto> memberList = memberService.getMemberPage(condition, pageable);
//         model 속성 추가
//        model.addAttribute("memberList", memberList);

        // 변경된 검색 조건과 페이징된 회원 리스트 전달
        model.addAttribute("memberSearch", memberSearch);
        return "pages/member/members";
    }

}
