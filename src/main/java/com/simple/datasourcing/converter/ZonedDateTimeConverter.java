package com.simple.datasourcing.converter;

import org.springframework.core.convert.converter.*;
import org.springframework.data.convert.*;

import java.time.*;
import java.time.format.*;

public class ZonedDateTimeConverter {

    @SuppressWarnings("NullableProblems")
    @WritingConverter
    public static class ZonedDateTimeToStringConverter implements Converter<ZonedDateTime, String> {

        @Override
        public String convert(ZonedDateTime source) {
            return source != null ? source.format(DateTimeFormatter.ISO_ZONED_DATE_TIME) : null;
        }
    }

    @SuppressWarnings("NullableProblems")
    @ReadingConverter
    public static class StringToZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

        @Override
        public ZonedDateTime convert(String source) {
            return source != null ? ZonedDateTime.parse(source, DateTimeFormatter.ISO_ZONED_DATE_TIME) : null;
        }
    }
}