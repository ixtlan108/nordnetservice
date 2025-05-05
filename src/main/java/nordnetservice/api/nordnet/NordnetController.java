package nordnetservice.api.nordnet;

import nordnetservice.api.nordnet.dto.OpeningPriceDTO;
import nordnetservice.api.nordnet.response.CallsResponse;
import nordnetservice.api.nordnet.response.StockOptionDTO;
import nordnetservice.api.nordnet.response.StockPriceDTO;
import nordnetservice.api.response.DefaultResponse;
import nordnetservice.api.response.AppStatusCode;
import nordnetservice.api.response.PayloadResponse;
import nordnetservice.api.util.ApiUtil;
import nordnetservice.domain.core.Core;
import nordnetservice.domain.functional.Either;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.dto.YearMonthDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:8080")
@Controller
@RequestMapping("/nordnet")
public class NordnetController {

    private final Core core;

    public NordnetController(Core core) {
        this.core = core;
    }

    /*
    @GetMapping(value = "/openingprice/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PayloadResponse<OpeningPriceDTO>> openingPrice(@PathVariable("oid") int oid) {
        var ticker = new StockTicker(oid);
        var price = core.openingPrice(ticker);
        return ApiUtil.mapWithFn(price, OpeningPriceDTO::new);
    }

     */

    @GetMapping(value = "/spot/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PayloadResponse<StockPriceDTO>> spot(@PathVariable("oid") int oid) {
        var ticker = new StockTicker(oid);
        var price = core.getStockPrice(ticker);
        return ApiUtil.mapWithFn(price, StockPriceDTO::new);
    }

    @GetMapping(value = "/calls/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PayloadResponse<CallsResponse>> calls(@PathVariable("oid") int oid) {
        var ticker = new StockTicker(oid);
        var result= core.getStockPrice(ticker).andThen(stockPrice ->
            core.getCalls(ticker).andThen(opx -> {
                return Either.right(new CallsResponse(new StockPriceDTO(stockPrice), map(opx)));
        }));
        return ApiUtil.map(result);
    }

    @GetMapping(value = "/puts/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PayloadResponse<CallsResponse>> puts(@PathVariable("oid") int oid) {
        var ticker = new StockTicker(oid);
        var result= core.getStockPrice(ticker).andThen(stockPrice ->
                core.getPuts(ticker).andThen(opx -> {
                    return Either.right(new CallsResponse(new StockPriceDTO(stockPrice), map(opx)));
                }));
        return ApiUtil.map(result);
    }

    @PostMapping(value = "/thirdfriday/nordnetmillis", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> thirdFridayNordnetMillis(@RequestBody List<YearMonthDTO> items) {
        return ApiUtil.mapWithErrFn(core.thirdFridayMillis(items), HttpStatus.OK, "thirdFriday NordnetMillis ok");
    }

    @GetMapping(value = "/caffeine/reset", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> resetCaffeine() {
        core.resetCaffeine();
        return ResponseEntity.ok(new DefaultResponse(AppStatusCode.OK, "ok"));
    }

    /*
    @PostMapping(value = "/thirdfriday/demo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void thirdFridayDemo(@RequestBody List<YearMonthDTO> items, HttpServletResponse response) {
        System.out.println(response);
        //response.setStatus(HttpStatus.NO_CONTENT.value());
        System.out.println(items);
    }
     */



    private List<StockOptionDTO> map(List<StockOption> options) {
        return options.stream().map(StockOptionDTO::new).toList();
    }
}

/*
@RestController
public class RepoController {
    @RequestMapping(value = "/document/{id}", method = RequestMethod.GET)
    public Object getDocument(@PathVariable long id, HttpServletResponse response) {
       Object object = getObject();
       if( null == object ){
          response.setStatus( HttpStatus.SC_NO_CONTENT);
       }
       return object ;
    }
}

@RestController
public class RepoController {
    @RequestMapping(value = "/document/{id}", method = RequestMethod.GET)
    public Object getDocument(@PathVariable long id) {
       Object object = getObject();
       if ( null == object ){
          return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
       }

       return object ;
    }
}

package your.package.filter;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NoContentFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        if (httpServletResponse.getContentType() == null ||
                httpServletResponse.getContentType().equals("")) {
            httpServletResponse.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }
}

@Aspect
public class NoContent204HandlerAspect {

  @Pointcut("execution(public * xx.xxxx.controllers.*.*(..))")
  private void anyControllerMethod() {
  }

  @Around("anyControllerMethod()")
  public Object handleException(ProceedingJoinPoint joinPoint) throws Throwable {

    Object[] args = joinPoint.getArgs();

    Optional<HttpServletResponse> response = Arrays.asList(args).stream().filter(x -> x instanceof HttpServletResponse).map(x -> (HttpServletResponse)x).findFirst();

    if (!response.isPresent())
      return joinPoint.proceed();

    Object retVal = joinPoint.proceed();
    if (retVal == null)
      response.get().setStatus(HttpStatus.NO_CONTENT.value());

    return retVal;
  }
}

 */
