package nordnetservice.adapter.downloader;

import nordnetservice.adapter.RedisAdapter;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOptionInfo;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.util.NordnetUtil;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("prod")
public class DefaultDownloaderAdapter  implements  Downloader<PageInfo> {
    private final Logger logger = LoggerFactory.getLogger(DefaultDownloaderAdapter.class);

    private HttpClient client;

    private final RedisAdapter redisAdapter;

    public DefaultDownloaderAdapter(RedisAdapter redisAdapter) {
        this.redisAdapter = redisAdapter;
    }

    @Override
    public List<PageInfo> download(StockTicker ticker) {
        var nordnetMillis = redisAdapter.nordnetMillisForUrl(LocalDate.now());

        var result = new ArrayList<PageInfo>();

        for (var nm : nordnetMillis) {
            try {
                var page = download(ticker, nm);
                result.add(page);
            }
            catch (DownloadException e) {
                logger.warn(String.format("(%s) Could not download, status: %d", e.getUrl(), e.getHttpStatus()));
            }
        }

        return result;
    }

    PageInfo download(StockTicker ticker, long nordnetMillis) {
        try {
            var url = NordnetUtil.urlFor(ticker, nordnetMillis);
            var req =
                    HttpRequest.newBuilder(url.toURI()).GET().build();
            //var h = new HttpHead(url.toString());
            HttpResponse<String> response = getClient().send(req, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new DownloadException(url, response.statusCode());
            }
            return new PageInfo(response.body());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageInfo download(StockOptionTicker ticker) {
        return null;
    }

    @Override
    public PageInfo downloadOne(StockTicker ticker) {
        var nordnetMillis = redisAdapter.nordnetMillisForUrl(LocalDate.now());
        return download(ticker, nordnetMillis.get(0));
    }

    @Override
    public PageInfo downloadOne(StockOptionInfo info) {
        return null;
    }

    private HttpClient getClient() {
        if (client == null) {
            client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        }
        return client;
    }
}
