package com.cryptoStreamAPI.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static final Logger log = LoggerFactory.getLogger(DateTimeUtil.class);

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(8);

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;

    /**
     *
     * @return timeStamp in epochMillis
     */
    public static Long getCurrentTimeStampInEpochMillis(){

        Instant instant = Instant.now().atOffset(ZONE_OFFSET).toInstant();
        return instant.toEpochMilli();
    }

    /**
     *
     * @return timeStamp in epochMillis
     */
    public static Long getTimeStampInEpochMillis(LocalDateTime localDateTime){

        Instant instant = localDateTime.toInstant(ZONE_OFFSET);
        return instant.toEpochMilli();
    }

    /**
     *
     * @return timeStamp in LocalDateTime
     */
    public static LocalDateTime getCurrentTimeStamp(){

        return LocalDateTime.now().atOffset(ZONE_OFFSET).toLocalDateTime();
    }

    /**
     *
     * @param format
     * @param date
     * @return formatted DateString
     */
    public static String getDateAsFormattedString(String format,LocalDateTime date){

        LocalDateTime dateArgument = date==null ? getCurrentTimeStamp() : date;

        String formatArgument = format!=null ? format : "yyyyMMdd";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(formatArgument);

        return dtf.format(dateArgument);
    }

    /**
     *
     * @param format
     * @param dateString
     * @return LocalDateTime
     */
    public static LocalDateTime parseStringAsLocalDateTime(String format,String dateString){

        String formatArgument = format!=null ? format : "yyyy-MM-dd'T'HH:mm:ss";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(formatArgument);

        return LocalDateTime.from(dtf.parse(dateString));
    }


}
