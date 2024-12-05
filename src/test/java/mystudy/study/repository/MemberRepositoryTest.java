package mystudy.study.repository;

import jakarta.persistence.EntityManager;
import mystudy.study.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 각각의 메소드에 대해 트랜잭션 실행, 그 이후 테스트 종류 전부 롤백
class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        // 데이터 입력
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        Member member3 = new Member("member3", 30);
        Member member4 = new Member("member4", 40);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
    }
    
    // 기본 테스트
    @Test
    public void basicTest() throws Exception{
        
        // 컬렉션 전체 조회
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member);
        }

        assertThat(members.size()).isEqualTo(4);
    }
    
    // optional 테스트
    @Test
    public void optionalTest() throws Exception{

        // 이메일로 사용자 찾기 
            // 이메일에 해당하는 사용자가 있을수도 없을수도 있음
        Optional<Member> findMember = memberRepository.findByUsername("member1");
        Optional<Member> findMember2 = memberRepository.findByUsername("memb");

        System.out.println("findMember = " + findMember);

        Member member = findMember.orElseGet(() -> null);
//        Member nonMember = findMember2.orElseThrow(() -> new IllegalArgumentException("사용자 없음")); // 오류를 발생시킴

        System.out.println("member = " + member);
//        System.out.println("nonMember = " + nonMember);
    }


}