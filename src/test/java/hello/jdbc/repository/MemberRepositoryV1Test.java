package hello.jdbc.repository;

import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.URL;
import static hello.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 memberRepositoryV1;

    @BeforeEach
    void beforeEach(){
        // 기본 DriverManager - 항상 새로운 커넥션을 획득
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // 커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        memberRepositoryV1 = new MemberRepositoryV1(dataSource);
    }

    // 같은 값으로 반복 실행 시, Unique index or primary key violation 에러 발생.
    // RESULT : 모든 테스트가 conn0 만 사용하는 것을 확인할 수 있음.
    @Test
    void crud() {
        // save
        Member member = new Member("memberV4", 10_000);
        memberRepositoryV1.save(member);

        // findById
        Member findMember = memberRepositoryV1.findById(member.getMemberId());
        log.info("find member : {}", findMember);
        assertEquals(member, findMember);

        // update: money
        memberRepositoryV1.update(member.getMemberId(), 20_000);
        Member updateMember = memberRepositoryV1.findById(member.getMemberId());
//        assertThat(updateMember).isEqualTo(member);
        assertThat(updateMember.getMoney()).isEqualTo(20_000);

        // delete
        memberRepositoryV1.delete(member.getMemberId());
        Assertions.assertThatThrownBy(() -> memberRepositoryV1.findById(member.getMemberId())).isInstanceOf(
            NoSuchElementException.class);
//        Member deleteMember = repositoryV0.findById(member.getMemberId());
//        assertNull(deleteMember);

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}