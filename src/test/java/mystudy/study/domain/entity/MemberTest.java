package mystudy.study.domain.entity;

import jakarta.persistence.EntityManager;
import mystudy.study.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    EntityManager em;

    @Autowired
    private MemberRepository memberRepository;


    @BeforeEach
    void setUp() {
        // 데이터 입력
        Member member1 = new Member("member1", 10, "member1@naver.com");
        Member member2 = new Member("member2", 20, "member2@naver.com");
        Member member3 = new Member("member3", 30, "member3@naver.com");
        Member member4 = new Member("member4", 40, "member4@naver.com");

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
    }


    // 생성날짜, 수정날짜 테스트
    @Test
    public void auditingTest1() throws Exception{

        List<Member> result = memberRepository.findAll();

        for (Member member : result) {
            System.out.println("member.getCreated_at() = " + member.getCreatedAt());
            System.out.println("member.getUpdated_at() = " + member.getUpdatedAt());
            System.out.println("member = " + member);
        }

    }
}