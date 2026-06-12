package com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.CategoryType;
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
     * Finds all categories by owner type, owner ID, and category type.
     *
     * @param ownerType the type of the owner (e.g., user, organization)
     * @param ownerId the ID of the owner
     * @param type the type of the category
     * @return a list of categories matching the specified criteria
     */
    List<Category> findAllByOwnerTypeAndOwnerIdAndType(String ownerType, Long ownerId, CategoryType type);
}