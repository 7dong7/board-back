package mystudy.study.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  ===== 어노테이션 정의 =====
 */

@Constraint(validatedBy = PasswordMatchValidator.class) // 검증 클래스 지정 (로직)
@Target({ElementType.TYPE}) // 클래스 수준에서 사용
@Retention(RetentionPolicy.RUNTIME) // 런타임에서 유지
public @interface PasswordMatch {
    String message() default "비밀번호가 일치하지 않습니다."; // 기본에러 메시지
    Class<?>[] groups() default {}; // 검증 그룹 (필요시사용)
    Class<? extends Payload>[] payload() default {}; // 추가 메타데이터 (필요 시 사용)

    // 검증 필드 지정
    String passwordField();
    String confirmPasswordField();
}
