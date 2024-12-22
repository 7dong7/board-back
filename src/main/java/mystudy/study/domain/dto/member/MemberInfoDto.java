package mystudy.study.domain.dto.member;

import lombok.*;
import mystudy.study.domain.dto.comment.CommentDto;
import mystudy.study.domain.dto.post.PostDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class MemberInfoDto {
    // member 엔티티
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    // post 엔티티
    private Long postCount; // 게시글 수
    // comment 엔티티
    private Long commentCount; // 댓글 수

    // 사용자의 게시글
    private Page<PostDto> postPage;
    // 사용자의 댓글
    private Page<CommentDto> commentPage;

}
