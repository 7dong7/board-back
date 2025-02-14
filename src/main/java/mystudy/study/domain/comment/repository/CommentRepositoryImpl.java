package mystudy.study.domain.comment.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import mystudy.study.domain.comment.dto.CommentDto;
import mystudy.study.domain.comment.dto.ParentCommentDto;
import mystudy.study.domain.comment.dto.ReplyCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

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

    // 사용자 id를 사용해서 작성한 전체 댓글 수 조회
    @Override
    public Long getCommentCountByMemberId(Long id) {

        return queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.member.id.eq(id))
                .fetchOne();
    }

    // 사용자 id를 사용해서 댓글 조회 (페이징)
    @Override
    public Page<CommentDto> getCommentByMemberId(Long memberId, Pageable pageable) {

        List<CommentDto> content = queryFactory
                .select(new QCommentDto(
                        comment.id,
                        comment.content,
                        member.username,
                        comment.createdAt,
                        comment.post.id.as("postId"))
                )
                .from(comment)
                .leftJoin(comment.member, member)
                .where(
                        comment.member.id.eq(memberId)
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
                        comment.member.id.eq(memberId)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 게시글 id를 사용해서 댓글 조회 (페이징)
    @Override
    public Page<ParentCommentDto> getCommentByPostId(Long postId, Pageable commentPageable) {

        List<ParentCommentDto> content = queryFactory
                .select(new QParentCommentDto(
                        comment.id,
                        comment.content,
                        comment.member.username.as("author"),
                        comment.createdAt)
                )
                .from(comment)
                .leftJoin(comment.post, post)
                .where(
                        comment.post.id.eq(postId),
                        comment.parent.isNull()
                )
                .orderBy(
                        comment.id.asc() // 먼저 작성한 댓글 위로
                )
                .offset(commentPageable.getOffset()) // page number
                .limit(commentPageable.getPageSize()) // page size
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .leftJoin(comment.member, member)
                .where(
                        comment.post.id.eq(postId),
                        comment.parent.isNull()
                );

        return PageableExecutionUtils.getPage(content, commentPageable, countQuery::fetchOne);
    }

    // 댓글 id를 parentId 로 사용하는 댓글 조회 (대댓글 조회, where 절에서 in 사용해서 한번에 조회)
    @Override
    public List<ReplyCommentDto> getCommentByParentId(List<Long> parentIdList) {

        return queryFactory
                .select(new QReplyCommentDto(
                        comment.id,
                        comment.content,
                        comment.member.username.as("author"),
                        comment.createdAt,
                        comment.parent.id)
                )
                .from(comment)
                .leftJoin(comment.post, post)
                .where(
                        comment.parent.id.in(parentIdList)
                )
                .orderBy(
                        comment.id.asc() // 최신 대댓글은 아래로
                )
                .fetch();
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
