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

    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.ACTIVE;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;  // 댓글을 작성한 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // 댓글을 작성한 회원

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


// == 메소드 == //
    // 게시글에 댓글 추가
    public void addPost(Post post) {
        this.post = post;
        this.post.getComments().add(this);
    }

    // 회원에 댓글 추가
    public void addMember(Member member) {
        this.member = member;
        this.member.getComments().add(this);
    }

    // 댓글 삭제하기
    public void delete() {
        this.status = CommentStatus.DELETE;
    }
}
