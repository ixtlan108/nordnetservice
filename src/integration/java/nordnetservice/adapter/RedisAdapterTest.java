package nordnetservice.adapter;

import nordnetservice.domain.stock.StockTicker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// @ActiveProfiles("test")

@SpringBootTest
public class RedisAdapterTest {

    @Autowired
    private RedisAdapter redisAdapter;

    @Test
    void test_opening_price() {
        var result = redisAdapter.openingPrice(new StockTicker("YAR"));
        assertNotNull(result);
        assertEquals(403.0, result, 0.01);
    }

    @Test
    void test_nordnet_millis() {
        var actual = redisAdapter.nordnetMillisForUrl(LocalDate.of(2024,1,10));
        assertEquals(3, actual.size());
    }

    /*
    @Test
    void test_redis_url() {
        var result = redisAdapter.fetchValue("test-url");
        var expected = "file:////home/rcs/opt/java/nordnetservice/test/resources/html/yar.html";
        assertEquals(expected, result);
    }
     */
}
