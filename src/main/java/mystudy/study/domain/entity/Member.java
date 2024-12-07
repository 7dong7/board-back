package mystudy.study.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mystudy.study.domain.BaseTimeEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Getter
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
//public class Member extends JpaBaseEntity { // 순수 JPA 상속
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name= "member_id")
    private Long id;

    private String username;
//    private String password;

    private int age;
    private String email;

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
