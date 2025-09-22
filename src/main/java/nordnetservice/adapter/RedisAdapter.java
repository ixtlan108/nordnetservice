package nordnetservice.adapter;


import nordnetservice.domain.stock.StockTicker;
import nordnetservice.util.StockOptionUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class RedisAdapter {

    private final RedisTemplate<String,Object> redisTemplate;

    public RedisAdapter(RedisTemplate<String,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Double openingPrice(StockTicker ticker) {
        var result = (String)redisTemplate.opsForHash().get("stockprice:open",
                String.format("%d",ticker.oid()));
        return result == null ? null : Double.parseDouble(result);
    }

    public List<Long> nordnetMillisForUrl(LocalDate cutOffDate)  {
        var result = new ArrayList<Long>();
        var cutOffMillis = StockOptionUtil.nordnetMillis(cutOffDate);
        var items = redisTemplate.opsForSet().members("expiry");
        for (var item : items) {
            var itemL = Long.parseLong((String)item);
            if (itemL > cutOffMillis) {
                result.add(itemL);
            }
        }
        return result;
    }

    public String fetchValue(String key) {
        return (String)redisTemplate.opsForValue().get(key);
    }

    public void updateNordnetMillis(List<Long> millis) {
        var items = millis.stream().map(Object::toString).toArray();
        redisTemplate.opsForSet().add("expiry", items);
    }

}

