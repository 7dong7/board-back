package mystudy.study.controller;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.PostDto;
import mystudy.study.domain.dto.PostSearchCondition;
import mystudy.study.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("posts")
public class PostController {

    private final PostService postService;

    // 게시글 검색 조건 페이징
    @GetMapping
    public String getPostPage(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchWord", required = false) String searchWord,
            @PageableDefault(size = 20, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable,
            Model model) {

        // pageable 생성
        Pageable pageable = PageRequest.of(
                clPageable.getPageNumber(),
                Math.max(1, Math.min(clPageable.getPageSize(), 50)), // 1 이상, 50 이하로 페이지 크기 제한
                clPageable.getSort()
        );

        // 게시글 검색 조건
        PostSearchCondition condition = PostSearchCondition.builder()
                .searchType(searchType)
                .searchWord(searchWord)
                .build();

        // 페이징 요청
        Page<PostDto> postPage = postService.getPostPage(pageable, condition);

        // 검색 조건
        Map<String, Object> map = new HashMap<>();
        map.put("searchType", searchType);
        map.put("searchWord", searchWord);
        

        // 확인
        System.out.println("postPage.getTotalPages() = " + postPage.getTotalPages());
        System.out.println("postPage.getTotalElements() = " + postPage.getTotalElements());
        
        List<PostDto> postDtoList = postPage.getContent();
        System.out.println("postDtoList.size() = " + postDtoList.size());

        for (PostDto postDto : postDtoList) {
            System.out.println("postDto = " + postDto);
        }

        model.addAttribute("postPage", postPage);
        model.addAttribute("searchParam", map);

        return "post/posts";
    }
}
