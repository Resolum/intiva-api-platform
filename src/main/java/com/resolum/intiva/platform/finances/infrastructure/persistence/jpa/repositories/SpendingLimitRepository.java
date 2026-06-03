package com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitTargetType;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for {@code SpendingLimit} persistence.
 */
@Repository
public interface SpendingLimitRepository extends JpaRepository<SpendingLimit, Long> {
    /**
     * Finds spending limits owned by the given owner id.
     *
     * @param ownerId owner identifier
     * @return matching spending limits
     */
    List<SpendingLimit> findByOwnerId(Long ownerId);

    /**
     * Finds spending limits owned by the given owner id and owner type.
     *
     * @param ownerId owner identifier
     * @param ownerType owner scope
     * @return matching spending limits
     */
    List<SpendingLimit> findByOwnerIdAndOwnerType(Long ownerId, OwnerTypes ownerType);

    /**
     * Finds spending limits by target type and target id.
     *
     * @param targetType controlled target type
     * @param targetId target identifier
     * @return matching spending limits
     */
    List<SpendingLimit> findByTargetTypeAndTargetId(SpendingLimitTargetType targetType, Long targetId);

    /**
     * Finds active spending limits for one owner.
     *
     * @param ownerId owner identifier
     * @param ownerType owner scope
     * @return active spending limits
     */
    List<SpendingLimit> findByOwnerIdAndOwnerTypeAndActiveTrue(Long ownerId, OwnerTypes ownerType);
}
