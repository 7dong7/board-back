package mystudy.study.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.session.SessionConst;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    // 로그인과 같은 컨트롤러의 호출전에 확인해야되는 로직은 preHandle 에 작성
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 클라이언트가 접속하려는 URI 의 경로
            // 로그인이 되어있지 않은 경우 이 값을 사용하여 로그인후에 이 URI 로 돌려보내준다
        String requestURI = request.getRequestURI();
            // 요청 method 확인 post 인 경우 요청의 url 이 클라이언트를 돌려보내기에 적절하지 않을 수 있음
        String requestMethod = request.getMethod();
        log.info("LoginCheckInterceptor 실행 [{}][{}]", requestMethod, requestURI);


        // false 는 session 이 이미 있으면 가져오고, 없으면 생성하지 말라는 것
        HttpSession session = request.getSession(false);
        
        // 로그인 체크
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER_ID) == null ) {
        // session 이 null 이거나 session 안에 저장된 loginMemberId의 값이 null 인 경우는 미인증 사용자
            log.info("비 로그인 사용자의 요청");

            if ("POST".equals(requestMethod)) {
                String referer = request.getHeader("Referer");

                log.info("header Referer {}: ", referer);
                if(referer != null ) {
                    requestURI = referer;
                }
            }

            log.info("interceptor redirectURL: {}", requestURI);
            response.sendRedirect("/login?redirectURL="+requestURI);
            return false; // preHandle 의 리턴 값이 false 이면 더 이상 로직진행 금지
        }

        // 인증 사용자의 경우 preHandle 의 값이 true 로 이후 로직을 진행
        return true; // 로직이 작성되었다면 WebConfig 에 addInterceptors 에 interceptor 추가
    }

    // postHandle 부분의 로직이 필요가없음 (로그인 체크 인터셉터의 경우)

    // afterCompletion 부분의 로직이 필요가없음 (로그인 체크 인터셉터의 경우)
}
