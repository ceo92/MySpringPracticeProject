package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {
    @Test
    void aa(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw(){
        Service service = new Service();

        Assertions.assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyUncheckedException.class);
    }


    /**
     * RuntimeEXCEPTION을 상속받은 예외는 언체크 예외
     */
    static class MyUncheckedException extends RuntimeException{
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * throws 안 적어줘도 됨
     */
    static class Service{
        Repository repository = new Repository();

        public void callCatch(){
            try {
                repository.call();
            }catch (MyUncheckedException e){
                log.info("예외 처리 , message={}", e.getMessage(), e);
            }
        }

        //throws 안 적어줘도 알아서 발생한 예외 던짐 밖으로 ㅇㅇ
        public void callThrow(){
            repository.call();
        }
    }

    static class Repository{
        public void call(){
            throw new MyUncheckedException("ex");
        }
    }

}
