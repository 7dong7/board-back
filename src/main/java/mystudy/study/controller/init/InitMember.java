package mystudy.study.controller.init;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import mystudy.study.domain.entity.Comment;
import mystudy.study.domain.entity.Member;
import mystudy.study.domain.entity.Post;
import mystudy.study.repository.MemberRepository;
import mystudy.study.repository.MemberRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {

    private final InitMemberService initMemberService;
    private final MemberRepository memberRepository;
    private final MemberRepositoryImpl memberRepositoryImpl;

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
            em.persist(member1);

            // 새로운 사용자 추가
            for (int i = 2; i < 51; i++) {
                Member member = new Member("member" + i, i, "member" + i + "@naver.com");
                em.persist(member);
            }

            for (int i = 51; i < 101; i++) {
                Member member = new Member("user" + i, i, "user" + i + "@gmail.com");
                em.persist(member);
            }

            for (int i = 101; i < 124; i++) {
                Member member = new Member("postuser" + i, i, "postuser" + i + "@daum.net");
                em.persist(member);
            }


            // 게시글 작성
            for ( int i = 1; i < 103; i++ ) {
                Post post = new Post("새로운 글작성"+i, "새로운 글이 작성되었습니다. 오늘은 날씨가 좋네요"+i, member1);
                member1.addPost(post);
            }

            // 댓글 작성
                // 작성자
            Member member = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1")
                    .getSingleResult();

                // 댓글을 작성할 게시글
            Post post = em.createQuery("select p from Post p where p.id = :id", Post.class)
                    .setParameter("id", 1)
                    .getSingleResult();

                // 댓글 (comment)
            for (int i = 1; i < 25; i++) {
                Comment comment = Comment.builder()
                        .content("내가 작성하는 글 " + i)
                        .post(post)
                        .member(member)
                        .build();
                em.persist(comment);
            }

                // 대상 comment
            Comment comment = em.createQuery("select c from Comment c where c.id = :id", Comment.class)
                    .setParameter("id", 1)
                    .getSingleResult();

                // 작성 사용자
            Member member2 = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member2")
                    .getSingleResult();
            
                // 대댓글 (reply)
            for (int i = 1; i < 4; i++) {
                Comment reply = Comment.builder()
                        .content("댓글에 대댓글을 작성했다 " + i)
                        .post(post)
                        .member(member2)
                        .parent(comment)
                        .build();
                em.persist(reply);
            }

//                Post post = new Post("새로운 글작성"+i, "새로운 글이 작성되었습니다"+i, member2);
//                member2.addPost(post);
//            }

//            em.persist(member2);

        }
    }

}
