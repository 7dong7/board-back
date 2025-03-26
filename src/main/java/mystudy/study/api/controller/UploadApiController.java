package mystudy.study.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.service.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UploadApiController {

    private final UploadService uploadService;

    @PostMapping("/api/image/upload")
    public ResponseEntity<Map<String, Object>> uploadApiImage(MultipartRequest multiRequest,
                                                             HttpServletRequest request) {
        log.info("uploadImage uploadImage: {}", multiRequest);
        log.info("uploadImage ContextPath: {}", request.getContextPath());
        // 반환할 값
        Map<String, Object> response = new HashMap<>();

        try {
            // 이미지 저장 로직
            String uploadPath = uploadService.imageApiUpload(multiRequest, request);

            // 값 json 형태로 변환
            response.put("uploaded", true);
            response.put("url", uploadPath);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("uploaded", false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
