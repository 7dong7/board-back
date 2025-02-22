package mystudy.study.domain.member.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import mystudy.study.domain.member.dto.MemberSearchCondition;
import mystudy.study.domain.member.dto.*;
import mystudy.study.domain.member.dto.SearchMemberDto;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.member.entity.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static mystudy.study.domain.member.entity.QMember.member;
import static org.springframework.util.StringUtils.hasText;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 회원 조건 검색
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

    // 회원 조건 검색 (페이징)
    @Override
    public Page<SearchMemberDto> getMemberPage(MemberSearchCondition condition, Pageable pageable) {

        List<SearchMemberDto> content = queryFactory
                .select(
                        new QSearchMemberDto(
                                member.id.as("member_id"),
                                member.nickname,
                                member.email,
                                member.createdAt)
                )
                .from(member)
                .where(
                        transformMemberSearchCondition(condition),
                        member.status.eq(MemberStatus.ACTIVE)
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


    // 회원 정보 수정 (본인만) - 회원 정보 조회
    @Override
    public Optional<EditMemberDto> getEditMember(Long memberId) {
        
        EditMemberDto editMember = queryFactory
                .select(new QEditMemberDto(
                        member.id.as("memberId"),
                        member.email,
                        member.createdAt,
                        member.nickname,
                        member.mobile)
                )
                .from(member)
                .where(
                        member.id.eq(memberId)
                )
                .fetchOne();
        // Optional 반환 - 본인의 Id 값이 아닐 수 있음 (변조)
        return Optional.ofNullable(editMember);
    }




    // 회원 로그인 / =================== 삭제 예정 =====================
    @Override
    public Member login(String loginId, String password) {
        return queryFactory.select(member)
                .from(member)
                .where(
                        member.email.eq(loginId),
                        member.password.eq(password)
                )
                .fetchOne();
    }

    // 정렬 조건 변환
    private OrderSpecifier<?> memberSort(Pageable pageable) {

        if (pageable.getSort().isSorted()) {

            for (Sort.Order order : pageable.getSort()) {

                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "nickname" :
                        return new OrderSpecifier<>(direction, member.nickname);
                    case "email" :
                        return new OrderSpecifier<>(direction, member.email);
                    case "id":
                        return new OrderSpecifier<>(direction, member.id);
                }
            }
        }
        return null;
    }

    // 조건에 맞는 회원 수
    private JPAQuery<Long> countQuery(MemberSearchCondition condition) {

        return queryFactory
                .select(member.count())
                .from(member)
                .where(
                        transformMemberSearchCondition(condition),
                        member.status.eq(MemberStatus.ACTIVE)
                );
    }

    // 검색 조건 변환
    private BooleanExpression transformMemberSearchCondition(MemberSearchCondition condition) {

        if (hasText(condition.getSearchType())) {
            String searchType = condition.getSearchType(); // 검색 조건
            String searchWord = condition.getSearchWord(); // 검색어

            return switch (searchType) {
                case "nickname" -> member.nickname.containsIgnoreCase(searchWord);
                case "email" -> member.email.containsIgnoreCase(searchWord);
                default -> null; // 검색 조건이 있으나 사전에 정의된 조건이 아닌 경우
            };
        } else {
            return null;
        }
    }

    
    // ---
    private BooleanExpression usernameEq(String nickname) {
        return hasText(nickname) ? member.nickname.containsIgnoreCase(nickname) : null;
    }

    private Predicate emailEq(String email) {
        return hasText(email) ? member.email.containsIgnoreCase(email) : null;
    }
    
}
