package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedAppTest {

    @Test
    void checked(){
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);

    }

    /**
     * 4. 체크 예외를 처리하지 못하고 밖으로 던짐. | 의존 관게에 대한 문제.
     * 여기서 처리할 수 없어도 throws 를 선언해주어야함. | 예외 기술이 바뀔 경우 코드 변경이 불가피함.
     * Exception 에 의존하게 됨. | 불필요한 의존 관계 발생.
     */
    static class Controller {
        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    /**
     * 3. 체크 예외를 처리하지 못하고 밖으로 던짐. | 의존 관계에 대한 문제.
     * 여기서 처리할 수 없어도 throws 를 선언해주어야함. | 예외 기술이 바뀔 경우 코드 변경이 불가피함.
     * Exception 에 의존하게 됨. | 불필요한 의존 관계 발생.
     */
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    /*
     * ANTI PATTERN
     * throws Exception
     * > 최상위 타입을 에외로 던질 경우 그 하위의 모든 체크 예외를 밖으로 던지는 것.
     * > 체크 예외의 기능을 무효화 하는 것과 같음.
     */

    /**
     * 2. 체크 예외 생성. | 복구 불가능한 예외.
     */
    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("Connection failed");
        }
    }

    /**
     * 1. 체크 예외 생성 | 복구 불가능한 예외.
     */
    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("SQLException");
        }
    }
}
