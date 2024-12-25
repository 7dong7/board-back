package mystudy.study.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import mystudy.study.domain.dto.comment.CommentDto;
import mystudy.study.domain.dto.comment.QCommentDto;
import mystudy.study.domain.dto.post.PostDto;
import mystudy.study.domain.dto.post.QPostDto;
import mystudy.study.domain.entity.QComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
import static mystudy.study.domain.entity.QComment.comment;
import static mystudy.study.domain.entity.QMember.member;
import static mystudy.study.domain.entity.QPost.post;

public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    // 사용자가 작성한 댓글 수 가져오기
    @Override
    public Long getCommentCountByMemberId(Long id) {

        return queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.member.id.eq(id))
                .fetchOne();
    }

    @Override
    public Page<CommentDto> getCommentByMemberId(Long id, Pageable pageable) {

        List<CommentDto> content = queryFactory
                .select(new QCommentDto(
                        comment.id,
                        comment.content,
                        member.username,
                        comment.createdAt)
                )
                .from(comment)
                .leftJoin(comment.member, member)
                .where(
                        comment.member.id.eq(id)
                )
                .orderBy(
                        commentSort(pageable)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .leftJoin(comment.member, member)
                .where(
                        comment.member.id.eq(id)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 정렬 조건 변환 (단일 조건)
    private OrderSpecifier<?> commentSort(Pageable pageable) {

        if (pageable.getSort().isSorted()) {

            Sort.Order order = pageable.getSort().stream().findFirst().orElse(null); // 첫번째 정렬 조건 확인

            if (order != null) { // 정렬 조건이 null 이 아닌경우
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC; // 정렬 조건 방향 확인

                return switch (order.getProperty()) { // "id", "content", "username", "createdAt"
                    case "id" -> new OrderSpecifier<>(direction, comment.id);
                    case "content" -> new OrderSpecifier<>(direction, comment.content);
                    case "createdAt" -> new OrderSpecifier<>(direction, comment.createdAt);
                    default -> new OrderSpecifier<>(direction, comment.id);
                };
            }
        }

        return new OrderSpecifier<>(Order.DESC, comment.id);
    } // private OrderSpecifier<?> commentSort(Pageable pageable)
}
