package com.cryptoStreamAPI.repository;

import com.cryptoStreamAPI.common.DateTimeUtil;
import com.cryptoStreamAPI.entity.TickerData;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.InternalStats;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.BucketHelpers;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.pipeline.movavg.MovAvgPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.movavg.models.HoltWintersModel;
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

    private static final String INTERVAL_FIELD = "l_value";
    private static final String VALUE_FIELD = "v_value";
    private static final String GAP_FIELD = "g_value";

    static HoltWintersModel.SeasonalityType seasonalityType;
    static BucketHelpers.GapPolicy gapPolicy;

    static int interval;
    static int numBuckets;
    static int windowSize;
    static double alpha;
    static double beta;
    static double gamma;
    static int period;

    enum MovAvgType {
        SIMPLE ("simple"), LINEAR("linear"), EWMA("ewma"), HOLT("holt"), HOLT_WINTERS("holt_winters"), HOLT_BIG_MINIMIZE("holt");

        private final String name;

        MovAvgType(String s) {
            name = s;
        }

        public String toString(){
            return name;
        }
    }

    enum MetricTarget {
        VALUE ("value"), COUNT("count"), METRIC("metric");

        private final String name;

        MetricTarget(String s) {
            name = s;
        }

        public String toString(){
            return name;
        }
    }

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

    @Override
    public void getMovingAverageDataForRange(){

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("tickerdatasnapshot")
                        .addAggregation(AggregationBuilders.stats("sum_of_price").field("price"));

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        if(searchResponse!=null) {
            log.info("metaData: {}", searchResponse);
            log.info("aggregation map: {}",searchResponse.getAggregations().getAsMap().get("sum_of_price"));

            log.info("aggregation metaData: {}", searchResponse.getAggregations().getAsMap().get("sum_of_price").getMetaData());
            log.info("aggregation type: {}", searchResponse.getAggregations().getAsMap().get("sum_of_price").getType());
            log.info("aggregation name: {}", searchResponse.getAggregations().getAsMap().get("sum_of_price").getName());
            log.info("aggregation class: {}", searchResponse.getAggregations().getAsMap().get("sum_of_price").getClass());
            log.info("aggregation name: {}", searchResponse.getAggregations().getAsMap().get("sum_of_price").getName());

            InternalStats internalStats =
                    (InternalStats)   searchResponse.getAggregations().getAsMap().get("sum_of_price");

            log.info("sum: {}",internalStats.getSum());

            log.info("average:  {}",internalStats.getAvg());


        }

        /*SearchResponse response = elasticsearchTemplate.getClient()
                .prepareSearch("tickerdatasnapshot").setTypes("type")
                .addAggregation(
                        histogram("histo").field(INTERVAL_FIELD).interval(interval)
                                .extendedBounds(0L, (long) (interval * (numBuckets - 1)))
                                .subAggregation(movingAvg("movavg_counts","_count")
                                        .window(windowSize)
                                        .modelBuilder(new LinearModel.LinearModelBuilder())
                                        .gapPolicy(gapPolicy)
                                        .setBucketsPaths("_count"))
                                .subAggregation(PipelineAggregatorBuilders.movingAvg("movavg_values","_count")
                                        .window(windowSize)
                                        .modelBuilder(new LinearModel.LinearModelBuilder())
                                        .gapPolicy(gapPolicy)
                                        .setBucketsPaths("the_metric"))
                ).execute().actionGet();*/

        SumAggregationBuilder sum = AggregationBuilders.sum("my_sum")
                .field("price");

        MovAvgPipelineAggregationBuilder mavg = PipelineAggregatorBuilders.movingAvg("my_mov_avg", "my_sum");

        DateHistogramAggregationBuilder histo = AggregationBuilders.dateHistogram("histo")
                .field("tickerTime")
                .subAggregation(sum)
                .subAggregation(mavg);

        SearchRequestBuilder searchRequestBuilder1 = elasticsearchTemplate.getClient().prepareSearch("tickerdatasnapshot").addAggregation(histo);
        SearchResponse searchResponse1 = searchRequestBuilder1.execute().actionGet();


        if(searchResponse1!=null) {
            log.info("metaData1: {}", searchResponse1);

        }
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