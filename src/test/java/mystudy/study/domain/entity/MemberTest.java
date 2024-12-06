package mystudy.study.domain.entity;

import jakarta.persistence.EntityManager;
import mystudy.study.repository.MemberRepository;
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

    // 생성날짜, 수정날짜 테스트
    @Test
    public void auditingTest1() throws Exception{
        // given
        Member member1 = new Member("member1", 10, "member1@naver.com");
        memberRepository.save(member1);

        member1.updateUsername("new member1");

        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findAll();

        for (Member member : result) {
            System.out.println("member.getCreated_at() = " + member.getCreated_at());
            System.out.println("member.getUpdated_at() = " + member.getUpdated_at());
            System.out.println("member = " + member);
        }
    }
}