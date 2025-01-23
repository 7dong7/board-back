package mystudy.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.dto.comment.NewCommentDto;
import mystudy.study.domain.dto.comment.ParentCommentDto;
import mystudy.study.domain.dto.member.login.LoginSessionInfo;
import mystudy.study.domain.dto.post.NewPostDto;
import mystudy.study.domain.dto.post.PostDto;
import mystudy.study.domain.dto.post.PostSearchCondition;
import mystudy.study.domain.dto.post.PostViewDto;
import mystudy.study.service.post.PostService;
import mystudy.study.session.SessionConst;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("posts")
public class PostController {

    private final PostService postService;

    // 게시글 검색 조건 페이징
    @GetMapping
    public String getPostPage(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchWord", required = false) String searchWord,
            @PageableDefault(size = 20, page = 1, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable,
            Model model) {

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
    @GetMapping("/{id}")
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

    // 새로운 글 작성 페이지
    @GetMapping("/new/post")
    public String createPost(Model model) {

        model.addAttribute("newPost", new NewPostDto());
        return "pages/post/newPost";
    }

    // 새로운 게시글 작성
    @PostMapping("/new/post")
    public String createPost(@ModelAttribute("newPost") NewPostDto newPostDto,
                             HttpServletRequest request) {
        System.out.println("newPostDto = " + newPostDto);
        
        // 사용자 id값
        HttpSession session = request.getSession();
        LoginSessionInfo loginSessionInfo = (LoginSessionInfo) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
        log.info("LoginSessionInfo: {}", loginSessionInfo);

        postService.createPost(newPostDto, loginSessionInfo.getId());

        return "redirect:/posts";
    }
}
