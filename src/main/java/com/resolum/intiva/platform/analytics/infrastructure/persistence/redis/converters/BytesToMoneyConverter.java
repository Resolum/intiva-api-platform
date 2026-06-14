package com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.converters;

import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Spring converter that deserialises a byte array from Redis back into a {@link Money} value object.
 *
 * <p>The expected serialisation format is a JSON object with two fields:
 * <ul>
 *     <li>{@code amount} — the monetary amount as a BigDecimal string</li>
 *     <li>{@code currencyCode} — the currency code enum name (e.g., PEN, USD)</li>
 * </ul>
 * </p>
 */
@Component
public class BytesToMoneyConverter implements Converter<byte[], Money> {

    /**
     * Redis-specific ObjectMapper used for JSON deserialisation.
     */
    private final ObjectMapper objectMapper;

    /**
     * Creates the converter with the redis-specific ObjectMapper.
     *
     * @param objectMapper the redis ObjectMapper bean
     */
    public BytesToMoneyConverter(@Qualifier("redisObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Deserialises a JSON byte array from Redis into a Money value object.
     *
     * @param source the JSON byte array to deserialise
     * @return a reconstructed Money value object
     * @throws IllegalArgumentException if deserialisation fails
     */
    @Override
    public Money convert(byte[] source) {
        try {
            var map = objectMapper.readValue(source, Map.class);
            return new Money(
                    new BigDecimal((String) map.get("amount")),
                    CurrencyCodes.valueOf((String) map.get("currencyCode"))
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to deserialize Money from bytes", e);
        }
    }
}
