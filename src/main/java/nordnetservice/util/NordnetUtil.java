package nordnetservice.util;

import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.YearMonthDTO;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class NordnetUtil {

    private final static ZoneId zoneId = ZoneId.of("Europe/Oslo");

    public static long calcUnixTimeForThirdFriday(YearMonthDTO yearMonth) {
        var thirdFriday = StockOptionUtil.thirdFriday(yearMonth.year(), yearMonth.month());

        var ldt = LocalDateTime.of(thirdFriday.getYear(), thirdFriday.getMonth(), thirdFriday.getDayOfMonth(), 0, 0, 0);

        var offset = zoneId.getRules().getOffset(ldt);

        return ldt.toInstant(offset).toEpochMilli();
    }

    public static URL urlFor(StockOptionTicker ticker ) {
        try {
            var info = StockOptionUtil.stockOptionInfoFromTicker(ticker);
            return new URL ("https","www.nordnet.no", pathQueryFor(info.getStockTicker(), info.getNordnetMillis()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL urlFor(StockTicker ticker, long nordnetMillis) {
        try {
            return new URL ("https","www.nordnet.no", pathQueryFor(ticker, nordnetMillis));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String pathQueryFor(StockTicker ticker, long nordnetMillis) {
        return String.format("/derivat/opsjoner/liste?currency=NOK&underlyingSymbol=%s&expireDate=%d",  ticker.ticker(), nordnetMillis);
        //return String.format("/market/options?currency=NOK&underlyingSymbol=%s&expireDate=%d",  ticker.ticker(), nordnetMillis);
        //https://www.nordnet.no/derivat/opsjoner/liste?currency=NOK&underlyingSymbol=YAR&expireDate=1766098800000
    }
    /*
   scheme:[//authority]path[?query][#fragment]
     */
}
