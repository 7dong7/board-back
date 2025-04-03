package mystudy.study.api.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.api.dto.ApiResponse;
import mystudy.study.domain.comment.dto.CommentDto;
import mystudy.study.domain.member.dto.GetMemberDetail;
import mystudy.study.domain.member.dto.MemberProfile;
import mystudy.study.domain.member.dto.NewMemberForm;
import mystudy.study.domain.member.dto.search.MemberDetailSearchCondition;
import mystudy.study.domain.member.service.MemberQueryService;
import mystudy.study.domain.member.service.MemberService;
import mystudy.study.domain.post.dto.PostDto;
import mystudy.study.security.jwt.JWTUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;

    private final JWTUtil jwtUtil;

    /**
     *  == 사용자 조회 ==
     *      모든 사용자가 볼수 있는 정보
     *      회원 정보 조회
     *      작성한 게시글 목록 조회
     *      작성한 댓글 목록 조회
     */
    @GetMapping("/api/members/{id}")
    public ResponseEntity<GetMemberDetail> getMemberDetail(@PathVariable("id") Long memberId,
                                                           @ModelAttribute MemberDetailSearchCondition searchCondition,
                                                           @PageableDefault(size = 10, page = 1, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable) {
        log.info("getMemberDetail searchCondition: {}", searchCondition);
        /**
         *  사용자의 정보를 먼저 조회하고 적합한 사용자가 아닌 경우에 그냥 종료시키면 된다
         *  게시글, 댓글을 조회하지 않는다
         */

        // 사용자 정보 조회
        GetMemberDetail memberDetail = memberService.getMemberDetail(memberId);
        /**
         *  찾은 사용자가 적합한지 확인하는 로직 ....
         */

        // 검색 조건 설정
        Set<String> ALLOWED_POST = Set.of("id", "title", "viewCount", "nickname", "createdAt");
        Set<String> ALLOWED_COMMENT = Set.of("id", "content", "nickname", "createdAt");

        if (!ALLOWED_POST.contains(searchCondition.getPostSort())) { // 적합하지 않은 검색 조건
            searchCondition.setPostSort("id");
        }
        if (!ALLOWED_COMMENT.contains(searchCondition.getCommentSort())) {
            searchCondition.setCommentSort("id");
        }

        // 게시글 페이징
        Pageable postPageable = PageRequest.of(
                Math.max(searchCondition.getPostPageNumber() - 1, 0), // 페이지 숫자
                10, // 게시글 사이즈 10 고정
                searchCondition.getPostDirection().equals("ASC")
                        ? Sort.by(searchCondition.getPostSort()).ascending()
                        : Sort.by(searchCondition.getPostSort()).descending()
        );
        Page<PostDto> pagePost = memberService.getMemberPosts(memberId, postPageable); // 게시글
        memberDetail.setPagePost(pagePost);

        // 댓글 페이징
        // comment Pageable 생성
        Pageable commentPageable = PageRequest.of(
                Math.max(searchCondition.getCommentPageNumber() - 1, 0),
                10,
                searchCondition.getCommentDirection().equals("ASC")
                        ? Sort.by(searchCondition.getCommentSort()).ascending()
                        : Sort.by(searchCondition.getCommentSort()).descending()
        );
        // 댓글 페이징
        Page<CommentDto> PageComment = memberService.getMemberComments(memberId, commentPageable);
        memberDetail.setPageComment(PageComment);

        return new ResponseEntity<>(memberDetail, HttpStatus.OK);
    }

    /**
     *  == 사용자 가입 ==
     *      @Valid 검증 실패시 => 오류발생 => GlobalExceptionHandler 가 처리
     */
    @PostMapping("/api/members")
    public ResponseEntity<ApiResponse<String>> newMember(@Valid @RequestBody NewMemberForm newMember) {
        log.info("회원 가입 로직 실행중 .... newMember: {}", newMember);
        /**
         *  memberService.newMember(newMember) 의 경우 이메일에 해당하는 사용자가 존재하는 경우
         *      throw new DuplicateEmailException("이미 존재하는 회원입니다.")
         *      exception 이 발생한다
         *
         *      해당 exception 은 GlobalExceptionHandler 가 처리하도록 구현했다
         *      따라서 exception 을 처리하기 위한 try-catch 문은 작성하지 않는다
         *  
         * @Vaild 어노테이션 사용
         *      내부적으로 유효성 검사 어노테이션을 상용해서 검증
         *      커스텀 어노테이션을 만들어서 사용 password, confirmPassword 비교
         */
        // 회원 가입 서비스
        memberService.newMember(newMember);

        return new ResponseEntity<>(new ApiResponse<>("회원가입에 성공했습니다.", "성공"), HttpStatus.OK);
    }
    /**
     *  == 사용자 조회 (수정 목적) ==
     *      수정할 데이터 조회
     *          mobile, nickname 기존 정보 가져가서 보여줌
     *          password 보여주지 말고 수정값 받기
     */
    @GetMapping("/api/members/profile")
    public ResponseEntity<ApiResponse<MemberProfile>> getMemberProfile(@RequestParam("id") Long memberId) {
        log.info("getMemberProfile memberId: {}", memberId);

        MemberProfile memberProfile = memberQueryService.getMemberProfile(memberId);
        log.info("getMemberProfile memberProfile: {}", memberProfile);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>("member profile",memberProfile));
    }
    /**
     *  == 사용자 정보 수정 요청 ==
     *      수정하려는 정보에 대해서 valid 적용 (검증)
     *      member 정보 수정 (처리)
     */
    @PatchMapping("/api/members/{id}")
    public ResponseEntity<ApiResponse<String>> editMemberProfile(@PathVariable("id") Long memberId,
                                                    @Valid @RequestBody MemberProfile memberProfile,
                                                    HttpServletRequest request) {
        log.info("MemberId: {}, memberProfile: {}", memberId, memberProfile);
        // access 토큰의 값
        String access = request.getHeader("Authorization").split(" ")[1];

        // 수정하려는 Id와 로그인한 사용자의 Id 가 같은 경우
        if (memberId.equals(jwtUtil.getMemberId(access))) {
            memberService.editMemberApi(memberId, memberProfile);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>("성공", "회원정보가 성공적으로 변경되었습니다."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("BAD_REQUEST", "실패", "잘못된 요청입니다."));
    }
    /**
     *  == 사용자 탈퇴 요청 ==
     *      jwt 사용자 검증
     *      탈퇴 성공시 refresh 토큰 삭제
     *
     */
    @DeleteMapping("/api/members/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMember(@PathVariable("id") Long memberId,
                                                            HttpServletRequest request, HttpServletResponse response) {
        log.info("회원 탈퇴 처리중 ... MemberId: {}", memberId);
        String access = request.getHeader("Authorization").split(" ")[1];

        // 수정하려는 Id와 로그인한 사용자의 Id 가 같은 경우
        if (memberId.equals(jwtUtil.getMemberId(access))) {
            memberService.deleteMember(memberId, request, response);
            response.addCookie(deleteCookie("refresh")); // refresh 토큰 삭제
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>("성공", "성공적으로 탈퇴했습니다."));
        }
        // 잘못된 요청
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("BAD_REQUEST", "실패", "잘못된 요청입니다."));
    }



    
    // == 기존 쿠키 삭제 ==
    private Cookie deleteCookie(String name) {
        Cookie cookie = new Cookie(name, null); // 값은 null로 설정
        cookie.setHttpOnly(true); // 기존 쿠키와 동일한 속성 유지
        cookie.setPath("/"); // 동일한 경로 지정
        cookie.setMaxAge(0); // 즉시 만료
        return cookie;
    }
}
