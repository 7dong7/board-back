package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.comment.CommentDto;
import mystudy.study.domain.dto.member.MemberInfoDto;
import mystudy.study.domain.dto.member.MemberSearchCondition;
import mystudy.study.domain.dto.member.SearchMemberDto;
import mystudy.study.domain.dto.post.PostDto;
import mystudy.study.domain.entity.Member;
import mystudy.study.repository.CommentRepository;
import mystudy.study.repository.MemberRepository;
import mystudy.study.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    
    // 다른 엔티티 서비스
    private final PostService postService;
    private final CommentService commentService;

    // 사용자 페이징
    @Transactional
    public Page<SearchMemberDto> getMemberPage(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.getMemberPage(condition, pageable);
    }

    // 사용자 하나의 정보와 게시글 댓글 페이징 처리 가져오기
    @Transactional
    public MemberInfoDto getMemberInfo(Long id, Pageable pageable) {
        // 사용자 정보
        Optional<Member> memberOpt = memberRepository.findById(id);
        Member member = memberOpt.orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다." + id));
        // 게시글 수 가져오기
        Long postCount = postService.getPostCountByMemberId(id);
        // 댓글 수 가져오기
        Long commentCount = commentService.getCommentCountByMemberId(id);

        // 게시글 페이징
        Page<PostDto> postPage = postService.getPostByMemberId(id, pageable);

        // 댓글 페이징
        Page<CommentDto> commentPage = commentService.getCommentByMemberId(id, pageable);


        // 사용자 정보 반환
        return MemberInfoDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .postCount(postCount)
                .commentCount(commentCount)
                .postPage(postPage)
                .commentPage(commentPage)
                .build();
    }
}
