package mystudy.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.comment.dto.NewCommentDto;
import mystudy.study.domain.comment.dto.ParentCommentDto;
import mystudy.study.security.CustomUserDetail;
import mystudy.study.security.oauth2.user.CustomOAuth2User;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartRequest;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping
public class PostController {

    private final PostService postService;
    private final PostQueryService postQueryService;

    // 게시글 검색 조건 페이징
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

    // 게시글 내용 보기, 댓글, 대댓글 (페이징)
    @GetMapping("/posts/{id}")
    public String getPostView(@PathVariable Long id,
                              @PageableDefault(size=20, page=0) Pageable clPageable,
                              Model model) {

        // 댓글 Pageable 생성
        Pageable commentPageable = PageRequest.of(
                Math.max(clPageable.getPageNumber() - 1, 0),
                20, // pageSize
                Sort.by("id").descending()); // pageSort

        // 게시글 내용물 가져오기, 댓글 가져오기 (페이징)
        PostViewDto postViewDto = postService.getPostView(id, commentPageable);

        List<ParentCommentDto> content = postViewDto.getCommentDtoPage().getContent();

        model.addAttribute("newComment", new NewCommentDto());
        model.addAttribute("post", postViewDto);
        return "pages/post/postView";
    }

    // 게시글 작성 : 페이지
    @GetMapping("/posts/new/post")
    public String createPost(@ModelAttribute("newPost") NewPostDto newPostDto) {
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

    // 글 수정 페이지
    @GetMapping("/posts/{id}/edit")
    public String updatePostPage(@PathVariable("id") Long postId,
                                 HttpServletRequest request,
                                 Model model) {
        // 수정 게시글 로그인 사용자의 게시글인지 확인
        HttpSession session = request.getSession(false);
        LoginSessionInfo loginSessionInfo = (LoginSessionInfo) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);

        // 수정하고자 하는 게시글 내용
        PostEditForm postEditForm = postQueryService.findByPostIdAndMemberId(postId, loginSessionInfo.getId());
        log.info("postEditForm = " + postEditForm);
        if (postEditForm == null) { // 게시글을 사용자가 작성하지 않음
            return "redirect:/posts/" + postId;
        }

        model.addAttribute("postEditForm", postEditForm);
        return "pages/post/updatePost";
    }

    // 글 수정 페이지
    @PostMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable("id") Long postId,
                             @ModelAttribute("postEditForm") PostEditForm postEditForm,
                             HttpServletRequest request) throws AccessDeniedException {
        log.info("postEditForm = " + postEditForm);

        // 수정 게시글 로그인 사용자의 게시글인지 확인
        HttpSession session = request.getSession(false);
        LoginSessionInfo loginSessionInfo = (LoginSessionInfo) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);

        postService.postEdit(postEditForm, loginSessionInfo.getId(), postId);

        return "redirect:/posts/" + postId;
    }
}
