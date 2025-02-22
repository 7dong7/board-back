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
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseTimeEntity {
    /**
     * updatable
     * 작성 이후 해당 필드가 데이터베이스에서 업데이트 되는것을 허용하지 않는다는 것
     * 기본값 true
     *
     * nullable
     * 필드 값이 null 을 허용할지 여부 설정
     * 기본값 true
     *
     * orphanRemoval
     * 부모 엔티티에서 자식 엔티티를 컬렉션(리스트, 세트 등) 에서 제거하면 자식 엔티티는 데이터베이스에서 자동으로 삭제되는 것
     * 강한 의존 관계에서 사용 CascadType.REMOVE 와 비슷하지만, 고아 객체에만 적용
     *
     * @OneToMany @OneToOne 에서 사용
     * 기본값 true
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false)
    private Long id;

    @Column(unique = true)
    private String email;       // 아이디
    private String password;    // 비밀번호

    private String name;        // 회원 본명
    private String nickname;    // 닉네임
    private int age;            // 나이대 ( 20-30 )
    private String mobile;      // 번호
    private String gender;      // 성볋 (남,여)
    private String birthday;    // 생일

    @Column(name = "provider_id")
    private String providerId;  // OAuth2 로그인시 회원 식별자 값
    private String provider;    // OAuth2 로그인 서버

    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.ACTIVE; // 회원 탈퇴 여부

    @Enumerated(EnumType.STRING) // 안붙이면 인덱스로 저장됨 0, 1
    private RoleType role; // 회원 권한

    /*
    * CascadeType.PERSIST 또는 CascadeType.ALL 옵션이 Member 엔티티의 posts 연관관계에 설정되어 있다면
    * 새로 추가된 Post 객체도 자동으로 영속성 컨텍스트에 등록되어, 트랜잭션 커밋 시 DB에 저장된다
    * posts 객체에 add 로 추가하는걸로 DB에 저장할 수 있다 
    * */
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // ---- 생성자 ----
    @Builder
    public Member(String email, String password, String name, String nickname, int age, String mobile, String gender, String birthday, String providerId, String provider, RoleType role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.age = age;
        this.mobile = mobile;
        this.gender = gender;
        this.birthday = birthday;
        this.providerId = providerId;
        this.provider = provider;
        this.role = role;
    }


    // ---- 메소드 ----
    // nickname 변경 메소드
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    // mobile 변경 메소드
    public void updateMobile(String mobile) {
        this.mobile = mobile;
    }
    
    // age 변경 메소드 (사용하는거 아님 로그인 할때 최신 네이버 데이터로 업데이트)
    public void updateAge(String age) {
        this.age = Integer.parseInt(age);
    }

    // 글 작성
    public void addPost(Post post) {
        posts.add(post);
    }

    // 글 삭제
    public void removePost(Post post) {
        posts.remove(post);
    }

    // 회원 탈퇴
    public void deleteMember() {
        this.status = MemberStatus.DELETE;
    }

}
