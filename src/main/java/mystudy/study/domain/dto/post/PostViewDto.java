package mystudy.study.domain.dto.post;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mystudy.study.domain.dto.comment.CommentDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostViewDto { // 게시글의 내용물을 보여줄 때 사용

    private Long postId; // 게시글 번호
    private String title; // 제목 
    private String content; // 내용
    private LocalDateTime createdAt; // 작성일 
    private Integer viewCount; // 조회수 
    private Long memberId;  // 게시자 id
    private String username; // 게시자 이름

    private Page<CommentDto> commentDtoList;

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
    public void addComments(Page<CommentDto> commentDtoList) {
        this.commentDtoList = commentDtoList;
    }
}
