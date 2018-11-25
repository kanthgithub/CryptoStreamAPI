package com.cryptoStreamAPI.service;

import com.cryptoStreamAPI.entity.TickerData;
import com.cryptoStreamAPI.model.HistoricDataModelWrapper;
import com.cryptoStreamAPI.repository.TickerDataRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TickerDataQueryServiceImpl implements TickerDataQueryService{

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TickerDataRepository tickerDataRepository;

    @Override
    public HistoricDataModelWrapper getTickerDataFromAPI() {
        ResponseEntity<HistoricDataModelWrapper> responseEntity = restTemplate.exchange(COINBASE_BTC_TICKER_URL, HttpMethod.GET, null, HistoricDataModelWrapper.class);

        return !responseEntity.getStatusCode().isError() && responseEntity.hasBody() ? responseEntity.getBody() : null;
    }

    @Override
    public List<TickerData> getHistoricTickerData() {
        return Lists.newArrayList(tickerDataRepository.findAll());
    }

    @Override
    public List<TickerData> getTickerDataByPeriod(long numberOfDays) {
        return tickerDataRepository.getTickerDataByPeriod(numberOfDays);
    }

    @Override
    public List<TickerData> getTickerDataByDate(LocalDateTime date) {
        return tickerDataRepository.getTickerDataByDate(date);
    }

    @Override
    public List<TickerData> getTickerDataByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        return tickerDataRepository.getTickerDataByDateRange(fromDate,toDate);
    }


}
