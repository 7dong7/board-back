package mystudy.study.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.comment.dto.CreateCommentForm;
import mystudy.study.domain.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentApiController {

    private final CommentService commentService;

    // 댓글 작성 이벤트
    @PostMapping("/api/comments/new")
    public ResponseEntity<String> createComment(@RequestBody CreateCommentForm createCommentForm) {
        log.info("CommentApiController createCommentForm: {}", createCommentForm);

        // 게시글에 대한 댓글 작성
        try {
            commentService.createComment(createCommentForm);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 잘못된 접근 & 악의적 접근
        }
        
        return new ResponseEntity<>("댓글이 성공적으로 작성되었습니다.", HttpStatus.OK);
    }

}
