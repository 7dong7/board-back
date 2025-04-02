package mystudy.study.advice;

import lombok.extern.slf4j.Slf4j;
import mystudy.study.api.dto.ApiResponse;
import mystudy.study.exception.DuplicateEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     *  api 의 요청이 데이터와 함께 들어오는 경우
     *  해당 데이터의 대한 유효성 검증 (@Valid) 를 실행하고 오류가 발생하면 오류를 처리할 수 있는 핸들러가 담당한다
     */

    /**
     * 회원가입시 입력받은 데이터를 검증하는
     * MethodArgumentNotValidException 핸들러
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        /**
         *  @ExceptionHandler 에 등록되어있는 exception 이 발생하는 경우 해당 핸들러가 동작하게 된다 ( 메소드가 실행되도록 지정 )
         *
         *  @RestControllerAdvice 클래스 안에 @ExceptionHandler 가 있으면 전역적으로 exception 을 받는다 ( 범위를 한정 지을 수 있음 )
         *
         *  MethodArgumentNotValidException ex
         *      예외가 발생하면 예외객체(ex)에서 유효성 검증 경과를 가져와, 실패한 field 와 message 를 추출할 수 있다
         *      ex.getBindingResult() => 검증 결과를 담고 있는 객체 (BindingResult)를 반환
         *      getFieldErrors() => 실패한 필드별 오류 목록 제공 ( List<FiledError> 리스트형태 )
         *          forEach 가 각 오류 필드를 순회
         *          error.getField(): 검증을 실패한 오류 필드 명
         *          error.getDefaultMessage(): 유효성 어노테이션에 설정된 메시지
         *                      @NotNull(message = "이름은 필수입니다.")
         *
         *     상태 코드와 메시지를 활용하면 프론트에게 잘 전달할 수 있음
         */
        Map<String, String> errors = new HashMap<>(); // 각 필드별로 에러 발생시 에러를 담을 맵 객체

        // all Error
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                errors.put(((FieldError) error).getField(), error.getDefaultMessage());
            } else {
                errors.put("confirmPassword", error.getDefaultMessage()); // @PasswordMatch 에러
            }
        });

        // exception response 생성 및 응답
        return new ResponseEntity<>(
                new ApiResponse<>("VALIDATION_ERROR", "유효성 검증 실패", errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleDuplicateEmail(DuplicateEmailException ex) {
        // 에러를 담을 객체
        Map<String, String> errors = new HashMap<>(); // 각 필드별로 에러 발생시 에러를 담을 맵 객체
        errors.put("email", ex.getMessage());

        // exception response 생성 및 응답
        return new ResponseEntity<>(
                new ApiResponse<>("VALIDATION_ERROR", "유효성 검증 실패", errors),
                HttpStatus.BAD_REQUEST
        );
    }
}
