package com.cryptoStreamAPI.repository;

import com.cryptoStreamAPI.common.DateTimeUtil;
import com.cryptoStreamAPI.entity.TickerData;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.pipeline.movavg.MovAvgPipelineAggregationBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.cryptoStreamAPI.common.DateTimeUtil.getTimeStampInEpochMillis;

@Repository
public class TickerDataRepositoryImpl implements TickerDataRepositoryCustom {

    public static final FieldSortBuilder SORT_BUILDER_TICKER_TIME_DESC = SortBuilders.fieldSort("tickerTimeInEpochMillis").order(SortOrder.DESC);
    public static final MatchPhraseQueryBuilder QUERY_BUILDER_BASE = QueryBuilders.matchPhraseQuery("base", BASE);
    public static final MatchPhraseQueryBuilder QUERY_BUILDER_CURRENCY = QueryBuilders.matchPhraseQuery("currency", CURRENCY);
    public static final RangeQueryBuilder QUERY_BUILDER_RANGE_TICKER_TIME_IN_EPOCH = QueryBuilders.rangeQuery("tickerTimeInEpochMillis");


    Logger log = LoggerFactory.getLogger(TickerDataRepositoryImpl.class);

    @Autowired
    public TickerDataRepositoryImpl(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    private ElasticsearchTemplate elasticsearchTemplate;


    @Override
    public List<TickerData> getTickerDataByPeriod(long numberOfDays) {

        //get Date-Range by computing distance between currentDate and numberOfDays to past
        LocalDateTime currentDate = DateTimeUtil.getCurrentTimeStamp();

        Long currentDateInEpochMillis = getTimeStampInEpochMillis(currentDate);

        LocalDateTime pastDate = currentDate.minusDays(numberOfDays);

        Long pastDateInEpochMillis = getTimeStampInEpochMillis(pastDate);

        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(QUERY_BUILDER_BASE)
                .must(QUERY_BUILDER_CURRENCY)
                .must(QUERY_BUILDER_RANGE_TICKER_TIME_IN_EPOCH.gte(pastDateInEpochMillis))
                .must(QUERY_BUILDER_RANGE_TICKER_TIME_IN_EPOCH.lte(currentDateInEpochMillis));

        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(qb).withSort(SORT_BUILDER_TICKER_TIME_DESC)
                .build();

        return elasticsearchTemplate.queryForList(build, TickerData.class);
    }

    @Override
    public List<TickerData> getTickerDataByDate(LocalDateTime localDateTime) {

        Long currentDateInEpochMillis = getTimeStampInEpochMillis(localDateTime);

        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(QUERY_BUILDER_BASE)
                .must(QUERY_BUILDER_CURRENCY)
                .must(QueryBuilders.termQuery("tickerTimeInEpochMillis",currentDateInEpochMillis));

        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(qb).withSort(SORT_BUILDER_TICKER_TIME_DESC)
                .build();

        return elasticsearchTemplate.queryForList(build, TickerData.class);
    }

    @Override
    public List<TickerData> getTickerDataByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {

        Long fromDateInEpochMillis = getTimeStampInEpochMillis(fromDate);

        Long toDateInEpochMillis = getTimeStampInEpochMillis(toDate);

        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(QUERY_BUILDER_BASE)
                .must(QUERY_BUILDER_CURRENCY)
                .must(QUERY_BUILDER_RANGE_TICKER_TIME_IN_EPOCH.gte(fromDateInEpochMillis))
                .must(QUERY_BUILDER_RANGE_TICKER_TIME_IN_EPOCH.lte(toDateInEpochMillis));

        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(qb).withSort(SORT_BUILDER_TICKER_TIME_DESC)
                .build();

        return elasticsearchTemplate.queryForList(build, TickerData.class);
    }


    public void getMovingAverageDataForRange(){

        SumAggregationBuilder sum = AggregationBuilders.sum("my_sum")
                .field("amount_field");

        MovAvgPipelineAggregationBuilder mavg = PipelineAggregatorBuilders.movingAvg("my_mov_avg", "my_sum");

        DateHistogramAggregationBuilder histo = AggregationBuilders.dateHistogram("histo")
                .field("date_field")
                .subAggregation(sum)
                .subAggregation(mavg);

        

    }


    /**
     *
     * @param base
     * @param currency
     * @return QueryBuilder
     */
    public QueryBuilder getQueryBuilderForTicker(String base,String currency) {

        return QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery("base", base))
                .must(QueryBuilders.matchPhraseQuery("currency", currency));
    }


    public QueryBuilder getQueryBuilderForDays(Long numberOfDays) {

        //get Date-Range by computing distance between currentDate and numberOfDays to past
        LocalDateTime currentDate = DateTimeUtil.getCurrentTimeStamp();

        Long currentDateInEpochMillis = getTimeStampInEpochMillis(currentDate);

        LocalDateTime pastDate = currentDate.minusDays(numberOfDays);

        Long pastDateInEpochMillis = getTimeStampInEpochMillis(pastDate);

        return QueryBuilders.boolQuery()
                            .must(QUERY_BUILDER_RANGE_TICKER_TIME_IN_EPOCH.gte(pastDateInEpochMillis))
                            .must(QUERY_BUILDER_RANGE_TICKER_TIME_IN_EPOCH.lte(currentDateInEpochMillis));
    }
}