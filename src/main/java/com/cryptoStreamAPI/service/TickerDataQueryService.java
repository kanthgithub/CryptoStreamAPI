package com.cryptoStreamAPI.service;

import com.cryptoStreamAPI.entity.TickerData;
import com.cryptoStreamAPI.model.HistoricDataModelWrapper;

import java.time.LocalDateTime;
import java.util.List;

public interface TickerDataQueryService {

    public static final String COINBASE_BTC_TICKER_URL = "https://www.coinbase.com/api/v2/prices/BTC-USD/historic?period=all";

    public HistoricDataModelWrapper getTickerDataFromAPI();

    public List<TickerData> getHistoricTickerData();

    public List<TickerData> getTickerDataByPeriod(long numberOfDays);

    public List<TickerData> getTickerDataByDate(LocalDateTime date) ;

    public List<TickerData> getTickerDataByDateRange(LocalDateTime fromDate,LocalDateTime toDate) ;


}
