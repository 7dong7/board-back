package mystudy.study.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import mystudy.study.domain.base.BaseTimeEntity;
import mystudy.study.domain.comment.entity.Comment;
import mystudy.study.domain.post.entity.Post;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseTimeEntity {
    /**
     * updatable
     * 작성 이후 해당 필드가 데이터베이스에서 업데이트 되는것을 허용하지 않는다는 것
     * 기본값 true
     * <p>
     * nullable
     * 필드 값이 null 을 허용할지 여부 설정
     * 기본값 true
     * <p>
     * orphanRemoval
     * 부모 엔티티에서 자식 엔티티를 컬렉션(리스트, 세트 등) 에서 제거하면 자식 엔티티는 데이터베이스에서 자동으로 삭제되는 것
     * 강한 의존 관계에서 사용 CascadType.REMOVE 와 비슷하지만, 고아 객체에만 적용
     *
     * @OneToMany @OneToOne 에서 사용
     * 기본값 true
     */
    @Id
    @GeneratedValue
    @Column(name = "member_id", updatable = false)
    private Long id;

    private String email;
    private String password;

    private String username;
    private int age;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();


    // ---- 생성자 ----
    @Builder
    public Member(String email, String password, String username, int age) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.age = age;
    }

    // ---- 메소드 ----
    // username 변경 메소드
    public void updateUsername(String username) {
        this.username = username;
    }

    // 글 작성
    public void addPost(Post post) {
        posts.add(post);
    }

    // 글 삭제
    public void removePost(Post post) {
        posts.remove(post);
    }


}
