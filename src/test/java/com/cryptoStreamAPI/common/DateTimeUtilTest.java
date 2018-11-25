package com.cryptoStreamAPI.common;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DateTimeUtilTest {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void assert_Get_CurrentTime_In_EpochMillis(){

        //given & when

        Long currentTimeInEpochMillis =
                DateTimeUtil.getCurrentTimeStampInEpochMillis();


        //then
        assertNotNull(currentTimeInEpochMillis);

        System.currentTimeMillis();

        log.info("currentTime In EpochMillis : {}",currentTimeInEpochMillis);

    }


    @Test
    public void test_get_Date_As_Formatted_String(){

        //given
        String format  = "yyyyMMdd";
        LocalDateTime date = LocalDateTime.of(2018,11 ,18 ,00 ,00 );
        String formatted_Date_String_Actual_Expected = "20181118";

        //when
        String formatted_Date_String_Actual = DateTimeUtil.getDateAsFormattedString(null,date);

        //then
        assertNotNull(formatted_Date_String_Actual);
        assertEquals(formatted_Date_String_Actual_Expected,formatted_Date_String_Actual );
    }


}
