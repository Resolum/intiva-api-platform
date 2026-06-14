package com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.converters;

import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Spring converter that serialises a {@link Money} value object into a byte array
 * for storage in Redis.
 *
 * <p>The serialisation format is a JSON object with two fields:
 * <ul>
 *     <li>{@code amount} — the monetary amount as a BigDecimal string</li>
 *     <li>{@code currencyCode} — the currency code enum name (e.g., PEN, USD)</li>
 * </ul>
 * </p>
 */
@Component
public class MoneyToBytesConverter implements Converter<Money, byte[]> {

    /**
     * Redis-specific ObjectMapper used for JSON serialisation.
     */
    private final ObjectMapper objectMapper;

    /**
     * Creates the converter with the redis-specific ObjectMapper.
     *
     * @param objectMapper the redis ObjectMapper bean
     */
    public MoneyToBytesConverter(@Qualifier("redisObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Serialises a Money value object to a JSON byte array.
     *
     * @param source the Money value to serialise
     * @return a JSON byte array containing the amount and currency code
     * @throws IllegalArgumentException if serialisation fails
     */
    @Override
    public byte[] convert(Money source) {
        try {
            return objectMapper.writeValueAsBytes(
                    Map.of(
                            "amount", source.getAmount().toString(),
                            "currencyCode", source.getCurrencyCode()
                    )
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to serialize Money to bytes", e);
        }
    }
}
