package nordnetservice.api.rapanui;

import nordnetservice.api.rapanui.response.FindOptionItem;
import nordnetservice.api.rapanui.response.FindOptionResponse;
import nordnetservice.api.response.AppStatusCode;
import nordnetservice.api.response.DefaultResponse;
import nordnetservice.api.response.PayloadResponse;
import nordnetservice.api.util.ApiUtil;
import nordnetservice.domain.core.Core;
import nordnetservice.domain.stockoption.StockOptionTicker;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8080")
@Controller
@RequestMapping("/rapanui")
public class RapanuiController {

    private final Core core;

    public RapanuiController(Core core) {
        this.core = core;
    }

    @ResponseBody
    @GetMapping(value = "/stockoption/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PayloadResponse<FindOptionResponse>> findOption(@PathVariable("ticker") String ticker) {

        var result = core.findOption(new StockOptionTicker(ticker));

        return ApiUtil.mapWithFn(result, r -> {
            var stock = r.first();
            var opt = r.second();
            return new FindOptionResponse(new FindOptionItem(opt.getBid(),
                    opt.getAsk(),
                    stock.cls(),
                    opt.getDays(),
                    opt.getX(),
                    opt.getIvBid()),
                    0,
                    null);
        });

    }

    @ResponseBody
    @GetMapping(value = "/toggleAccrule/{oid}/{isActive}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> toggleAccRule(@PathVariable("oid") int oid,
                                                         @PathVariable("isActive") boolean isActive) {
        System.out.println(String.format("oid: %d, isActive: %s", oid,isActive));
        return ResponseEntity.ok(new DefaultResponse(AppStatusCode.OK, "ok"));
    }
}
