package nordnetservice.adapter;

import nordnetservice.adapter.nordnet.NordnetAdapterV2;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionTicker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@ActiveProfiles("integration")
@SpringBootTest
class NordnetAdapterV2Test {


    private final StockTicker stockTicker = new StockTicker("YAR");

    @Autowired
    @Qualifier("v2")
    NordnetAdapterV2 nordnetAdapter;

    @BeforeEach
    void init() {
    }

    @Test
    void test_parse() {
        var stockPrice = nordnetAdapter.getStockPrice(stockTicker);
        checkStockPrice(stockPrice);
        var calls = nordnetAdapter.getCalls(stockTicker);
        assertEquals(37, calls.size());
        var puts = nordnetAdapter.getPuts(stockTicker);
        assertEquals(37, puts.size());

        checkStockOption("YAR4F390", 0.95, 2.10, 390, calls);
        checkStockOption("YAR4F340", 14.25, 16.00, 340, calls);
        checkStockOption("YAR4F310", 34.50, 38.75, 310, calls);
        checkStockOption("YAR4F220", 115.50, 130.50, 220, calls);

        checkStockOption("YAR4R390", 49.25, 55.00, 390, puts);
        checkStockOption("YAR4R340", 14.00, 15.50, 340, puts);
        checkStockOption("YAR4R310", 4.30, 5.70, 310, puts);
        checkStockOption("YAR4R220", 0.01, 1.40, 220, puts);
    }

    @Test
    void test_find_option() {

        var ticker = new StockOptionTicker("YAR4F370");
        var option = nordnetAdapter.findOption(ticker);
        assertNotNull(option);
        checkStockPrice(option.first());
        checkStockOption(option.second(),3.50, 4.60, 370, 0.1, 0.29, true);


        var ticker2 = new StockOptionTicker("YAR4R370");
        var option2 = nordnetAdapter.findOption(ticker2);
        assertNotNull(option2);
        checkStockOption(option2.second(),32.75, 36.25, 370, 0.75, 0.83, true);
    }

    @Test
    void test_opening_price() {
        var price = nordnetAdapter.openingPrice(stockTicker);
        assertEquals(341.9, price.price(), 0.1);
    }

    private void checkStockPrice(StockPrice stockPrice) {
        assertEquals(403.0, stockPrice.opn(), 0.1);
        assertEquals(343.1, stockPrice.hi(), 0.1);
        assertEquals(327.4, stockPrice.lo(), 0.1);
        assertEquals(341.9, stockPrice.cls(), 0.1);
    }

    private void checkStockOption(StockOption option,
                                  double bid,
                                  double ask,
                                  double x,
                                  double ivBid,
                                  double ivAsk,
                                  boolean skipIv) {
        assertEquals(bid, option.getBid(), 0.01);
        assertEquals(ask, option.getAsk(), 0.01);
        assertEquals(x, option.getX(), 0.001);
        if (!skipIv) {
            assertEquals(ivBid, option.getIvBid(), 0.01);
            assertEquals(ivAsk, option.getIvAsk(), 0.01);
        }
    }

    private void checkStockOption(String tickerS,
                                     double bid,
                                     double ask,
                                     double x,
                                     List<StockOption> options) {

        var opt = options.stream().filter(s -> s.getTicker().value().equals(tickerS)).findFirst();
        assertFalse(opt.isEmpty());
        var o = opt.get();
        assertEquals(bid, o.getBid(), 0.01);
        assertEquals(ask, o.getAsk(), 0.01);
        assertEquals(x, o.getX(), 0.001);
    }
}
