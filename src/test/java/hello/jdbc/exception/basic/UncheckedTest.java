package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw(){
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyUncheckedException.class);
    }

    /**
     * 1. 언체크 예외 생성.
     * 컴파일러가 예외를 체크하지 않는 예외임을 의미.
     * RuntimeException 을 상속받은 예외는 언체크 예외가 됨.
     */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * UnChecked 예외는
     * 예외를 잡거나, 던지지 않아도 됨.
     * 예외를 잡지 않으면 자동으로 밖으로 던짐.
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 3-1.
         * 필요한 경우 예외를 잡아서 처리하면 됨.
         */
        public void callCatch() {
            try{
                repository.call();
            }catch (MyUncheckedException e){
                // 예외 처리 로직.
                log.info("[예외 처리] message {}", e.getMessage(), e);
            }
        }

        /**
         *
         * 3-2.
         * 예외를 잡지 않아도 됨. 자연스럽게 상위로 넘어감.
         * 체크 예외와 다르게 throws 예외 선언을 하지 않아도 됨.
         */
        public void callThrow() {
            repository.call();
        }
    }

    /**
     * 2. repository 에서 예외를 발생 시킴.
     */
    static class Repository {
        public void call() {
            throw new MyUncheckedException("UnCheckedException");
        }
    }
}
