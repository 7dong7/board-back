package mystudy.study.domain.member.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.comment.dto.CommentDto;
import mystudy.study.domain.member.dto.MemberInfoDto;
import mystudy.study.domain.member.dto.MemberSearchCondition;
import mystudy.study.domain.member.dto.SearchMemberDto;
import mystudy.study.domain.post.dto.PostDto;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.member.repository.MemberRepository;
import mystudy.study.domain.post.service.PostService;
import mystudy.study.domain.comment.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    
    // 다른 엔티티 서비스
    private final PostService postService;


    private final CommentService commentService;
    private final MemberQueryService memberQueryService;

    // 사용자 등록
    @Transactional
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    // 사용자 페이징
    public Page<SearchMemberDto> getMemberPage(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.getMemberPage(condition, pageable);
    }

    // 사용자 하나의 정보와 게시글 댓글 페이징 처리 가져오기
    public MemberInfoDto getMemberInfo(Long memberId, Pageable postPageable, Pageable commentPageable) {

        // 사용자 id로 사용자 조회
        Member member = memberQueryService.findMemberById(memberId);

        // 게시글 수 가져오기
        Long postCount = postService.getPostCountByMemberId(memberId);
        // 댓글 수 가져오기
        Long commentCount = commentService.getCommentCountByMemberId(memberId);

        // 게시글 페이징
        Page<PostDto> postPage = postService.getPostByMemberId(memberId, postPageable);
        // 댓글 페이징
        Page<CommentDto> commentPage = commentService.getCommentByMemberId(memberId, commentPageable);

        // 사용자 정보 반환
        // 반환 dto 채우기
        return MemberInfoDto.builder()
                .id(memberId)
                .nickname(member.getNickname())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .postCount(postCount)
                .commentCount(commentCount)
                .postPage(postPage)
                .commentPage(commentPage)
                .build();
    }


}
