package mystudy.study.init;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import mystudy.study.domain.comment.entity.Comment;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.member.entity.RoleType;
import mystudy.study.domain.post.entity.Post;
import mystudy.study.domain.member.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitMember implements CommandLineRunner {

    private final InitMemberService initMemberService;
    private final MemberRepository memberRepository;


    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.count() == 0) {
            initMemberService.init();
        }
    }

    @Component
    @RequiredArgsConstructor
    static class InitMemberService {

        @PersistenceContext
        EntityManager em;

        private final BCryptPasswordEncoder bCryptPasswordEncoder;


        @Transactional
        public void init() {
            // 관리자 한명
            Member admin = Member.builder()
                    .email("admin")
                    .name("admin")
                    .password(bCryptPasswordEncoder.encode("1234"))
                    .nickname("관리자")
                    .age(0)
                    .role(RoleType.ROLE_ADMIN)
                    .build();

            em.persist(admin);

            Member member1 = Member.builder()
                    .email("member1@naver.com")
                    .password(bCryptPasswordEncoder.encode("test!"))
                    .nickname("member1")
                    .age(10)
                    .role(RoleType.ROLE_USER)
                    .build();

            em.persist(member1);

            // 새로운 사용자 추가
            for (int i = 2; i < 51; i++) {
                Member member = Member.builder()
                        .email("member" + i + "@naver.com")
                        .password(bCryptPasswordEncoder.encode("test!"))
                        .nickname("member" + i)
                        .age(i)
                        .role(RoleType.ROLE_USER)
                        .build();
                em.persist(member);
            }

            for (int i = 51; i < 101; i++) {
                Member member = Member.builder()
                        .email("user" + i + "@gmail.com")
                        .password(bCryptPasswordEncoder.encode("test!"))
                        .nickname("user" + i)
                        .age(i)
                        .role(RoleType.ROLE_USER)
                        .build();
                em.persist(member);
            }

            for (int i = 101; i < 124; i++) {
                Member member = Member.builder()
                        .email("postuser" + i + "@daum.com")
                        .password(bCryptPasswordEncoder.encode("test!"))
                        .nickname("postuser" + i)
                        .age(i)
                        .role(RoleType.ROLE_USER)
                        .build();
                em.persist(member);
            }

            // 게시글 작성
            for ( int i = 1; i < 103; i++ ) {
                Post post = new Post("새로운 글작성"+i, "새로운 글이 작성되었습니다. 오늘은 날씨가 좋네요"+i, member1);
                member1.addPost(post);
            }

            // 댓글 작성
                // 작성자
            Member member = em.createQuery("select m from Member m where m.nickname = :username", Member.class)
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

                // 부모 댓글
            Comment comment23 = em.createQuery("select c from Comment c where c.id = :id", Comment.class)
                    .setParameter("id", 23)
                    .getSingleResult();

                // 대댓글 작성자
            Member member2 = em.createQuery("select m from Member m where m.nickname = :username", Member.class)
                    .setParameter("username", "member2")
                    .getSingleResult();
            
                // 대댓글 (reply)
            for (int i = 1; i < 4; i++) {
                Comment reply = Comment.builder()
                        .content("댓글에 대댓글을 작성했다 " + i)
                        .post(post)
                        .member(member2)
                        .parent(comment23)
                        .build();
                em.persist(reply);
            }

            // 대댓글 작성자
            Member member3 = em.createQuery("select m from Member m where m.nickname = :username", Member.class)
                    .setParameter("username", "member3")
                    .getSingleResult();

            // 부모 댓글
            Comment comment22 = em.createQuery("select c from Comment c where c.id = :id", Comment.class)
                    .setParameter("id", 22)
                    .getSingleResult();

                // 대댓글 2
            for (int i = 1; i < 4; i++) {
                Comment reply = Comment.builder()
                        .content("댓글에 대댓글을 작성했다 " + i)
                        .post(post)
                        .member(member3)
                        .parent(comment22)
                        .build();
                em.persist(reply);
            }

        }
    }

}
