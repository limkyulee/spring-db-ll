package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedAppTest {

    @Test
    void unchecked(){
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }

    /**
     * Throwable | 스택 트레이스의 중요성.
     * 기존 예외를 포함하지 않으면 어떤 원인으로 예외가 발생했는데 확인할 수 없는 문제 발생.
     */
    @Test
    void printEx(){
        Controller controller = new Controller();

        try {
            controller.request();
        }catch (Exception e){
            log.info("[Exception]", e);
            e.printStackTrace(); // 스택 트레이스 출력.
        }
    }

    static class Controller {
        Service service = new Service();

        /**
         * 5. 예외 발생. | Exception 예외 던짐.
         */
        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        /**
         * 4. 예외 발생. | 예외 던짐.
         */
        public void logic()  {
            repository.call();
            networkClient.call();
        }
    }

    /**
     * 3. 기존 체크 예외를 RuntimeConnectException 으로 발생하도록 함.
     */
    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("Connection failed");
        }
    }

    static class Repository {
        /**
         * 2. 발생한 체크 예외를 RuntimeSQLException 으로 전환하여 예외를 던짐.
         */
        public void call() {
            try{
                runSQL();
            }catch (SQLException e){
                throw new RuntimeSQLException(e);
            }
        }

        /**
         * 1. 체크 예외인 SQLException 발생.
         * @throws SQLException
         */
        private void runSQL() throws SQLException {
            throw new SQLException("SQL Exception");
        }
    }

    /**
     * 0. 언체크 예외 생성.
     */
    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    /**
     * 0. 언체크 예외 생성.
     */
    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(String message) {

        }

        // PLUS : Throwable | 기존 예외를 포함하여 예외를 던짐.
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
