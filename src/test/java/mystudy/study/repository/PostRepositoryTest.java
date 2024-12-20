package mystudy.study.repository;

import jakarta.persistence.EntityManager;
import mystudy.study.domain.dto.PostDto;
import mystudy.study.domain.dto.PostSearchCondition;
import mystudy.study.domain.entity.Member;
import mystudy.study.domain.entity.Post;
import mystudy.study.domain.entity.QPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        // 글 작성
        Post post1 = new Post("새로운 글작성1", "새로운 글이 작성되었습니다1.", member1);
        Post post2 = new Post("새로운 글작성2", "새로운 글이 작성되었습니다2.", member1);

        member1.addPost(post1);
        member1.addPost(post2);

        memberRepository.save(member1);
    }

    @Test
    public void postBasicTest() throws Exception{
        // member 찾아보기
        Optional<Member> optMember = memberRepository.findByUsername("member1");

        Member member = optMember.orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다"));

        List<Post> posts = member.getPosts();

        for (Post post : posts) {
            System.out.println("post = " + post);
            System.out.println("post.getCreatedAt() = " + post.getCreatedAt());
            System.out.println("post.getUpdatedBy() = " + post.getUpdatedAt());
            System.out.println("post.getCreatedBy() = " + post.getCreatedBy());
            System.out.println("post.getUpdatedBy() = " + post.getUpdatedBy());
        }

        // 삭제
        member.removePost(member.getPosts().get(0));
        Optional<Member> optMember2 = memberRepository.findByUsername("member1");

        Member member2 = optMember.orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다"));
        List<Post> posts2 = member2.getPosts();

        for (Post post : posts2) {
            System.out.println("post = " + post);
            System.out.println("post.getCreatedAt() = " + post.getCreatedAt());
            System.out.println("post.getUpdatedBy() = " + post.getUpdatedAt());
            System.out.println("post.getCreatedBy() = " + post.getCreatedBy());
            System.out.println("post.getUpdatedBy() = " + post.getUpdatedBy());
        }
    }
    
    @Test
    public void getPostPageTest() throws Exception{
        // pageable 생성
        Pageable pageable = PageRequest.of(
                0, // 페이지 숫자 0
                10, // 페이지 크기
                Sort.by(Sort.Order.desc("id")) // 기본 id 최신순 정렬
        ); 
        
        // 검색 조건 세팅
        // 게시글 검색 조건
        PostSearchCondition condition = PostSearchCondition.builder()
                .searchType("title")
                .searchWord("1")
                .build();

        // 게시글 검색
        Page<PostDto> postDtoPage = postRepository.getPostPage(pageable, condition);

        // 결과 확인
        System.out.println("postDtoPage.getTotalPages() = " + postDtoPage.getTotalPages());
        System.out.println("postDtoPage.getTotalElements() = " + postDtoPage.getTotalElements());
        List<PostDto> content = postDtoPage.getContent();
        for (PostDto postDto : content) {
            System.out.println("postDto = " + postDto);
        }

    }
}