package roomescape.domain.member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    boolean existsByEmail(String email);

    Member save(Member member);

    Optional<Member> findByEmail(String email);

    Optional<Member> findById(long id);

    List<Member> findAll();
}
