package mystudy.study.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UploadService {

    @Value("${local.save.image.path}")
    String imagePath;

    @Value("${local.save.image.local_path}")
    String imageLocalPath;

    public String imageUpload(MultipartRequest multipartRequest,
                              HttpServletRequest httpServletRequest) throws IOException, InterruptedException {

        // 이미지 파일 추출
        MultipartFile file = multipartRequest.getFile("upload");
        log.info("UploadService imageUpload file: {}", file.getOriginalFilename());

        // 유일한 파일명 -> 저장할 파일명
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".")); // 확장자
        log.info("ext: {}", ext);

        String uuidFileName = UUID.randomUUID() + ext; // UUID 사용 유일한 파일명

        // 서버에 저장할 경로 + 파일명 (저장 절대 경로)
        String localSavePath = imagePath + uuidFileName;
        log.info("localPath: {}", localSavePath);
        // 파일을 저장할 실제 경로 + 파일명
        // C:/NCS/study/study/src/main/resources/static/upload/images/3a515387-fd6e-4c5e-8d4d-cfdabafc0579.png

        // 저장 피알 생성
        File localFile = new File(localSavePath);
        file.transferTo(localFile);

        // 파일 저장 완료 체크 (최대 2초 대기, 100ms 간격)
        int waited = 0;
        log.info("localFile.exists(): {}", localFile.exists());
        while (!localFile.exists() && waited < 2000) {
            Thread.sleep(100);
            waited += 100;
        }
        log.info("localFile.exists(): {}", localFile.exists());
        if (!localFile.exists()) {
            throw new IOException("이미지를 업로드할 수 없습니다");
        }

    // == 파일 불러올 경로 ==
        // contextPath 추출
        String contextPath = httpServletRequest.getContextPath();

        // "" + "/upload/images/" + "3a515387-fd6e-4c5e-8d4d-cfdabafc0579.png"
        String fileURL = contextPath + imageLocalPath + uuidFileName;
        log.info("fileURL: {}", fileURL);

        return fileURL;
    }
}
