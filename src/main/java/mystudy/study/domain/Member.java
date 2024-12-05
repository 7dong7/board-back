package mystudy.study.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id @GeneratedValue
    @Column(name= "member_id")
    private Long id;

    private String username;
//    private String password;

    private int age;
    private String email;

    @CreatedDate
    private LocalDateTime created_at;

    @LastModifiedDate
    private LocalDateTime updated_at;

    public Member(String username, int age, String email) {
        this.username = username;
        this.age = age;
        this.email = email;
    }
}
