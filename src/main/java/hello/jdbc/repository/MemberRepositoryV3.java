package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * 트랜잭션 - 트랜잭션 매니저
 * DataSourceUtils.getConnection()
 * DatasourceUtils.releaseConnection()
 */

@Slf4j
@AllArgsConstructor
public class MemberRepositoryV3 {
    private final DataSource dataSource;

    // FIXME : 멤버 저장
    public Member save(Member member){
        String sql = "insert into member(member_id, money) values (?,?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // 트랜잭션 동기화 매니저에서 커낵션 조회.
            conn = getConnection();

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());

            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("[DB Error] ", e);
            throw new RuntimeException(e);
        }finally {
            close(conn, pstmt, null);
        }
    }

    // FIXME : 멤버 조회
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if(rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else{
                throw new NoSuchElementException("member not found memberId : " + memberId);
            }
        }catch (SQLException e){
            log.error("[DB Error] ", e);
            throw new RuntimeException(e);
        }finally {
            close(conn, pstmt, rs);
        }
    }

    // FIXME : 멤버 업데이트
    public void update(String memberId, Integer money) {
        String sql = "update member set money = ? where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("[DB Update Success] resultSize = " + resultSize);
        }catch (SQLException e){
            log.error("[DB Connection Error] ", e);
            throw new RuntimeException(e);
        }finally {
            close(conn, pstmt, null);
        }
    }

    // FIXME : 멤버 삭제
    public void delete(String memberId) {
        String sql = "delete from member where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("[DB Update Success] resultSize = " + resultSize);
        }catch (SQLException e){
            log.error("[DB Connection Error] ", e);
            throw new RuntimeException(e);
        }finally {
            close(conn, pstmt, null);
        }
    }

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(pstmt);
        // PLUS : 트랜잭션 동기화 사용을 위해 DataSourceUtils 를 사용해야함. | 트랜잭션 동기화 매니저
        // 트랙잭션을 사용하기 위해 동기화된 커낵션은 커낵션을 닫지 않고 그대로 유지해줌.
        // 트랙잭션 매니저가 관리하는 커낵션이 아닌 경우 해당 커넥션을 닫음.
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    private Connection getConnection() throws SQLException {
        // PLUS : 트랜잭션 동기화 사용을 위해 DataSourceUtils 를 사용해야함. | 트랜잭션 동기화 매니저.
        Connection conn = DataSourceUtils.getConnection(dataSource);
        log.info("[DB Connection Success] conn = {}, class = {}",conn, conn.getClass());

        return conn;
    }
}
