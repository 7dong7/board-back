package mystudy.study.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mystudy.study.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@ToString
public class Post extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "post_id", updatable = false)
    private Long id;

    private String title;
    private String content;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    private Integer viewCount = 0;

    // 생성자
    public Post(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    // 작성자

    
}
