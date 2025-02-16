package mystudy.study;

import mystudy.study.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 스피링 인터셉터
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 로그인 인터셉터
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/login", "/logout", "/error", "/css/**", "/*.ico",
                        "/posts", "/posts/*");

    }
}
