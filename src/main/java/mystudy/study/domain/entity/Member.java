package mystudy.study.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mystudy.study.domain.BaseTimeEntity;
import mystudy.study.domain.JpaBaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@ToString
//@EntityListeners(AuditingEntityListener.class)
//public class Member extends JpaBaseEntity { // 순수 JPA 상속
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name= "member_id")
    private Long id;

    private String username;
//    private String password;

    private int age;
    private String email;

    // 어노테이션 사용 @EntityListeners(AuditingEntityListener.class)
//    @CreatedDate
//    private LocalDateTime created_at;
//    @LastModifiedDate
//    private LocalDateTime updated_at;

    public Member(String username, int age, String email) {
        this.username = username;
        this.age = age;
        this.email = email;
    }
    
    // 변경 메소드
    public void updateUsername(String username) {
        this.username = username;
    }
}
