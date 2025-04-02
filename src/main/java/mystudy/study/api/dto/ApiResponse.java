package mystudy.study.api.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private String status;  // success, error
    private String code;    // 에러 코드 ( 응답실패시, error 발생시 )
    private String message;
    private T data;         // 성공 시 데이터, 실패 시 에러

    // 성공 응답 생성자
    public ApiResponse(String message, T data) {
        this.status = "success";
        this.message = message;
        this.data = data;
    }
    
    // 실패 응답 생성자
    public ApiResponse(String code, String message, T data) {
        this.status = "error";
        this.code = code;
        this.message = message;
        this.data = data;
    }
}