package com.cryptoStreamAPI.repository;

import com.cryptoStreamAPI.entity.TickerData;
import com.cryptoStreamAPI.model.MovingAverageModel;

import java.time.LocalDateTime;
import java.util.List;

public interface TickerDataRepositoryCustom {

    final String BASE = "BTC";
    final String CURRENCY = "USD";

    public List<TickerData> getTickerDataByPeriod(long numberOfDays);

    public List<TickerData> getTickerDataByDate(LocalDateTime localDateTime);

    public List<TickerData> getTickerDataByDateRange(LocalDateTime fromDate,LocalDateTime toDate);

    public List<MovingAverageModel> getMovingAverageDataForRange(LocalDateTime fromDate, LocalDateTime toDate, int dayRangeRollover);


}
