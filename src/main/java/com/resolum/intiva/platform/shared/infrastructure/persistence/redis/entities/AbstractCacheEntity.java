package com.resolum.intiva.platform.shared.infrastructure.persistence.redis.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * AbstractCacheEntity serves as a base class for all cache entities stored in Redis. It provides a common structure for cache entities, including a unique identifier (id) that can be used to retrieve and manage cached data efficiently.
 */
@Getter
public abstract class AbstractCacheEntity {

    /** The unique identifier for the cache entity. */
    @Setter
    @Id
    private String id;
}
