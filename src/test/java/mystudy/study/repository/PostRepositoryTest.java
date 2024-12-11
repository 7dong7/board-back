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
import java.util.Optional;

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

        // 글 작성
        Post post1 = new Post("새로운 글작성1", "새로운 글이 작성되었습니다1.", member1);
        Post post2 = new Post("새로운 글작성2", "새로운 글이 작성되었습니다2.", member1);

        member1.addPost(post1);
        member1.addPost(post2);
        postRepository.save(post1);
        postRepository.save(post2);

    }

    @Test
    public void postBasicTest() throws Exception{
        // member 찾아보기

    }
}