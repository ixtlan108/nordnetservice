package nordnetservice.adapter.downloader;

import nordnetservice.domain.stockoption.StockOptionInfo;
import org.htmlunit.WebClient;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOptionTicker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@Profile({"demo","docker"})
public class DemoDownloaderAdapter implements Downloader<PageInfo> {
    private final WebClient client;
    private final String urlDemoPath;

    public DemoDownloaderAdapter(@Value("${url.path}") String urlDemoPath) {
        System.out.println(urlDemoPath);

        this.urlDemoPath = urlDemoPath;
        this.client = new WebClient();
        this.client.getOptions().setJavaScriptEnabled(false);
    }

    private List<PageInfo> result = null;

    /*
    private List<PageInfo> downloadV2(StockTicker ticker) {
        if (result == null) {
            try {
                var page = client.getPage(testUrlV2);
                var content = page.getWebResponse().getContentAsString();
                var info = new PageInfo(content);
                result = Collections.singletonList(info);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
     */

    @Override
    public List<PageInfo> download(StockTicker ticker) {
        if (result == null) {

            try {
                var page = client.getPage(urlFor(ticker));
                var content = page.getWebResponse().getContentAsString();
                var info = new PageInfo(content);

                result = Collections.singletonList(info);

                /*
                if (testUrl2 == null) {
                    result = Collections.singletonList(info);
                } else {
                    var page2 = client.getPage(testUrl2);
                    var content2 = page2.getWebResponse().getContentAsString();
                    var info2 = new PageInfo(content2);
                    result = Arrays.asList(info, info2);
                }

                 */
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
        /*
        try {
            var uri = new URI(testUrl); //Path.of(testUrl).toUri();
            var req =
                    HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response = getClient().send(req, HttpResponse.BodyHandlers.ofString());
            var info = new PageInfo(response.body());
            return Collections.singletonList(info);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
         */
    }

    @Override
    public PageInfo download(StockOptionInfo info) {
        return download(info.getStockTicker()).getFirst();
    }

    private String urlFor(StockTicker ticker) {
        return String.format("%s/%s.html", urlDemoPath, ticker.ticker());
    }
}
