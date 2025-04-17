package hello.jdbc.exception.translator;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDBException;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ExTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    void init(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicateKeyException(){
        service.create("myId");
        service.create("myId");
    }

    @Slf4j
    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;

        public void create(String memberId){
            try{
                repository.save(new Member(memberId, 0));
                log.info("Member {} created", memberId);
            }catch(MyDuplicateKeyException e){
                log.info("Member {} already exists", memberId);
                String retryId = generateNewId(memberId);
                log.info("Retry id {}", retryId);
                repository.save(new Member(retryId, 0));
            }catch (MyDBException e){
                log.info("데이터 접근 계층 예외", e);
                throw  e;
            }
        }

        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(1000);
        }
    }

    @RequiredArgsConstructor
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member)  {
            String sql = "insert into member(member_id, money) values(?, ?)";

            Connection conn = null;
            PreparedStatement ps = null;

            try{
                conn = dataSource.getConnection();
                ps = conn.prepareStatement(sql);
                ps.setString(1, member.getMemberId());
                ps.setInt(2, member.getMoney());
                ps.executeUpdate();

                return member;
            }catch (SQLException e){
                // h2 db
                if(e.getSQLState().equals("23505")){
                    throw new MyDuplicateKeyException(e);
                }
                throw new MyDBException(e);
            }finally {
                JdbcUtils.closeStatement(ps);
                JdbcUtils.closeConnection(conn);
            }
        }
    }
}
