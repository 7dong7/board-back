package mystudy.study.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostEditDto {
/**
 *  게시글 수정 시 게시글을 조회하는 용도로 사용
 */

    // post
    private Long postId;        // 게시글 번호
    private String title;       // 제목
    private String content;     // 내용
    private LocalDateTime createdAt;    // 작성일
    private LocalDateTime updatedAt;    // 수정일
    private Integer viewCount;  // 조회수

    // member
    private Long memberId;      // 게시자 id
    private String email;    // 게시자 email
    private String nickname;    // 게시자 이름

    @QueryProjection
    public PostEditDto(Long postId, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt, Integer viewCount, Long memberId, String email, String nickname) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.viewCount = viewCount;
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
    }
}
