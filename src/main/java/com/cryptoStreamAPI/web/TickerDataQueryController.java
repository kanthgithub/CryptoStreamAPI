package com.cryptoStreamAPI.web;


import com.cryptoStreamAPI.entity.TickerData;
import com.cryptoStreamAPI.model.HistoricDataModelWrapper;
import com.cryptoStreamAPI.service.TickerDataQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


//API to let users to see the bitcoin price movement for last week, last month, last year or any custom date.

@RestController
@Validated
@RequestMapping(path = "/crypto/")
public class TickerDataQueryController {


    @Autowired
    private TickerDataQueryService tickerDataQueryService;

    /**
     *  Extracts all tickerData entities recorded in elasticSearch
     *
     *  HTTPCode: 200 (Success) / 500 (Internal Server Error)
     *
     * @return all TickerData entities recorded in elasticSearch
     */
    @RequestMapping(path="historic/all", method = RequestMethod.GET)
    public ResponseEntity<List<TickerData>> getAllHistoricTickerDataEntities()  {

        List<TickerData> tickerDataList = tickerDataQueryService.getHistoricTickerData();

        return tickerDataList!=null ?  ResponseEntity.ok(tickerDataList) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     *  Extracts all tickerData entities for a day-range recorded in elasticSearch
     *
     *  HTTPCode: 200 (Success) / 500 (Internal Server Error)
     *
     * @return all TickerData entities recorded in elasticSearch
     */
    @RequestMapping(path="historic/days/{days}", method = RequestMethod.GET)
    public ResponseEntity<List<TickerData>> getAllHistoricTickerDataEntitiesByDaysRange(@PathVariable("days")  String numberOfDays)  {

        List<TickerData> tickerDataList = tickerDataQueryService.getTickerDataByPeriod(Long.valueOf(numberOfDays));

        return tickerDataList!=null ?  ResponseEntity.ok(tickerDataList) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     *  Extracts all tickerData entities for a date recorded in elasticSearch
     *
     *  HTTPCode: 200 (Success) / 500 (Internal Server Error)
     *
     * @return all TickerData entities recorded in elasticSearch
     */
    @RequestMapping(path="historic/date/{date}", method = RequestMethod.GET)
    public ResponseEntity<List<TickerData>> getAllHistoricTickerDataEntitiesByDate(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                                                    LocalDateTime date)  {

        List<TickerData> tickerDataList = tickerDataQueryService.getTickerDataByDate(date);

        return tickerDataList!=null ?  ResponseEntity.ok(tickerDataList) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     *  Extracts all tickerData entities for a date-range recorded in elasticSearch
     *
     *  HTTPCode: 200 (Success) / 500 (Internal Server Error)
     *
     * @return all TickerData entities recorded in elasticSearch
     */
    @RequestMapping(path="historic/dateRange/{fromDate}/{toDate}", method = RequestMethod.GET)
    public ResponseEntity<List<TickerData>> getAllHistoricTickerDataEntitiesByDateRange(@PathVariable("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                                                LocalDateTime fromDate,
                                                                                        @PathVariable("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                                                LocalDateTime toDate)  {

        List<TickerData> tickerDataList = tickerDataQueryService.getTickerDataByDateRange(fromDate,toDate);

        return tickerDataList!=null ?  ResponseEntity.ok(tickerDataList) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     *  Extracts all tickerData entities from coinBase-API
     *
     *  HTTPCode: 200 (Success) / 500 (Internal Server Error)
     *
     * @return all TickerData entities from Coinbase-API
     */
    @RequestMapping(path="historic/api", method = RequestMethod.GET)
    public ResponseEntity<HistoricDataModelWrapper> getAllTickerDataEntitiesFromAPI()  {

        HistoricDataModelWrapper tickerDataFromAPI = tickerDataQueryService.getTickerDataFromAPI();

        return tickerDataFromAPI!=null ?  ResponseEntity.ok(tickerDataFromAPI) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
