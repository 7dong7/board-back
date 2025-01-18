package mystudy.study.domain.entity;

import jakarta.persistence.EntityManager;
import mystudy.study.repository.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        for (int i = 1; i < 4; i++) {
            Member member = Member.builder()
                    .email("member" + i + "@naver.com")
                    .password("test!")
                    .username("member" + i)
                    .age(i)
                    .build();
            em.persist(member);
        }
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
    
    // 사용자 회원가입 테스트
    @Test
    public void saveMemberTest() throws Exception{
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("test!")
                .username("testuser")
                .age(10)
                .build();
        // 사용자 등록
        memberRepository.save(member);

        Member findMember = memberRepository.findByUsername("testuser")
                .orElseGet(() -> null);

        Assertions.assertThat(findMember).isEqualTo(member);
    }
}