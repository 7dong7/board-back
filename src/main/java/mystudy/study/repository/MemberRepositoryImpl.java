package mystudy.study.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import mystudy.study.domain.dto.MemberSearchCondition;
import mystudy.study.domain.dto.QSearchMemberDto;
import mystudy.study.domain.dto.SearchMemberDto;
import mystudy.study.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

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
//                        usernameEq(condition.getUsername()),
//                        emailEq(condition.getEmail())
                )
                .fetch();
    }

    // 사용자 조건 검색 (페이징)
    @Override
    public Page<SearchMemberDto> getMemberPage(MemberSearchCondition condition, Pageable pageable) {

        List<SearchMemberDto> content = queryFactory
                .select(
                        new QSearchMemberDto(
                                member.id.as("member_id"),
                                member.username,
                                member.email,
                                member.createdAt)
                )
                .from(member)
                .where(
                        transformMemberSearchCondition(condition)
                )
                .orderBy(
                        memberSort(pageable)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = countQuery(condition);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
    
    // 정렬 조건 변환
    private OrderSpecifier<?> memberSort(Pageable pageable) {

        if (pageable.getSort().isSorted()) {

            for (Sort.Order order : pageable.getSort()) {

                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "username" :
                        return new OrderSpecifier<>(direction, member.username);
                    case "email" :
                        return new OrderSpecifier<>(direction, member.email);
                    case "id":
                        return new OrderSpecifier<>(direction, member.id);
                }
            }
        }
        return null;
    }

    // 조건에 맞는 사용자 수
    private JPAQuery<Long> countQuery(MemberSearchCondition condition) {

        return queryFactory
                .select(member.count())
                .from(member)
                .where(
                        transformMemberSearchCondition(condition)
                );
    }

    // 검색 조건 변환
    private BooleanExpression transformMemberSearchCondition(MemberSearchCondition condition) {

        if (hasText(condition.getSearchType())) {
            String searchType = condition.getSearchType(); // 검색 조건
            String searchWord = condition.getSearchWord(); // 검색어

            return switch (searchType) {
                case "username" -> member.username.containsIgnoreCase(searchWord);
                case "email" -> member.email.containsIgnoreCase(searchWord);
                default -> null; // 검색 조건이 있으나 사전에 정의된 조건이 아닌 경우
            };
        } else {
            return null;
        }
    }

    
    // ---
    private BooleanExpression usernameEq(String username) {
        return hasText(username) ? member.username.containsIgnoreCase(username) : null;
    }

    private Predicate emailEq(String email) {
        return hasText(email) ? member.email.containsIgnoreCase(email) : null;
    }
    
}
