package mystudy.study.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import mystudy.study.domain.dto.MemberSearchCondition;
import mystudy.study.domain.entity.Member;
import mystudy.study.domain.entity.QMember;
import org.springframework.util.StringUtils;

import java.util.List;

import static mystudy.study.domain.entity.QMember.member;
import static org.springframework.util.StringUtils.hasText;

public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


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

    private BooleanExpression usernameEq(String username) {
        return hasText(username) ? member.username.contains(username) : null;
    }

    private Predicate emailEq(String email) {
        return hasText(email) ? member.email.contains(email) : null;
    }
}
