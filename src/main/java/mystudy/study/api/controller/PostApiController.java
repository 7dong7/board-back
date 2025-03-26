package mystudy.study.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.comment.dto.ViewCommentDto;
import mystudy.study.domain.comment.service.CommentQueryService;
import mystudy.study.domain.post.dto.NewPostDto;
import mystudy.study.domain.post.dto.PostDto;
import mystudy.study.domain.post.dto.PostSearchCondition;
import mystudy.study.domain.post.dto.ViewPostDto;
import mystudy.study.domain.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;
    private final CommentQueryService commentQueryService;

    // 게시글 목록 조회 - 페이지
    @GetMapping("/api/posts")
    public ResponseEntity<Page<PostDto>> getPosts(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchWord", required = false) String searchWord,
            @PageableDefault(size = 20, page = 1, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable) {
        log.info("PostApiController getPosts searchParams: [searchType: {}, searchWord: {}]", searchType, searchWord);
        log.info("입력받은 clPageable: {}", clPageable);
        // pageable 생성
        Pageable pageable = PageRequest.of(
                Math.max(clPageable.getPageNumber() - 1, 0), // 페이지 번호
                Math.max(1, Math.min(clPageable.getPageSize(), 50)), // 1 이상, 50 이하로 페이지 크기 제한
                clPageable.getSort()
        );
        log.info("사용 pageable: {}", pageable);
        // 게시글 검색 조건
        PostSearchCondition condition = PostSearchCondition.builder()
                .searchType(searchType)
                .searchWord(searchWord)
                .build(); // builder 패턴으로 만들어봄

        // 페이징 요청
        Page<PostDto> postPage = postService.getPostPage(pageable, condition);

        return ResponseEntity.ok(postPage);
    }

    // 게시글 내용 조회 - 페이지
    @GetMapping("/api/posts/{id}")
    public ResponseEntity<ViewPostDto> getPostDetail(@PathVariable("id") Long postId,
                                                     @PageableDefault(size = 15, page = 1) Pageable clPageable) {
        log.info("PostApiController getPostDetail postId: {}", postId);
        log.info("PostApiController getPostDetail clPageable: {}", clPageable);

        // 댓글 Pageable 생성
        Pageable commentPageable = PageRequest.of(
                Math.max(clPageable.getPageNumber() - 1, 0),
                15, // pageSize
                Sort.by("id").descending()); // pageSort

        // 게시글 조회
        ViewPostDto viewPost = postService.getPostDetail(postId);

        // 댓글 & 대댓글 조회 (페이징)
        Page<ViewCommentDto> viewComment = commentQueryService.getViewComment(postId, commentPageable);

        viewPost.setViewComment(viewComment); // 댓글 담기

        return new ResponseEntity<>(viewPost, HttpStatus.OK);
    }

    // 게시글 작성 - 처리
    @PostMapping("/api/posts/new")
    public ResponseEntity<NewPostDto> createPost(@RequestBody NewPostDto newPostDto) {
        log.info("PostApiController createPost 게시글 작성처리 postDto: {}", newPostDto);

        postService.createPost(newPostDto);

        return new ResponseEntity<>(newPostDto, HttpStatus.CREATED);
    }
}
