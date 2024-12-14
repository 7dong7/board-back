package mystudy.study.controller.init;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import mystudy.study.domain.entity.Member;
import mystudy.study.domain.entity.Post;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {

    private final InitMemberService initMemberService;

    @PostConstruct
    public void init() {
        initMemberService.init();
    }

    @Component
    static class InitMemberService {

        @PersistenceContext
        EntityManager em;

        @Transactional
        public void init() {
            Member member1 = new Member("member1", 10, "member1@naver.com");
//            Member member2 = new Member("member2", 20, "member2@naver.com");
            
            // member1의 게시글 작성
            for ( int i = 0; i < 103; i++ ) {
                Post post = new Post("새로운 글작성"+i, "새로운 글이 작성되었습니다"+i, member1);
                member1.addPost(post);
            }

//            for ( int i = 0; i < 24; i++ ) {
//                Post post = new Post("새로운 글작성"+i, "새로운 글이 작성되었습니다"+i, member2);
//                member2.addPost(post);
//            }

            em.persist(member1);
//            em.persist(member2);

            // 새로운 사용자 추가
            for (int i = 2; i < 102; i++) {
                Member member = new Member("member" + i, i, "member" + i + "@naver.com");
                em.persist(member);
            }
        }
    }

}
