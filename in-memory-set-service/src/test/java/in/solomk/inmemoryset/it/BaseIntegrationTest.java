package in.solomk.inmemoryset.it;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public abstract class BaseIntegrationTest {
    private static final AtomicInteger counter = new AtomicInteger();

    protected int nextItemValue() {
        return counter.incrementAndGet();
    }

    protected String nextItemValueAsString() {
        return String.valueOf(nextItemValue());
    }
}
