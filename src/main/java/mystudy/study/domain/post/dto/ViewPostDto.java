package mystudy.study.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;
import mystudy.study.domain.comment.dto.ViewCommentDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ViewPostDto {
/**
 * "/posts/{id}" 접근시에 게시글의 컨텐츠를 보여주기 위해서 사용
 */
    // post
    private Long postId;                // 게시글 번호
    private String title;               // 제목
    private String content;             // 내용
    private LocalDateTime createdAt;    // 작성일
    private Integer viewCount;          // 조회수

    // member
    private Long memberId;              // 작성자 id
    private String nickname;            // 작성자 닉네임
    private String email;

    private Page<ViewCommentDto> viewComment; // 댓글

    @QueryProjection
    public ViewPostDto(Long postId, String title, String content, LocalDateTime createdAt, Integer viewCount, Long memberId, String nickname, String email) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.memberId = memberId;
        this.nickname = nickname;
        this.email = email;
    }
}
