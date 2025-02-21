package mystudy.study.domain.member.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.comment.dto.CommentDto;
import mystudy.study.domain.member.dto.*;
import mystudy.study.domain.post.dto.PostDto;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.member.repository.MemberRepository;
import mystudy.study.domain.post.service.PostService;
import mystudy.study.domain.comment.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    // repository
    private final MemberRepository memberRepository;

    // service
    private final PostService postService;
    private final CommentService commentService;

    private final MemberQueryService memberQueryService;
    
    // 암호화
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원 가입 : 서비스
    public void registerMember(RegisterMemberForm memberForm) {

        // 중복 사용자 검증
        Member findMember = memberQueryService.findByEmail(memberForm.getEmail());

        if(findMember != null) { // 해당 email 로 이미 회원가입이 되어있는 상태
            throw new IllegalArgumentException("이미 존재하는 회원");
        }

        // 사용자 생성
        Member member = Member.builder()
                .email(memberForm.getEmail())
                .password(bCryptPasswordEncoder.encode(memberForm.getPassword()))
                .name(memberForm.getName())
                .nickname(memberForm.getNickname())
                .age(memberForm.getAge())
                .mobile(memberForm.getMobile())
                .gender(memberForm.getGender())
                .birthday(memberForm.getBirthday())
                .build();

        // 사용자 등록
        saveMember(member);
    }
    
    // 회원 등록
    public void saveMember(Member member) {
        memberRepository.save(member);

    }

    // 사용자 정보 조회
    public InfoMemberDto getInfoMemberDto(Long memberId) {
        // 사용자 정보
        Member member = memberQueryService.findMemberById(memberId);
        
        // 작성한 게시글 수
        Long postCount = postService.getPostCountByMemberId(memberId);

        // 작성한 댓글 수
        Long commentCount = commentService.getCommentCountByMemberId(memberId);

        // 반환 dto 생성
        InfoMemberDto infoMemberDto = new InfoMemberDto();
        infoMemberDto.setMemberId(member.getId());
        infoMemberDto.setEmail(member.getEmail());
        infoMemberDto.setNickname(member.getNickname());
        infoMemberDto.setCreatedAt(member.getCreatedAt());

        infoMemberDto.setPostCount(postCount); // 게시글 수
        infoMemberDto.setCommentCount(commentCount); // 댓글 수

        return infoMemberDto;
    }

    // 사용자 정보 조회 - 사용자가 작성한 게시글 조회 (페이징)
    public Page<PostDto> getMemberPosts(Long memberId, Pageable postPageable) {
        /**
         *  사용자 정보 조회시 사용자의 게시글을 같이 볼 수 있게 만든다
         *  게시글 검색의 조건으로 memberId를 사용해서 사용자의 게시글을 페이징 처리해서 조회한다
         */
        // 게시글 페이징
        return postService.getPostByMemberId(memberId, postPageable);
    }

    // 사용자 정보 조회 - 사용자가 작성한 댓글 조회 (페이징)
    public Page<CommentDto> getMemberComments(Long memberId, Pageable commentPageable) {
        /**
         *  게시글 검색의 조건으로 memberId를 사용해서 사용자의 댓글을 페이징 처리해서 조회한다
         */
        // 댓글 페이징
        return commentService.getCommentByMemberId(memberId, commentPageable);
    }






    // 사용자 페이징
    public Page<SearchMemberDto> getMemberPage(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.getMemberPage(condition, pageable);
    }

}
