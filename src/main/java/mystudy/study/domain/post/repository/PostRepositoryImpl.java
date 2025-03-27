package mystudy.study.domain.post.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import mystudy.study.domain.post.dto.*;
import mystudy.study.domain.post.entity.Post;
import mystudy.study.domain.post.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static mystudy.study.domain.member.entity.QMember.member;
import static mystudy.study.domain.post.entity.QPost.post;
import static org.springframework.util.StringUtils.hasText;

public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 조건에 맞는 게시글 가져오기
    @Override
    public Page<PostDto> getPostPage(Pageable pageable, PostSearchCondition condition) {

        List<PostDto> content = queryFactory
                .select(new QPostDto(
                        post.id,
                        post.title,
                        post.createdAt,
                        post.viewCount,
                        member.id.as("memberId"),
                        member.nickname,
                        member.email)) // 검색 결과 Dto 변환
                .from(post)
                .leftJoin(post.member, member)
                .where(
                        transformPostSearchCondition(condition), // 검색 조건 변환
                        post.status.eq(PostStatus.ACTIVE)
                )
                .orderBy(
                        postSort(pageable)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = countQuery(condition);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 회원 id 에 해당하는 게시글 수
    @Override
    public Long getPostCountByMemberId(Long id) {
        return queryFactory
                .select(post.count())
                .from(post)
                .where(
                        post.member.id.eq(id),
                        post.status.eq(PostStatus.ACTIVE)
                )
                .fetchOne();
    }

    // 회원 정보 조회 - 회원가 작성한 게시글 조회 (페이징)
    @Override
    public Page<PostDto> getPostByMemberId(Long id, Pageable pageable) {

        List<PostDto> content = queryFactory
                .select(new QPostDto(
                        post.id,
                        post.title,
                        post.createdAt,
                        post.viewCount,
                        member.id.as("memberId"),
                        member.nickname,
                        member.email)) // 검색 결과 Dto 변환
                .from(post)
                .leftJoin(post.member, member)
                .where(
                        post.member.id.eq(id),      // 회원가 작성한 게시글 조회
                        post.status.eq(PostStatus.ACTIVE) // 삭제되지 않은 게시글 조회
                )
                .orderBy(
                        postSort(pageable)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(post.member, member)
                .where(
                        post.member.id.eq(id),
                        post.status.eq(PostStatus.ACTIVE)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 게시글 조회 - ViewPostDto 직접 조회
    @Override
    public ViewPostDto getViewPostDto(Long postId) {

        return queryFactory
                .select(new QViewPostDto(
                        post.id,
                        post.title,
                        post.content,
                        post.createdAt,
                        post.viewCount,
                        member.id.as("memberId"),
                        member.nickname,
                        member.email)
                )
                .from(post)
                .leftJoin(post.member, member)
                .where(
                        post.id.eq(postId),
                        post.status.eq(PostStatus.ACTIVE)
                )
                .fetchOne();
    }

    // 게시글 수정 - 게시글 조회 PostEditDto (postId 사용)
    @Override
    public PostEditDto getPostEditDtoByPostId(Long postId) {

        return queryFactory
                .select(new QPostEditDto(
                                post.id,
                                post.title,
                                post.content,
                                post.createdAt,
                                post.updatedAt,
                                post.viewCount,
                                member.id.as("memberId"),
                                member.email,
                                member.nickname
                        )
                )
                .from(post)
                .leftJoin(post.member, member)
                .where(
                        post.id.eq(postId),
                        post.status.eq(PostStatus.ACTIVE)
                )
                .fetchOne();
    }


    // postId로 게시글 조회 fetch join
    @Override
    public Optional<Post> findByPostId(Long postId) {

        Post result = queryFactory.select(post)
                .from(post)
                .leftJoin(post.member, member).fetchJoin()
                .where(
                        post.id.eq(postId),
                        post.status.eq(PostStatus.ACTIVE)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }


    // 정렬 조건 변환
    private OrderSpecifier<?>[] postSort(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (pageable.getSort().isSorted()) {

            for (Sort.Order order : pageable.getSort()) {

                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "id" -> orderSpecifiers.add(new OrderSpecifier<>(direction, post.id));
                    case "title" -> orderSpecifiers.add(new OrderSpecifier<>(direction, post.title));
                    case "createdAt" -> orderSpecifiers.add(new OrderSpecifier<>(direction, post.createdAt));
                    case "viewCount" -> orderSpecifiers.add(new OrderSpecifier<>(direction, post.viewCount));
                }
            }
        }
        
        // 리스트를 배열로 변환하여 반환
        return orderSpecifiers.toArray(new OrderSpecifier[0]);  // 리스트의 내용을 OrderSpecifier 타입의 배열로 변환
    }

    // 조건에 맞는 게시글의 수
    private JPAQuery<Long> countQuery(PostSearchCondition condition) {

        return queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(post.member, member)
                .where(
                        transformPostSearchCondition(condition),
                        post.status.eq(PostStatus.ACTIVE)
                );
    }

    // 검색 조건 변환
    private BooleanExpression transformPostSearchCondition(PostSearchCondition condition) {

        if (!hasText(condition.getSearchWord())) { // 검색 조건은 있을 수 있으나 검색어가 없다면 조건 무시
            return null;
        }
            
        if (hasText(condition.getSearchType())) { // 검색 조건이 존재하면
            String searchType = condition.getSearchType(); // 검색 조건
            String searchWord = condition.getSearchWord(); // 검색어

            // 검색 조건에 따른 데이터베이스 조회 쿼리
            return switch (searchType) {
                case "nickname" -> member.nickname.containsIgnoreCase(searchWord);
                case "title" -> post.title.containsIgnoreCase(searchWord);
                case "content" -> post.content.containsIgnoreCase(searchWord);
                case "title_content" -> post.title.containsIgnoreCase(searchWord)
                                                .or(post.content.containsIgnoreCase(searchWord)); // 제목 + 내용 검색
                default -> null; // 검색 조건이 있으나 사전에 정의된 조건이 아닌 경우
            };
        } else {
            return null;
        }
    }
}
