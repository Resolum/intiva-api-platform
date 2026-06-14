package com.resolum.intiva.platform.shared.infrastructure.persistence.redis.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.converters.BytesToMoneyConverter;
import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.converters.MoneyToBytesConverter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a Spring configuration class that sets up the necessary beans for connecting to a Redis server using Lettuce as the client. It defines the connection factory, object mapper for serialization and deserialization, and custom conversions for handling complex data types when interacting with Redis. The configuration values for the Redis connection (host, port, and password) are injected from the application properties using the @Value annotation. This allows the application to easily connect to a Redis server and perform operations such as storing and retrieving data in a structured format.
 */
@Configuration
public class RedisConfiguration {

    /** The host for the Redis connection, which is injected from the application properties using the @Value annotation. This host is used to establish a connection to the Redis server, allowing the application to interact with the Redis data store. The value is typically specified in the application.properties or application.yml file under the key "spring.data.redis.host". The default host for Redis is usually "localhost", but it can be configured to use a different host if needed. */
    @Value("${spring.data.redis.host}")
    private String host;

    /** The port number for the Redis connection, which is injected from the application properties using the @Value annotation. This port is used to establish a connection to the Redis server, allowing the application to interact with the Redis data store. The value is typically specified in the application.properties or application.yml file under the key "spring.data.redis.port". The default port for Redis is usually 6379, but it can be configured to use a different port if needed. */
    @Value("${spring.data.redis.port}")
    private Integer port;

    /** The password for the Redis connection, which is injected from the application properties using the @Value annotation. This password is used for authentication when connecting to the Redis server, ensuring that only authorized clients can access the Redis data store. The value is typically specified in the application.properties or application.yml file under the key "spring.data.redis.password". If authentication is not required, this value can be left empty or omitted from the configuration. */
    @Value("${spring.data.redis.password}")
    private String password;

    /**
     * Define a bean for LettuceConnectionFactory to establish a connection to the Redis server using the specified host, port, and password, along with client configuration options for command timeout and shutdown timeout.
     *
     * @return a LettuceConnectionFactory instance configured to connect to the Redis server with the specified host, port, password, and client configuration options for command timeout and shutdown timeout
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        // Configure the Redis connection with the specified host and port
        RedisStandaloneConfiguration redisConfiguration =
                new RedisStandaloneConfiguration(host, port);

        // Set the password for the Redis connection if authentication is required
        redisConfiguration.setPassword(password);

        // Configure the Lettuce client with command timeout and shutdown timeout options
        LettuceClientConfiguration clientConfiguration =
                LettuceClientConfiguration.builder()
                        .commandTimeout(Duration.ofSeconds(2))
                        .shutdownTimeout(Duration.ofMillis(100))
                        .build();

        // Create and return a LettuceConnectionFactory with the specified Redis configuration and client configuration
        return new LettuceConnectionFactory(
                redisConfiguration,
                clientConfiguration
        );
    }

    /**
     * Define a bean for ObjectMapper to be used for Redis serialization and deserialization, allowing the application to convert Java objects to JSON and vice versa when storing and retrieving data from Redis.
     *
     * @return an ObjectMapper instance configured for Redis serialization and deserialization, capable of handling various data types and formats using the JsonMapper builder
     */
    @Bean
    public ObjectMapper redisObjectMapper() {

        // Create and return an ObjectMapper instance using the JsonMapper builder, which can handle various data types and formats for Redis serialization and deserialization
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }

    /**
     * Define a bean for RedisCustomConversions to register custom converters for Redis serialization and deserialization, allowing the application to handle complex data types and value objects when storing and retrieving data from Redis.
     *
     * @return a RedisCustomConversions instance with registered custom converters for Redis serialization and deserialization
     */
    @Bean
    public RedisCustomConversions redisCustomConversions(
            MoneyToBytesConverter moneyToBytesConverter,
            BytesToMoneyConverter bytesToMoneyConverter) {

        // Create a list of custom converters for Redis serialization and deserialization, including converters for handling value objects
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(moneyToBytesConverter);
        converters.add(bytesToMoneyConverter);

        // Create and return a RedisCustomConversions instance with the registered custom converters, which will be used by the RedisTemplate for handling serialization and deserialization of objects stored in Redis
        return new RedisCustomConversions(converters);
    }
}
