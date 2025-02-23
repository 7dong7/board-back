package mystudy.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.comment.dto.NewCommentDto;
import mystudy.study.domain.comment.dto.ParentCommentDto;
import mystudy.study.domain.comment.dto.WriteCommentForm;
import mystudy.study.domain.member.dto.login.LoginSessionInfo;
import mystudy.study.domain.post.dto.*;
import mystudy.study.domain.post.service.PostQueryService;
import mystudy.study.domain.post.service.PostService;
import mystudy.study.session.SessionConst;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping
public class PostController {

    private final PostService postService;
    private final PostQueryService postQueryService;


    // 게시글 작성 : 페이지
    @GetMapping("/posts/new/post")
    public String createPostPage(@ModelAttribute("newPost") NewPostDto newPostDto) {
        return "pages/post/newPost";
    }

    // 게시글 작성 : 처리
    @PostMapping("/posts/new/post")
    public String createPost(@ModelAttribute("newPost") NewPostDto newPostDto,
                             HttpServletRequest request) {
        log.info("createPost newPostDto: {}", newPostDto);
// createPost newPostDto: NewPostDto(title=ㅁㄴㅇ, content=<p>ㅁㄴㅇ<img src="/upload/images/be9cf97f-0d31-47f2-948d-e3518eca7272.png"></p>)

        // 게시글 작성
        postService.createPost(newPostDto);

        return "redirect:/posts";
    }

    // 게시글 조회 : 페이지 (게시글 내용, 댓글&대댓글(페이징))
    @GetMapping("/posts/{id}")
    public String getPostView(@PathVariable("id") Long postId,
                              @PageableDefault(size=20, page=0) Pageable clPageable,
                              Model model) {
        // 댓글 Pageable 생성
        Pageable commentPageable = PageRequest.of(
                Math.max(clPageable.getPageNumber() - 1, 0),
                20, // pageSize
                Sort.by("id").descending()); // pageSort

        // 게시글 조회 (postId 사용)
        ViewPostDto viewPost = postService.getViewPost(postId);

        model.addAttribute("post", viewPost);

        // 댓글 & 대댓글 가져오기




        // 게시글 내용물 가져오기, 댓글 가져오기 (페이징)
//        PostViewDto postViewDto = postService.getPostView(postId, commentPageable);
//
//        List<ParentCommentDto> content = postViewDto.getCommentDtoPage().getContent();

        model.addAttribute("commentForm", new WriteCommentForm());
        return "pages/post/postView";
    }

    // 게시글 수정 : 페이지
    @GetMapping("/posts/{id}/edit")
    public String postEditPage(@PathVariable("id") Long postId,
                               HttpServletResponse response,
                               Model model) {

        // 게시글 조회 (postId 사용)
        PostEditDto postEditDto = postService.viewPostEdit(postId);

        // 로그인한 회원 정보
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 게시글 작성자와 로그인한 회원 일치 여부 확인
        if (email.equals(postEditDto.getEmail())) { // 일치하는 경우
            log.info("postEditPage postEditDto: {}", postEditDto);
            model.addAttribute("post", postEditDto);
            return "pages/post/postEdit";
        }

        // 일치하지 않는 경우
        return "redirect:/posts/" + postId;
    }

    // 게시글 수정 : 처리
    @PostMapping("/posts/{id}/edit")
    public String postEdit(@PathVariable("id") Long postId,
                           @ModelAttribute("postEditDto") PostEditDto postEditDto) throws AccessDeniedException {
        log.info("postEdit postEditDto: {}", postEditDto);

        // 게시글 수정
        try {
            postService.postEdit(postEditDto);
        } catch (AccessDeniedException e) { // 로그인 회원와 게시글 주인이 다른 경우
            return "redirect:/posts";
        }
        return "redirect:/posts/" + postId;
    }

    // 게시글 삭제 : 처리
    @PostMapping("/posts/{id}/delete")
    public String postDelete(@PathVariable("id") Long postId) {
        log.info("postDelete postId: {}", postId);
        
        // 게시글 삭제
        try {
            postService.deletePost(postId);
        } catch (Exception e) {
            // 잘못된 삭제 요청
            return "redirect:/posts";
        }
        return "redirect:/posts";
    }

    // 게시글 목록 : 페이지
    @GetMapping("/posts")
    public String getPostPage(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchWord", required = false) String searchWord,
            @PageableDefault(size = 20, page = 1, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable,
            Model model,
            HttpServletRequest request) {

        // pageable 생성
        Pageable pageable = PageRequest.of(
                Math.max(clPageable.getPageNumber()-1, 0),
                Math.max(1, Math.min(clPageable.getPageSize(), 50)), // 1 이상, 50 이하로 페이지 크기 제한
                clPageable.getSort()
        );

        // 게시글 검색 조건
        PostSearchCondition condition = PostSearchCondition.builder()
                .searchType(searchType)
                .searchWord(searchWord)
                .build(); // builder 패턴으로 만들어봄
        /*
            필드의 개수가 4개보다 적고,
            필드의 변경 가능성이 없는 경우라면
            생성자, 정적 팩토리 메소드가 좋을 수 있다
        * */

        // 페이징 요청
        Page<PostDto> postPage = postService.getPostPage(pageable, condition);

        // 검색 조건
        Map<String, Object> map = new HashMap<>();
        map.put("searchType", searchType);
        map.put("searchWord", searchWord);

        model.addAttribute("postPage", postPage);
        model.addAttribute("searchParam", map);
        return "pages/post/posts";
    }










}
