package com.simple.datasourcing.converter;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import java.io.*;
import java.time.*;
import java.time.format.*;

public class CustomZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        return ZonedDateTime.parse(parser.getText(), FORMATTER);
    }
}