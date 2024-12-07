package mystudy.study.repository;

import jakarta.persistence.EntityManager;
import mystudy.study.domain.dto.MemberSearchCondition;
import mystudy.study.domain.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

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
        Member member1 = new Member("member1", 10, "member1@naver.com");
        Member member2 = new Member("member2", 20, "member2@naver.com");
        Member member3 = new Member("member3", 30, "member3@naver.com");
        Member member4 = new Member("member4", 40, "member4@naver.com");
        Member user1 = new Member("user1", 10, "user1@naver.com");
        Member user2 = new Member("user2", 20, "user2@naver.com");
        Member user3 = new Member("user3", 30, "user3@naver.com");

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(user1);
        memberRepository.save(user2);
        memberRepository.save(user3);
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

    // assert test
    @Test
    public void assertTest() throws Exception{

        Optional<Member> findMemberOption = memberRepository.findByUsername("member1");

        Member member = findMemberOption.orElseThrow(() -> new IllegalArgumentException("사용자가 없음"));

        System.out.println("member = " + member);

        assertThat(member.getUsername()).isEqualTo("member1");
//        assertThat(member.getUsername()).as("찾는 사용자가 아닙니다.").isEqualTo("member2");
    }


    @Test
    public void assertTest2() throws Exception{

        String str = "hello, world, AssertJ";

        System.out.println("str = " + str);

        assertThat(str).isNotNull()
                .startsWith("he")   // he로 시작하는지 확인
                .contains("wor")    // wor 이 포함되어 있는지
                .endsWith("tJ")     // tJ 로 끝나는지
                .hasSize(21);   // 길이가 21 인지

    }

    // list 검증
    @Test
    void listTest() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

        assertThat(names)           // names 리스트를 대상으로
                .isNotEmpty()       // 리스트가 비어있는지
                .hasSize(3)     // 리스트 사이즈가 3인지
                .contains("Alice", "Bob")   // 리스트에 Alice, Bob
                .doesNotContain("David")    // David 가 포함되어 있지 있으면 에러
                .containsExactly("Alice", "Bob", "Charlie") // 리스트가 정확히 지정된 요소들로 구성되어 있는지 (순서도 맞아야 됨)
                .containsExactlyInAnyOrder("Charlie", "Bob", "Alice") // 리스타 정확히 지정된 요소들로 구성되어 있는지 (순서 상관 X)
                .startsWith("Alice")    // 콜렉션 Alice 로 시작하는지
                .endsWith("Charlie"); // 콜렉션 Charlie 로 끝나는지
    }
    
    // list 중첩 검증
    @Test
    void nestedListTest() {
        List<List<String>> nestedList = Arrays.asList(
                Arrays.asList("A1", "A2"),
                Arrays.asList("B1", "B2"),
                Arrays.asList("C1", "C2")
        );

//        assertThat(nestedList)
//                .hasSize(3)
//                .extracting(List::size)
//                .containsOnly(2, 2, 2)
//                .flatExtracting(list -> list)
//                .containsExactly("A1", "A2", "B1", "B2", "C1", "C2");
    }

    // map 검증
    @Test
    void mapTest() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Alice", 30);
        map.put("Bob", 25);

        assertThat(map)
                .isNotEmpty()
                .hasSize(2)
                .containsKeys("Alice", "Bob")
                .containsValues(25, 30)
                .containsEntry("Alice", 30)
                .doesNotContainKey("Charlie")
                .doesNotContainValue(40);
    }

    @Test
    public void memberSearchConditionTest() throws Exception{

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setUsername("member1");
        condition.setEmail("member1");

        List<Member> conditionMember = memberRepository.searchMember(condition);

        for (Member member : conditionMember) {
            System.out.println("member = " + member);
            System.out.println("member.getCreatedAt() = " + member.getCreatedAt());
            System.out.println("member.getUpdatedAt() = " + member.getUpdatedAt());
        }

        assertThat(conditionMember).extracting("username").contains("member1");
    }
    
}