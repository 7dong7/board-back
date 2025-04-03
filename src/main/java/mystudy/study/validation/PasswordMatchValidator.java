package mystudy.study.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.member.dto.NewMemberForm;

import java.lang.reflect.Field;

/**
 *  ===== 어노테이션의 작동 로직 구현 ======
 *  
 *  ConstraintValidator<PasswordMatch, NewMemberForm>
 *          커스텀 어노테이션 PasswordMatch 를 적용한 객체를 선언 (NewMemberForm)
 *
 *  initialize 어노테이션의 초기화 ( message 커스텀시 사용 )
 *  
 *  isValid : 어노테이션의 로직을 구현
 *      null 체크 안전성 상승
 *      equals 를 사용 문자열 비교
 */
@Slf4j
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {
    private String passwordField;
    private String confirmPasswordField;


    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        // 초기화 로직 (필요시 사용) message 같은 걸 수정하는 경우
        this.passwordField =constraintAnnotation.passwordField();
        this.confirmPasswordField =constraintAnnotation.confirmPasswordField();
    }

    // 비밀번호가 같은지 확인하는 로직
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        // 리플렉션을 사용해 필드 값 가져오기
        log.info("비밀번호 확인 동작");
        try {
            String password = getFieldValue(object, passwordField);
            String confirmPassword = getFieldValue(object, confirmPasswordField);

            log.info("비밀번호 확인 password: {}, confirmPassword: {}", password, confirmPassword);
            // null 체크
            if ( password == null || confirmPassword == null) {
                return false;
            }

            // 비밀번호, 비밀번호 확인 값 같은지 확인
            log.info("결과: {}", password.equals(confirmPassword));
            return password.equals(confirmPassword);
        } catch (Exception e) {
            return false;
        }
    }


    private String getFieldValue(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true); // private 필드에 대한 접근 허용
        Object value = field.get(object);
        return value != null ? value.toString() : null;
    }
}
