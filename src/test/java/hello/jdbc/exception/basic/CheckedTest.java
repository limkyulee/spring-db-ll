package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedTest {

    @Test
    void checked_catch(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw(){
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }

    /**
     * 1. 체크 예외 생성.
     * Exception 을 상속받은 예외는 체크 예외가 됨.
     */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * Checked 예외는
     * 예외를 잡아서 처리하거나, 던지거나 둘 중 하나를 필수로 선택해야함.
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 3-1. 예외를 잡아서 처리.
         */
        public void callCatch() {
            try{
                repository.call();
            }catch (MyCheckedException e) {
                // 예외 처리 로직.
                log.info("[예외 처리] message {}", e.getMessage(), e);
            }
        }

        /**
         * 3-2. repository 에서 받은 예외를 밖으로 덤짐.
         * 체크 예외는 예외를 잡지 않고 밖으로 던지면 throws 예외를 메서드에 필수로 선언해야함.
         * @throws MyCheckedException
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    /**
     * 2. repository 에서 예외 발생시킴.
     */
    static class Repository{
        public void call() throws MyCheckedException {
            throw new MyCheckedException("MyCheckedException");
        }
    }

}
