package com.resolum.intiva.platform.household.infrastructure.persistence.redis.repositories;

import com.resolum.intiva.platform.household.infrastructure.persistence.redis.entities.InvitationLinkCacheEntity;
import org.springframework.data.repository.CrudRepository;

public interface InvitationLinkCacheRepository extends CrudRepository<InvitationLinkCacheEntity, String> {
}
