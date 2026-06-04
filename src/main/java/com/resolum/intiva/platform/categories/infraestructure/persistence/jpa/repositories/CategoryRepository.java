package com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Category entities in the database.
 * This interface extends JpaRepository, providing basic CRUD operations and custom query methods.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds all categories based on the specified owner type and owner ID.
     *
     * @param ownerType The type of owner (e.g., 'individual', 'family').
     * @param ownerId The unique identifier of the owner (user or family).
     * @return A list of categories that match the specified owner type and owner ID.
     */
    List<Category> findAllByOwnerTypeAndOwnerId(String ownerType, Long ownerId);
}