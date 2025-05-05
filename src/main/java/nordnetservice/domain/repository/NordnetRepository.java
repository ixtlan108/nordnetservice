package nordnetservice.domain.repository;

import nordnetservice.domain.stock.OpeningPrice;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.Tuple2;

import java.util.List;

public interface NordnetRepository {
    List<StockOption> getCalls(StockTicker ticker);
    List<StockOption> getPuts(StockTicker ticker);
    StockPrice getStockPrice(StockTicker ticker);
    Tuple2<StockPrice,StockOption> findOption(StockOptionTicker ticker);
    //OpeningPrice openingPrice(StockTicker ticker);
    void resetCaffeine();
}
