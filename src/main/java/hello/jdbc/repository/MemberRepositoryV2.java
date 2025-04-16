package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * JDBC - ConnectionParam
 */

@Slf4j
@AllArgsConstructor
public class MemberRepositoryV2 {
    private final DataSource dataSource;

    // FIXME : 멤버 저장
    public Member save(Member member){
        String sql = "insert into member(member_id, money) values (?,?)";

        Connection conn = null;
        PreparedStatement pstmt = null;


        try {
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

    // FIXME : DB 트랜잭션 사용을 위해 같은 커낵션 유지.
    public Member findById(Connection conn, String memberId) {
        String sql = "select * from member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
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
            JdbcUtils.closeStatement(pstmt);
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

    // FIXME : DB 트랜잭션 사용을 위해 같은 커낵션 유지.
    public void update(Connection conn, String memberId, Integer money) {
        String sql = "update member set money = ? where member_id = ?";

        PreparedStatement pstmt = null;

        try{
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("[DB Update Success] resultSize = " + resultSize);
        }catch (SQLException e){
            log.error("[DB Connection Error] ", e);
            throw new RuntimeException(e);
        }finally {
            // 커낵션 유지가 필요하기 때문에 커낵션을 닫지 않음.
            JdbcUtils.closeStatement(pstmt);
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
        JdbcUtils.closeConnection(conn);
    }

    private Connection getConnection() throws SQLException {
        Connection conn  = dataSource.getConnection();
        log.info("[DB Connection Success] conn = {}, class = {}",conn, conn.getClass());

        return conn;
    }
}
