package nordnetservice.domain.core;

import nordnetservice.adapter.CritterAdapter;
import nordnetservice.adapter.RedisAdapter;
import nordnetservice.critter.stockoption.StockOptionPurchase;
import nordnetservice.domain.error.ApplicationError;
import nordnetservice.domain.error.GeneralError;
import nordnetservice.domain.error.SqlError;
import nordnetservice.domain.functional.Either;
import nordnetservice.domain.repository.NordnetRepository;
import nordnetservice.domain.stock.OpeningPrice;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.PurchaseType;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.Tuple2;
import nordnetservice.dto.YearMonthDTO;
import nordnetservice.util.NordnetUtil;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class Core {
    private final NordnetRepository nordnetRepository;
    private final CritterAdapter critterAdapter;
    private final RedisAdapter redisAdapter;

    public Core(@Qualifier("v2") NordnetRepository nordnetRepository,
                CritterAdapter critterAdapter,
                RedisAdapter redisAdapter) {
        this.nordnetRepository = nordnetRepository;
        this.critterAdapter = critterAdapter;
        this.redisAdapter = redisAdapter;
    }


    /*
    @FunctionalInterface
    private interface HandleEitherCommand<T> {
        T handle() throws Exception;
    };
     */

    private <T> Either<ApplicationError,T> handle(Supplier<T> fn) {
        try {
            return Either.right(fn.get());
        }
        catch (MyBatisSystemException ex) {
            if (ex.getMessage() == null) {
                return Either.left(new SqlError.MybatisSqlError(ex.getCause().getMessage()));
            }
            else {
                return Either.left(new SqlError.MybatisSqlError(ex.getMessage()));
            }
        }
        catch (Exception ex) {
            return Either.left(new GeneralError.GeneralApplicationError(
                    String.format("(%s) %s", ex.getClass().getSimpleName(),ex.getMessage())));
        }
    }

    public Either<ApplicationError,Tuple2<StockPrice, StockOption>> findOption(StockOptionTicker ticker) {
        return handle(() -> nordnetRepository.findOption(ticker));
    }

    public Either<ApplicationError,List<StockOptionPurchase>> fetchCritters(PurchaseType purchaseType) {
        return handle(() -> critterAdapter.fetchCritters(purchaseType));
    }

    public Either<ApplicationError,List<StockOption>> getCalls(StockTicker ticker) {
        return handle(() -> nordnetRepository.getCalls(ticker));
    }

    public Either<ApplicationError,List<StockOption>> getPuts(StockTicker ticker) {
        return handle(() -> nordnetRepository.getPuts(ticker));
    }

    public Either<ApplicationError,StockPrice> getStockPrice(StockTicker ticker) {
        return handle(() -> nordnetRepository.getStockPrice(ticker));
    }

    /*
    public Either<ApplicationError,OpeningPrice> openingPrice(StockTicker ticker) {
        return handle(() -> nordnetRepository.openingPrice(ticker));
    }
     */

    public Optional<ApplicationError> thirdFridayMillis(List<YearMonthDTO> items) {
        try {
            var millis = items.stream().map(NordnetUtil::calcUnixTimeForThirdFriday).toList();
            redisAdapter.updateNordnetMillis(millis);
            return Optional.empty();
        }
        catch (Exception ex) {
            return Optional.of(new GeneralError.GeneralApplicationError(ex.getMessage()));
        }
    }

    public void resetCaffeine() {
        nordnetRepository.resetCaffeine();
    }
}
