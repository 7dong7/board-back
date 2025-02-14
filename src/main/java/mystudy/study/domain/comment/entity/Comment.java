package mystudy.study.domain.comment.entity;

import jakarta.persistence.*;
import lombok.*;
import mystudy.study.domain.base.BaseEntity;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.post.entity.Post;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 (JPA 요구)
@ToString
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 게시글 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 작성자 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent; // 부모 댓글 ID  // null 이면 부모, 있으면 자식


    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>(); // 부모 댓글의 댓글들

    @Builder
    public Comment(Long id, String content, Post post, Member member, Comment parent, List<Comment> replies) {
        this.id = id;
        this.content = content;
        this.post = post;
        this.member = member;
        this.parent = parent;
        this.replies = replies;
    }

}
