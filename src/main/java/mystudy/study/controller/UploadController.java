package mystudy.study.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.service.UploadService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartRequest;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @ResponseBody
    @PostMapping("/image/upload")
    public Map<String, Object> uploadImage(MultipartRequest multiRequest,
                                           HttpServletRequest request) throws Exception {
        log.info("uploadImage: {}", multiRequest);
        log.info("ContextPath: {}", request.getContextPath());
        // 반환할 값
        Map<String, Object> response = new HashMap<>();

        try {
            // 이미지 저장 로직
            String uploadPath = uploadService.imageUpload(multiRequest, request);

            // 값 json 형태로 변환
            response.put("uploaded", true);
            response.put("url", uploadPath);
        } catch (Exception e) {
            response.put("uploaded", false);
        }

        return response;
    }
}
