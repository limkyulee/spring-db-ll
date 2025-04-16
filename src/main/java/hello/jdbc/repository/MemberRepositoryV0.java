package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;

/**
 * JDBC - DriverManager 사용
 */

@Slf4j
public class MemberRepositoryV0 {
    // FIXME : 멤버 저장
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values (?,?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        // DB 커낵션 획득.
        conn = getConnection();
        try {
            // DB 에 전달할 SQL 및 데이터 셋팅
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());

            // executeUpdate | 데이터를 변경할 때 사용
            pstmt.executeUpdate(); // 실제 Query 실행.
            return member;
        } catch (SQLException e) {
            log.error("[DB Error] ", e);
            throw new RuntimeException(e);
        }finally {
            // 쿼리 실행 후 리소스 정리.
            // 리소스 정리를 해주지 않을 경우 리소스 누수가 발생할 수 있음.
            close(conn, pstmt, null);
        }
    }

    // FIXME : 멤버 조회
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        /*
         * ResultSet
         * SelectQuery 결과가 순서대로 들어감.
         * rs.next() | 커서를 다음으로 이동. 데이터가 있을 경우 true 반환. (초기 데이터 조회를 위해 한 번 실행해주어야함)
         */
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);

            // executeQuery | 데이터를 조회할 때 사용.
            rs = pstmt.executeQuery(); // 실제 Query 실행.

            // 한 번만 호출하면 되기 때문에 IF 문 사용.
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

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        if(rs != null) {
            try catch (SQLException e){
                log.error("[DB ResultSet Error] ", e);
            }
        }

        if(pstmt != null) {
            try {
                pstmt.close();
            }catch (SQLException e) {
                log.error("[DB PreparedStatement Error] ", e);
            }
        }

        if(conn != null) {
            try{
                conn.close();
            }catch (SQLException e) {
                log.error("[DB Connection Error] ", e);
            }
        }
    }

    // 업데이트
    public void update(String memberId, Integer money) {
        String sql = "update member set money = ? where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int resultSize = pstmt.executeUpdate(); // 쿼리를 실행하고 해당 쿼리의 영향을 받은 row 수를 반환.
            log.info("[DB Update Success] resultSize = " + resultSize);
        }catch (SQLException e){
            log.error("[DB Connection Error] ", e);
            throw new RuntimeException(e);
        }finally {
            close(conn, pstmt, null);
        }
    }

    // 삭제
    public void delete(String memberId) {
        String sql = "delete from member where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);

            int resultSize = pstmt.executeUpdate(); // 쿼리를 실행하고 해당 쿼리의 영향을 받은 row 수를 반환.
            log.info("[DB Update Success] resultSize = " + resultSize);
        }catch (SQLException e){
            log.error("[DB Connection Error] ", e);
            throw new RuntimeException(e);
        }finally {
            close(conn, pstmt, null);
        }
    }

    private static Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

}
