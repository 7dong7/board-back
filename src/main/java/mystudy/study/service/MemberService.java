package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.MemberInfoDto;
import mystudy.study.domain.dto.MemberSearchCondition;
import mystudy.study.domain.dto.SearchMemberDto;
import mystudy.study.domain.entity.Member;
import mystudy.study.repository.CommentRepository;
import mystudy.study.repository.MemberRepository;
import mystudy.study.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Page<SearchMemberDto> getMemberPage(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.getMemberPage(condition, pageable);
    }

    @Transactional
    public MemberInfoDto getMemberInfo(Long id) {
        // 사용자 정보
        Optional<Member> memberOpt = memberRepository.findById(id);
        Member member = memberOpt.orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다." + id));

        // 게시글 수
        Long postCount = postRepository.getPostCountByMemberId(id);

        // 댓글 수
        Long commentCount = commentRepository.getCommentCountByMemberId(id);

        // 사용자 정보 반환
        return MemberInfoDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .postCount(postCount)
                .commentCount(commentCount)
                .build();
    }
}
