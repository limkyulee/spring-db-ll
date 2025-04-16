package hello.jdbc.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import hello.jdbc.domain.Member;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repositoryV0 = new MemberRepositoryV0();

    // 같은 값으로 반복 실행 시, Unique index or primary key violation 에러 발생.
    @Test
    void crud() {
        // save
        Member member = new Member("memberV4", 10_000);
        repositoryV0.save(member);

        // findById
        Member findMember = repositoryV0.findById(member.getMemberId());
        log.info("find member : {}", findMember);
        assertEquals(member, findMember);

        // update: money
        repositoryV0.update(member.getMemberId(), 20_000);
        Member updateMember = repositoryV0.findById(member.getMemberId());
//        assertThat(updateMember).isEqualTo(member);
        assertThat(updateMember.getMoney()).isEqualTo(20_000);

        // delete
        repositoryV0.delete(member.getMemberId());
        Assertions.assertThatThrownBy(() -> repositoryV0.findById(member.getMemberId())).isInstanceOf(
            NoSuchElementException.class);
//        Member deleteMember = repositoryV0.findById(member.getMemberId());
//        assertNull(deleteMember);
    }

}