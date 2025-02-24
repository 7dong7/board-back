package mystudy.study.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mystudy.study.domain.comment.dto.CommentDto;
import mystudy.study.domain.comment.service.CommentQueryService;
import mystudy.study.domain.member.dto.*;
import mystudy.study.domain.member.dto.search.MemberSearchCondition;
import mystudy.study.domain.member.entity.MemberStatus;
import mystudy.study.domain.member.entity.RoleType;
import mystudy.study.domain.post.dto.PostDto;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.member.repository.MemberRepository;
import mystudy.study.domain.post.service.PostService;
import mystudy.study.domain.comment.service.CommentService;
import mystudy.study.security.CustomUserDetail;
import mystudy.study.security.CustomUserDetailsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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

    // 로그인 회원를 위한 service
    private final CustomUserDetailsService customUserDetailsService;
    
    // 암호화
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CommentQueryService commentQueryService;

    // 회원 가입 : 서비스
    public void registerMember(RegisterMemberForm memberForm) {

        // 중복 회원 검증
        Member findMember = memberQueryService.findByEmail(memberForm.getEmail());

        if(findMember != null) { // 해당 email 로 이미 회원가입이 되어있는 상태
            throw new IllegalArgumentException("이미 존재하는 회원");
        }

        // 회원 생성
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

        // 회원 등록
        saveMember(member);
    }
    
    // 회원 등록
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    // 회원 정보 조회
    public InfoMemberDto getInfoMemberDto(Long memberId) {
        // 회원 정보
        Member member = memberQueryService.findMemberById(memberId);
        MemberStatus status = member.getStatus();
        
        if (status == MemberStatus.DELETE) { // 관리자는 탈퇴한 회원 조회 가능
            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            GrantedAuthority grantedAuthority = authorities.iterator().next();
            String role = grantedAuthority.getAuthority();

            if (!role.equals(RoleType.ROLE_ADMIN.name())) { // 권한이 관리자가 아닌 경우
                throw new IllegalArgumentException("탈퇴한 회원입니다.");
            }
        }

        // 작성한 게시글 수
        Long postCount = postService.getPostCountByMemberId(memberId);

        // 작성한 댓글 수
        Long commentCount = commentQueryService.getCommentCountByMemberId(memberId);

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

    // 회원 정보 조회 - 회원가 작성한 게시글 조회 (페이징)
    public Page<PostDto> getMemberPosts(Long memberId, Pageable postPageable) {
        /**
         *  회원 정보 조회시 회원의 게시글을 같이 볼 수 있게 만든다
         *  게시글 검색의 조건으로 memberId를 사용해서 회원의 게시글을 페이징 처리해서 조회한다
         */
        // 게시글 페이징
        return postService.getPostByMemberId(memberId, postPageable);
    }

    // 회원 정보 조회 - 회원가 작성한 댓글 조회 (페이징)
    public Page<CommentDto> getMemberComments(Long memberId, Pageable commentPageable) {
        /**
         *  게시글 검색의 조건으로 memberId를 사용해서 회원의 댓글을 페이징 처리해서 조회한다
         */
        // 댓글 페이징
        return commentQueryService.getCommentByMemberId(memberId, commentPageable);
    }

    // 회원 정보 수정
    public void editMember(Long memberId, EditMemberDto memberDto) {

        // 수정하려는 member 엔티티 조회 -> 없는 경우 예외 처리
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 회원가 없습니다. memberId: " + memberId));

        // 현재 로그인한 회원의 authentication 객체
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        // 회원 로그인 email
        String email = currentAuth.getName();

        // 로그인한 회원가 다른 사람의 정보를 수정하려는 경우
        if (!member.getEmail().equals(email)) { // 같지 않은 경우
            throw new IllegalArgumentException("잘못된 요청입니다. 회원: "+email+ "가 다른 회원: "+member.getEmail()+"의 정보를 수정하려고 접근했습니다.");
        }
        
    // 회원 정보 수정
        boolean memberUpdate = false; // 회원의 정보가 변경되지 않음
        // 닉네임 수정
        if (!memberDto.getNickname().equals(member.getNickname())) { // 다른 경우 수정
            member.updateNickname(memberDto.getNickname()); // 닉네임 변경
            memberUpdate = true; // 회원의 정보가 변경됨
        }
        // 휴대폰 번호 수정
        if (!memberDto.getMobile().equals(member.getMobile())) { // 다른 경우
            member.updateMobile(memberDto.getMobile());
            memberUpdate = true; // 회원의 정보가 변경됨
        }
        /**
         *  회원의 정보가 변경된 경우
         *  기존의 session 에 저장된 정보와 DB의 저장된 정보가 차이가 생긴다
         *  따라서, 다시 DB 에서 회원의 정보를 조회해 session에 저장해 최신화 시킨다
         */
        if (memberUpdate) { // 회원의 정보가 변경된 경우 session 에 저장된 정보를 변경
            // 변경된 정보 DB 에서 조회
            CustomUserDetail updateUserDetails = (CustomUserDetail) customUserDetailsService.loadUserByUsername(email);
            // 인증 객체 생성
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updateUserDetails,
                    currentAuth.getCredentials(),
                    updateUserDetails.getAuthorities()
            );
            // 변경된 회원 정보 세션에서 변경
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

    // 회원 탈퇴
    public void deleteMember(Long memberId,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        /**
         * 회원 탈퇴시
         * 1. 본인의 계정만 탈퇴시킬 수 있어야한다
         * 2. 소프트 딜리트 ACTIVE -> DELETE
         * 3. 세션에서 로그인 정보사 삭제
         */
        // 삭제하려는 회원 정보 조회
        Member member = memberQueryService.findMemberById(memberId); // 이미 옵셔널을 처리함
        
        // 로그인 회원
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        String email = currentAuth.getName();

        // 로그인 회원과 탈퇴 하려는 회원의 정보가 일치하는지 확인
        if (!member.getEmail().equals(email)) { // 로그인 회원정보가 다른 경우
            throw new IllegalStateException("잘못된 회원 탈퇴");
        }
        
        // 회원 탈퇴
        member.deleteMember(); // ACTIVE -> DELETE

        // 세션에서 로그인 사용자 삭제 (로그아웃)
        SecurityContextHolder.clearContext(); // 현재 로컬 스레드 초기화
        new SecurityContextLogoutHandler().logout(request, response, currentAuth); // 세션 무효화
    }





    // 회원 페이징 // ============ 삭제 예정 ============= //
    public Page<SearchMemberDto> getMemberPage(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.getMemberPage(condition, pageable);
    }



}
