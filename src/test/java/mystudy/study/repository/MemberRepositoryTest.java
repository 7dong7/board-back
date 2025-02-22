package mystudy.study.repository;

import jakarta.persistence.EntityManager;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.post.entity.Post;
import mystudy.study.domain.member.repository.MemberRepository;
import mystudy.study.domain.post.repository.PostRepository;
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
    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void setUp() {
        // 데이터 입력
        Member member1 = Member.builder()
                .email("member1@naver.com")
                .password("test!")
                .nickname("memberi")
                .age(1)
                .build();
        em.persist(member1);

        for (int i = 2; i < 5; i++) {
            Member member = Member.builder()
                    .email("member" + i + "@naver.com")
                    .password("test!")
                    .nickname("member" + i)
                    .age(i)
                    .build();
            em.persist(member);
        }
        // 회원 등록

        // 글 작성
        Post post1 = new Post("새로운 글작성", "새로운 글이 작성되었습니다.", member1);
        postRepository.save(post1);
    }

    // 기본 테스트
    @Test
    public void basicTest() throws Exception{

        // 컬렉션 전체 조회
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member);
        }

    }

    // optional 테스트
    @Test
    public void optionalTest() throws Exception{

        // 이메일로 회원 찾기
            // 이메일에 해당하는 회원가 있을수도 없을수도 있음
        Optional<Member> findMember = memberRepository.findByNickname("member1");
        Optional<Member> findMember2 = memberRepository.findByNickname("memb");

        System.out.println("findMember = " + findMember);

        Member member = findMember.orElseGet(() -> null);
//        Member nonMember = findMember2.orElseThrow(() -> new IllegalArgumentException("회원 없음")); // 오류를 발생시킴

        System.out.println("member = " + member);
//        System.out.println("nonMember = " + nonMember);
    }

    // assert test
    @Test
    public void assertTest() throws Exception{

        Optional<Member> findMemberOption = memberRepository.findByNickname("member1");

        Member member = findMemberOption.orElseThrow(() -> new IllegalArgumentException("회원가 없음"));

        System.out.println("member = " + member);

        assertThat(member.getNickname()).isEqualTo("member1");
//        assertThat(member.getUsername()).as("찾는 회원가 아닙니다.").isEqualTo("member2");
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
    public void assertFilteredTest() throws Exception {

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member);
        }

        assertThat(members).hasSize(7); // 전체 회원 수 7

        assertThat(members)
                .filteredOn(member -> member.getNickname().toLowerCase().contains("member"))
                .hasSize(4); // 7명 중 username 이 member 가 포함되는 회원는 4명
    }

    @Test
    public void assertAllMatchTest() throws Exception{

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member);
        }

        assertThat(members).hasSize(7); // 전체 회원 수 7

        assertThat(members)
                .filteredOn(member -> member.getNickname().toLowerCase().contains("member"))
                .allMatch(member -> member.getNickname().toLowerCase().contains("member"));

//        assertThat(members)
//                .as("리스트안의 모든 username에 member가 포함되지 않습니다.")
//                .allMatch(member -> member.getUsername().toLowerCase().contains("member"));

    }
    
}