package hello.jdbc.connection;

import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.URL;
import static hello.jdbc.connection.ConnectionConst.USERNAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

/*
 * DriverManager
 *  > JDBC 가 제공하는 DB 연결을 위한 드라이버
 *  > 라이브러리에 있는 데이터베이스 드라이버를 찾아서 해당 드라이버가 제공하는 커넥션을 반환함.
 *  > EX) H2 데이터베이스 드라이버가 작동하여 실제 데이터베이스와 커넥션을 맺고 그 결과를 반환함.
 */
@Slf4j
public class DBConnectionUtil {
     public static Connection getConnection() {
         try {

             // PLUS : org.h2.jdbc.JdbcConnection
             Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             log.info("get connection {}, class {}", connection, connection.getClass());

             return connection;
         } catch (SQLException e) {
             throw new IllegalStateException(e);
         }
     }
}
