package mystudy.study.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.comment.dto.CommentDto;
import mystudy.study.domain.member.dto.GetMemberDetail;
import mystudy.study.domain.member.dto.search.MemberDetailSearchCondition;
import mystudy.study.domain.member.service.MemberService;
import mystudy.study.domain.post.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;


    /**
     *  회원 정보 조회 api
     *      회원 정보 조회
     *      회원이 작성한 게시글 목록 조회
     *      회원이 작성한 댓글 목록 조회
     */
    @GetMapping("/api/members/{id}")
    public ResponseEntity<GetMemberDetail> getMemberDetail(@PathVariable("id") Long memberId,
                                                           @ModelAttribute MemberDetailSearchCondition searchCondition,
                                                           @PageableDefault(size = 10, page = 1, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable) {
        log.info("getMemberDetail searchCondition: {}", searchCondition);
        /**
         *  사용자의 정보를 먼저 조회하고 적합한 사용자가 아닌 경우에 그냥 종료시키면 된다
         *  게시글, 댓글을 조회하지 않는다
         */

        // 사용자 정보 조회
        GetMemberDetail memberDetail = memberService.getMemberDetail(memberId);
        /**
         *  찾은 사용자가 적합한지 확인하는 로직 ....
         */

        // 검색 조건 설정
        Set<String> ALLOWED_POST = Set.of("id", "title", "viewCount", "nickname", "createdAt");
        Set<String> ALLOWED_COMMENT = Set.of("id", "content", "nickname", "createdAt");

        if(!ALLOWED_POST.contains(searchCondition.getPostSort())) { // 적합하지 않은 검색 조건
            searchCondition.setPostSort("id");
        }
        if(!ALLOWED_COMMENT.contains(searchCondition.getCommentSort())) {
            searchCondition.setCommentSort("id");
        }

        // 게시글 페이징
        Pageable postPageable = PageRequest.of(
                Math.max(searchCondition.getPostPageNumber() - 1, 0), // 페이지 숫자
                10, // 게시글 사이즈 10 고정
                searchCondition.getPostDirection().equals("ASC")
                        ? Sort.by(searchCondition.getPostSort()).ascending()
                        : Sort.by(searchCondition.getPostSort()).descending()
        );
        Page<PostDto> pagePost = memberService.getMemberPosts(memberId, postPageable); // 게시글
        memberDetail.setPagePost(pagePost);


        // 댓글 페이징
        // comment Pageable 생성
        Pageable commentPageable = PageRequest.of(
                Math.max(searchCondition.getCommentPageNumber()-1, 0),
                10,
                searchCondition.getCommentDirection().equals("ASC")
                        ? Sort.by(searchCondition.getCommentSort()).ascending()
                        : Sort.by(searchCondition.getCommentSort()).descending()
        );
        // 댓글 페이징
        Page<CommentDto> PageComment = memberService.getMemberComments(memberId, commentPageable);
        memberDetail.setPageComment(PageComment);

        return new ResponseEntity<>(memberDetail, HttpStatus.OK);
    }


}