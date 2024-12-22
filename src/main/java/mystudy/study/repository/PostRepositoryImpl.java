package mystudy.study.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import mystudy.study.domain.dto.post.PostDto;
import mystudy.study.domain.dto.post.PostSearchCondition;
import mystudy.study.domain.dto.post.QPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static mystudy.study.domain.entity.QMember.member;
import static mystudy.study.domain.entity.QPost.post;
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
                        member.username)) // 검색 결과 Dto 변환
                .from(post)
                .leftJoin(post.member, member)
                .where(
                        transformPostSearchCondition(condition) // 검색 조건 변환
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

    // 사용자 id 에 해당하는 게시글 수
    @Override
    public Long getPostCountByMemberId(Long id) {
        return queryFactory
                .select(post.count())
                .from(post)
                .where(post.member.id.eq(id))
                .fetchOne();
    }

    // 사용자 id에 해당하는 게시글 페이징
    @Override
    public Page<PostDto> getPostByMemberId(Long id, Pageable pageable) {

        List<PostDto> content = queryFactory
                .select(new QPostDto(
                        post.id,
                        post.title,
                        post.createdAt,
                        post.viewCount,
                        member.id.as("memberId"),
                        member.username)) // 검색 결과 Dto 변환
                .from(post)
                .leftJoin(post.member, member)
                .where(
                        post.member.id.eq(id)
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
                        post.member.id.eq(id)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 검색 조건 변환
    private BooleanExpression transformPostSearchCondition(PostSearchCondition condition) {

        if (hasText(condition.getSearchType())) { // 검색 조건이 존재하면
            String searchType = condition.getSearchType(); // 검색 조건
            String searchWord = condition.getSearchWord(); // 검색어

            // 검색 조건에 따른 데이터베이스 조회 쿼리
            System.out.println("searchType = " + searchType);
            System.out.println("searchWord = " + searchWord);
            return switch (searchType) {
                case "username" -> member.username.containsIgnoreCase(searchWord);
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

    // 조건에 맞는 게시글의 수
    private JPAQuery<Long> countQuery(PostSearchCondition condition) {

        return queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(post.member, member)
                .where(
                        transformPostSearchCondition(condition)
                );
    }

    // 정렬 조건 변환
    private OrderSpecifier<?> postSort(Pageable pageable) {

        if (pageable.getSort().isSorted()) {

            for (Sort.Order order : pageable.getSort()) {

                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "username" :
                        return new OrderSpecifier<>(direction, member.username);
                    case "viewCount" :
                        return new OrderSpecifier<>(direction, post.viewCount);
                    case "id":
                        return new OrderSpecifier<>(direction, post.id);
                }
            }
        }
        return null;
    }
}
