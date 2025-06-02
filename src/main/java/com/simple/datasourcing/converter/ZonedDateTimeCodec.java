package com.simple.datasourcing.converter;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeCodec implements Codec<ZonedDateTime> {

    @Override
    public ZonedDateTime decode(BsonReader reader, DecoderContext decoderContext) {
        String zonedDateTime = reader.readString();
        return ZonedDateTime.parse(zonedDateTime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    @Override
    public void encode(BsonWriter writer, ZonedDateTime value, EncoderContext encoderContext) {
        writer.writeString(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    }

    @Override
    public Class<ZonedDateTime> getEncoderClass() {
        return ZonedDateTime.class;
    }

    public static class ZonedDateTimeCodecProvider implements CodecProvider {

        @Override
        @SuppressWarnings("unchecked")
        public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
            if (clazz == ZonedDateTime.class) {
                return (Codec<T>) new ZonedDateTimeCodec();
            }
            return null;
        }
    }
}