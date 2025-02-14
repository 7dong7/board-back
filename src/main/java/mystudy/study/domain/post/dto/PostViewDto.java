package mystudy.study.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mystudy.study.domain.comment.dto.ParentCommentDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class PostViewDto { // 게시글의 내용물을 보여줄 때 사용

    private Long postId; // 게시글 번호
    private String title; // 제목 
    private String content; // 내용
    private LocalDateTime createdAt; // 작성일 
    private Integer viewCount; // 조회수 
    private Long memberId;  // 게시자 id
    private String username; // 게시자 이름

    private Page<ParentCommentDto> commentDtoPage;

    @QueryProjection
    public PostViewDto(Long postId, String title, String content, LocalDateTime createdAt, Integer viewCount, Long memberId, String username) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.memberId = memberId;
        this.username = username;
    }

    // Page<CommentDto> 추가
    public void addComments(Page<ParentCommentDto> commentDtoPage) {
        this.commentDtoPage= commentDtoPage;
    }
}
