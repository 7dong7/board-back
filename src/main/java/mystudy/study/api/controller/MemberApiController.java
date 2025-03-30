package mystudy.study.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.member.dto.GetMemberDetail;
import mystudy.study.domain.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/members/{id}")
    public ResponseEntity<GetMemberDetail> getMemberDetail(@PathVariable("id") Long memberId) {
        GetMemberDetail memberDetail = memberService.getMemberDetail(memberId);

        return new ResponseEntity<>(memberDetail, HttpStatus.OK);
    }
}
