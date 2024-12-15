package mystudy.study.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import mystudy.study.domain.dto.MemberSearchCondition;
import mystudy.study.domain.entity.Member;
import mystudy.study.domain.entity.QMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static mystudy.study.domain.entity.QMember.member;
import static org.springframework.util.StringUtils.hasText;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 사용자 조건 검색
    @Override
    public List<Member> searchMember(MemberSearchCondition condition) {

        return queryFactory
                .select(member)
                .from(member)
                .where(
                        usernameEq(condition.getUsername()),
                        emailEq(condition.getEmail())
                )
                .fetch();
    }

    // 사용자 조건 검색 (페이징)
    @Override
    public Page<Member> searchMembers(MemberSearchCondition condition, Pageable pageable) {

        List<Member> content = queryFactory.select(member)
                .from(member)
                .where(
                        usernameEq(condition.getUsername()),
                        emailEq(condition.getEmail())
                )
                .orderBy(member.username.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = countQuery(condition);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private OrderSpecifier<?> memberSort(Pageable page) {

        if (!page.getSort().isEmpty()) {

            for (Sort.Order order : page.getSort()) {

                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "username" :
                        return new OrderSpecifier<>(direction, member.username);
                    case "email" :
                        return new OrderSpecifier<>(direction, member.email);
                    case "age":
                        return new OrderSpecifier<>(direction, member.age);

                }
            }
        }
        return null;
    }


    // 검색 조건에 해당하는 총 사용자 수
    private JPAQuery<Long> countQuery(MemberSearchCondition condition) {

        return queryFactory
                .select(member.count())
                .from(member)
                .where(
                        usernameEq(condition.getUsername()),
                        emailEq(condition.getEmail())
                );
    }

    private BooleanExpression usernameEq(String username) {
        return hasText(username) ? member.username.contains(username) : null;
    }

    private Predicate emailEq(String email) {
        return hasText(email) ? member.email.contains(email) : null;
    }
}
