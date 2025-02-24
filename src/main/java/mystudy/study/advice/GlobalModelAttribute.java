package mystudy.study.advice;

import lombok.extern.slf4j.Slf4j;
import mystudy.study.advice.dto.CurrentMemberDto;
import mystudy.study.security.CustomUserDetail;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@Slf4j
public class GlobalModelAttribute {

    @ModelAttribute("currentMember")
    public CurrentMemberDto getCurrentMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 로그인 한 상태 검증
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();

            if (principal instanceof CustomUserDetail customUserDetail) {
            // 폼 로그인의 경우
                CurrentMemberDto currentMemberDto = new CurrentMemberDto(customUserDetail);
                log.info("Current Member: {}", currentMemberDto);

                return currentMemberDto;
            }
        }
        return null;
    }
}
