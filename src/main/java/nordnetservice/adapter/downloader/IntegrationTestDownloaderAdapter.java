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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Profile({"test", "integrationx"})
public class IntegrationTestDownloaderAdapter implements Downloader<PageInfo> {
    private final WebClient client;
    private final String testUrl;
    private final String testUrl2;

    public IntegrationTestDownloaderAdapter(@Value("${url.test}") String testUrl,
                                            @Value("${url.test.2:#{null}}") String testUrl2) {
        System.out.println(testUrl);
        System.out.println(testUrl2);
        this.testUrl = testUrl;
        this.testUrl2 = testUrl2;

        this.client = new WebClient();
        this.client.getOptions().setJavaScriptEnabled(false);
    }

    private List<PageInfo> result = null;

    @Override
    public List<PageInfo> download(StockTicker ticker) {
        if (result == null) {

            try {
                var page = client.getPage(testUrl);
                var content = page.getWebResponse().getContentAsString();
                var info = new PageInfo(content);

                if (testUrl2 == null) {
                    result = Collections.singletonList(info);
                } else {
                    var page2 = client.getPage(testUrl2);
                    var content2 = page2.getWebResponse().getContentAsString();
                    var info2 = new PageInfo(content2);
                    result = Arrays.asList(info, info2);
                }
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
        return null;
    }

    private PageInfo downloadOne() {
        try {
            var page = client.getPage(testUrl);
            var content = page.getWebResponse().getContentAsString();
            return new PageInfo(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
