package mystudy.study.controller;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.comment.NewCommentDto;
import mystudy.study.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    // 게시글에 댓글 작성하기 (comment)
    @PostMapping("/new")
    public String newComment(@ModelAttribute("newComment")NewCommentDto newCommentDto) {
        // 댓글 저장
        commentService.newComment(newCommentDto);

        // 게시글로 돌아가기
        Long postId = newCommentDto.getPostId();
        return "redirect:/posts/" + postId;
    }

    // 게시글에 댓글 작성하기 (comment)
    @PostMapping("/replies/new")
    public String newCommentReply(@ModelAttribute("newComment")NewCommentDto newCommentDto) {
        System.out.println("newCommentDto = " + newCommentDto);

        // 대댓글 저장하기
        commentService.newReply(newCommentDto);

        // 게시글로 돌아가기
        Long postId = newCommentDto.getPostId();
        return "redirect:/posts/" + postId;
    }

}
