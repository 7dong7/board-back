package mystudy.study.repository;

import jakarta.persistence.EntityManager;
import mystudy.study.domain.entity.Member;
import mystudy.study.domain.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        // 사용자 등록
        Member member1 = new Member("member1", 10, "member1@naver.com");
        Member member2 = new Member("member2", 20, "member2@naver.com");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 글 작성
        Post post1 = new Post("새로운 글작성", "새로운 글이 작성되었습니다.", member1);

        postRepository.save(post1);
    }

    @Test
    public void postBasicTest() throws Exception{
        List<Post> posts = postRepository.findAll();

        for (Post post : posts) {
            System.out.println("post = " + post);
        }
    }
}