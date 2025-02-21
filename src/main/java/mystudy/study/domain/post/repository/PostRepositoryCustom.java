package mystudy.study.domain.post.repository;

import mystudy.study.domain.post.dto.*;
import mystudy.study.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    // 전체 게시글 페이징으로 가져오기
    Page<PostDto> getPostPage(Pageable pageable, PostSearchCondition condition);

    // 사용자가 작성한 게시글 수
    Long getPostCountByMemberId(Long id);

    // 사용자 정보 조회 - 사용자가 작성한 게시글 조회 (페이징)
    Page<PostDto> getPostByMemberId(Long id, Pageable pageable);

    // 게시글 postId로 조회 - fetch join
    Post findByPostId(Long postId);

    // 게시글 조회 - ViewPostDto 직접 조회
    ViewPostDto getViewPostDto(Long postId);

    // 게시글 수정 - 게시글 조회 PostEditDto (postId 사용)
    PostEditDto getPostEditDtoByPostId(Long postId);

    // ============= 삭제 예정 ================
    // 게시글 보기 - 게시글 내용 조회
    PostViewDto getPostView(Long postId);
}
