package nordnetservice.domain.downloader;


import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOptionInfo;
import nordnetservice.domain.stockoption.StockOptionTicker;

import java.util.List;

public interface Downloader<T> {
    List<T> download(StockTicker ticker);
    T download(StockOptionTicker ticker);
    //T downloadOne(StockTicker ticker);
    //T downloadOne(StockOptionInfo info);
}
