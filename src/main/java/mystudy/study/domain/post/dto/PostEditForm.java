package mystudy.study.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class PostEditForm {

    private Long postId; // 게시글 번호
    private String title; // 제목
    private String content; // 내용
    private LocalDateTime createdAt; // 작성일
    private LocalDateTime updatedAt; // 수정일
    private Integer viewCount; // 조회수
    private Long memberId; // 게시자 id
    private String username; // 게시자 이름

    @QueryProjection
    public PostEditForm(Long postId, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt, Integer viewCount, Long memberId, String username) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.viewCount = viewCount;
        this.memberId = memberId;
        this.username = username;
    }
}
