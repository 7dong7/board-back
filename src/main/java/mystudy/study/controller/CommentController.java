package mystudy.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.comment.dto.NewCommentDto;
import mystudy.study.domain.member.dto.login.LoginSessionInfo;
import mystudy.study.domain.comment.service.CommentService;
import mystudy.study.session.SessionConst;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    // 게시글에 댓글 작성 하기 (comment)
    @PostMapping("/new")
    public String newComment(@ModelAttribute("newComment")NewCommentDto newCommentDto,
                             HttpServletRequest request) {
        // 댓글 작성자
        HttpSession session = request.getSession(false);
        LoginSessionInfo loginSessionInfo = (LoginSessionInfo) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);

        // 댓글 저장
        commentService.newComment(newCommentDto, loginSessionInfo.getId());

        // 게시글로 돌아가기
        Long postId = newCommentDto.getPostId();
        return "redirect:/posts/" + postId;
    }

    // 게시글에 대댓글 작성하기 (replies)
    @PostMapping("/replies/new")
    public String newCommentReply(@ModelAttribute("newComment")NewCommentDto newCommentDto,
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
