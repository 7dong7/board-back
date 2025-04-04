package mystudy.study.exception;

import org.springframework.security.core.AuthenticationException;

/**
 *  탈퇴한 회원에 대해서 로그인을 하려고하는 경우 발생하는 오류
 */
public class AccountDeletedException extends AuthenticationException {
    public AccountDeletedException(String msg) {
        super(msg);
    }
}
