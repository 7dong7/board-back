package mystudy.study.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;
import mystudy.study.domain.base.BaseEntity;
import mystudy.study.domain.comment.entity.Comment;
import mystudy.study.domain.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Post extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", updatable = false)
    private Long id;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;
    private Integer viewCount = 0;

    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.ACTIVE;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 생성자
    @Builder
    public Post(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    // 게시글 조회수 증가
    public void increaseViewCount() {
        viewCount++;
    }

    // 게시글 내용 수정
    public void updateContent(String content) {
        this.content = content;
    }

    // 게시글 삭제
    public void deletePost() {
        this.status = PostStatus.DELETE;
    }
    
}
