package mystudy.study.domain.member.repository;

import mystudy.study.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    // 사용자 이름으로 조회
    Optional<Member> findByUsername(String username);

    // 사용자 email 로 조회 (아이디)
    Optional<Member> findByEmail(String email);
}
