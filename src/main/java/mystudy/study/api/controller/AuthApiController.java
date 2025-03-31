package mystudy.study.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.api.dto.AuthPasswordForm;
import mystudy.study.domain.member.service.MemberService;
import mystudy.study.security.jwt.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthApiController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    /**
     *  사용자의 정보를 수정하기 위해서 비밀번호 확인 검증
     *  비밀번호가 맞는경우 회원 수정 페이지에 접근가능
     *
     *  access 토큰의 회원정보 확인
     *  id로 회원 조회
     */
    @PostMapping("/api/auth/verify-password") // 로그인 사용자만 접근 가능
    public ResponseEntity<String> verifyPassword(HttpServletRequest request,
                                                 @RequestBody AuthPasswordForm authPassword) {
        log.info("verifyPassword authPassword: {}", authPassword);
        String authorization = request.getHeader("Authorization");
        if (authorization == null || // 토큰이 없는 경우
            authPassword == null || // 넘어온 데이터가 없는 경우 memberId, password
            // 넘어온 값이 존재하는데 jwt 토큰의 id와 프론트에서 넘어온 id 가 다른 경우
            authPassword.getMemberId() != jwtUtil.getMemberId(authorization.split(" ")[1])
        ) {
            return new ResponseEntity<>("잘못된 접근", HttpStatus.BAD_REQUEST);
        }

        Boolean isVerify = memberService.memberVerify(authPassword);

        if (isVerify) { // isVerify = true 인증 통과
            return new ResponseEntity<>("접근확인", HttpStatus.OK);
        }
        // isVerify = false 인증 실패
        return new ResponseEntity<>("비밀번호가 일치하지 않음", HttpStatus.UNAUTHORIZED);
    }
}
