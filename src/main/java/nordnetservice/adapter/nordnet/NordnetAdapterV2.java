package nordnetservice.adapter.nordnet;

import nordnetservice.adapter.RedisAdapter;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vega.financial.calculator.OptionCalculator;

@Component("v2")
public class NordnetAdapterV2 extends NordnetAdapterV1 {
    public NordnetAdapterV2(Downloader<PageInfo> downloaderAdapter,
                            RedisAdapter redisAdapter,
                            @Qualifier("blackScholes") OptionCalculator blackScholes,
                            @Qualifier("binomialTree") OptionCalculator binomialTree,
                            @Value("${curdate:#{null}}") String curDateStr,
                            @Value("${curdate.v2:#{null}}") String curDateTest,
                            @Value("${cache.options.expiry}") int optionsExpiry,
                            @Value("${cache.option.expiry}") int optionExpiry,
                            @Value("${redis.fetchOpeningPrice}") boolean fetchOpeningPrice) {
        super(downloaderAdapter,
                redisAdapter,
                blackScholes,
                binomialTree,
                curDateStr,
                curDateTest,
                optionsExpiry,
                optionExpiry,
                fetchOpeningPrice);
        SP_CLS = 3;
        SP_HI = 6;
        SP_LO = 7;
    }
}
