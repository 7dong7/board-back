package mystudy.study.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mystudy.study.domain.member.dto.NewMemberForm;

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
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, NewMemberForm> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        // 초기화 로직 (필요시 사용) message 같은 걸 수정하는 경우
    }

    // 비밀번호가 같은지 확인하는 로직
    @Override
    public boolean isValid(NewMemberForm form, ConstraintValidatorContext context) {
        if (form.getPassword() == null || form.getConfirmPassword() == null) {
            // null 인 경우는 이미 유효하지 않음
            return false;
        }

        return form.getPassword().equals(form.getConfirmPassword()); // 필드 비교
    }
}
