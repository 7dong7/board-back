package mystudy.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.comment.dto.NewCommentDto;
import mystudy.study.domain.comment.dto.WriteCommentForm;
import mystudy.study.domain.comment.service.CommentQueryService;
import mystudy.study.domain.member.dto.login.LoginSessionInfo;
import mystudy.study.domain.comment.service.CommentService;
import mystudy.study.session.SessionConst;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping
public class CommentController {

    private final CommentService commentService;
    private final CommentQueryService commentQueryService;

    // 댓글 작성(comment) : 처리
    @PostMapping("/comments/{id}/new")
    public String newComment(@PathVariable("id") Long postId, // 댓글의 주인 게시글
                             @ModelAttribute("commentForm") WriteCommentForm writeCommentForm) {
        /**
         *  댓글을 작성한다
         *  1. 로그인 여부 확인 -> security setting
         *  2. 게시글 존재 여부 확인 -> 존재하지 않는 게시글에 임의적으로 댓글을 작성하려고 하는 경우 (postId 조작)
         *  3. 댓글 작성
         */
        log.info("newComment writeCommentForm: {}", writeCommentForm);

        // 게시글에 대한 댓글 작성
        try {
            commentService.writeComment(postId, writeCommentForm);
        } catch (IllegalArgumentException e) {
            return "redirect:/posts"; // 잘못된 postId 접근
        }

        return "redirect:/posts/" + postId;
    }

    // 대댓글 작성(reply) : 처리
    @PostMapping("/comments/{postId}/new/{commentId}")
    public String newReply(@PathVariable("postId") Long postId, // 댓글 게시글 번호
                           @PathVariable("commentId") Long commentId, // 작성한 대댓글의 부모댓글
                           @ModelAttribute("commentForm") WriteCommentForm writeCommentForm) {
        /**
         *  댓글을 작성한다
         *  1. 로그인 여부 확인 -> security setting
         *  2. 게시글 존재 여부 확인 -> 존재하지 않는 게시글에 임의적으로 댓글을 작성하려고 하는 경우 (postId 조작)
         *  3. 댓글 작성
         */
        log.info("newReply writeCommentForm: {}", writeCommentForm);

        // 댓글에 대한 대댓글 작성
        try {
            commentService.writeReply(postId, commentId, writeCommentForm);
        } catch (IllegalArgumentException e) {
            return "redirect:/posts"; // 잘못된 postId 접근
        }

        return "redirect:/posts/" + postId;
    }

    // 댓글 삭제(comment) : 처리
    @PostMapping("/comments/{postId}/delete/{commentId}")
    public String deleteComment(@PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId) {
        log.info("deleteComment postId: {}, commentId: {}", postId, commentId);

        // 댓글 삭제하기
        try {
            commentService.deleteComment(commentId);
        } catch (IllegalArgumentException e) { // 정상적인 이용이 아닌경우 (조작)
            return "redirect:/posts";
        }

        return "redirect:/posts/"+postId;
    }











 // =========== 삭제 예정 =========== //
    // 대댓글 작성(reply) : 처리
    @PostMapping("/comments/replies/new")
    public String newCommentReply(@ModelAttribute("newComment") NewCommentDto newCommentDto,
                                  HttpServletRequest request) {

        // 로그인 회원 정보
        HttpSession session = request.getSession(false);
        LoginSessionInfo loginSessionInfo = (LoginSessionInfo) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);

        // 대댓글 저장하기
        commentService.newReply(newCommentDto, loginSessionInfo.getId());

        // 게시글로 돌아가기
        Long postId = newCommentDto.getPostId();
        return "redirect:/posts/" + postId;
    }

}
