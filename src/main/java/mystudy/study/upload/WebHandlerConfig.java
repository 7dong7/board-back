package mystudy.study.upload;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebHandlerConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("upload/images/**")
                .addResourceLocations("file:C:/NCS/study/study/src/main/resources/static/upload/images/")
                .setCachePeriod(0); // 캐시 비활성화 => 즉시 로드 보장
    }
}
