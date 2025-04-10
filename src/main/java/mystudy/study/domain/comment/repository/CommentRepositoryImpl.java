package mystudy.study.domain.comment.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import mystudy.study.domain.comment.dto.*;
import mystudy.study.domain.comment.entity.Comment;
import mystudy.study.domain.comment.entity.QComment;
import mystudy.study.domain.post.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static mystudy.study.domain.comment.entity.QComment.comment;
import static mystudy.study.domain.member.entity.QMember.member;
import static mystudy.study.domain.post.entity.QPost.post;

public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 회원 id를 사용해서 작성한 전체 댓글 수 조회
    @Override
    public Long getCommentCountByMemberId(Long id) {

        return queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.member.id.eq(id))
                .fetchOne();
    }

    // 회원 정보 조회 - 회원이 작성한 댓글 조회 (페이징)
    @Override
    public Page<CommentDto> getCommentByMemberId(Long memberId, Pageable pageable) {

        List<CommentDto> content = queryFactory
                .select(new QCommentDto(
                        comment.id,
                        comment.content,
                        member.nickname,
                        comment.createdAt,
                        comment.post.id.as("postId")
                        )
                )
                .from(comment)
                .join(comment.member, member)
                .join(comment.post, post)
                .where(
                        comment.member.id.eq(memberId),
                        post.status.eq(PostStatus.ACTIVE)
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
                .join(comment.member, member)
                .join(comment.post, post)
                .where(
                        comment.member.id.eq(memberId),
                        post.status.eq(PostStatus.ACTIVE)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 게시글 조회 - 댓글 조회 (페이징) : 댓글 페이징 조회후  대댓글 배치 조회
    public Page<ViewCommentDto> getViewComment(Long postId, Pageable commentPageable) {

        List<ViewCommentDto> result = queryFactory
                .select(new QViewCommentDto(
                        comment.id.as("commentId"),
                        comment.content,
                        member.nickname,
                        comment.createdAt,
                        comment.status,
                        comment.member.id.as("memberId")
                        )
                )
                .from(comment)
                .join(comment.post, post)
                .leftJoin(comment.member, member)
                .where(
                        post.status.eq(PostStatus.ACTIVE),  // 삭제되지 않은 게시판의 댓글 조회
                        post.id.eq(postId),      // 게시글에 해당하는 댓글 조회
                        comment.parent.isNull() // 부모댓글 조회
                )
                .orderBy(
                        comment.id.asc() // 생성순서 오름차순 1 2 3 4 5
                )
                .offset(commentPageable.getOffset()) //
                .limit(15)      // 댓글 15개 조회 고정
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .join(comment.post, post)
                .leftJoin(comment.member, member)
                .where(
                        post.status.eq(PostStatus.ACTIVE),  // 삭제되지 않은 게시판의 댓글 조회
                        post.id.eq(postId),      // 게시글에 해당하는 댓글 조회
                        comment.parent.isNull() // 부모댓글 조회
                );

        return PageableExecutionUtils.getPage(result, commentPageable, countQuery::fetchOne);
    }

    // 게시글 조회 - 대댓글 조회 : 댓글 부모Id 사용 배치 조회
    @Override
    public List<ViewReplyDto> getViewReply(List<Long> parentIdList) {

        return queryFactory
                .select(new QViewReplyDto(
                        comment.id.as("commentId"),
                        comment.parent.id,
                        comment.content,
                        comment.member.nickname.as("author"),
                        comment.createdAt,
                        comment.status,
                        comment.member.id.as("memberId")
                        )
                )
                .from(comment)
                .leftJoin(comment.post, post)
                .leftJoin(comment.member, member)
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
