package mystudy.study.exception;

public class DuplicateEmailException extends RuntimeException {
    // 회원가입시 회원이 이미 존재하는 경우에 발생하는 커스텀 exception
    public DuplicateEmailException(String message){
        super(message);
    }
}
