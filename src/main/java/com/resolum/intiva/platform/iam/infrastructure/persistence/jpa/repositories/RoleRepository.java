package com.resolum.intiva.platform.iam.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.iam.domain.model.entities.Role;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.RoleTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Role entities in the database.
 * This interface extends JpaRepository, providing CRUD operations and custom query methods for Role entities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    /**
     * Checks if a role with the given name exists in the database. Used for not repeating the same role name check in multiple places.
     * @param name The name of the role to check for existence.
     * @return true if a role with the given name exists, false otherwise.
     */
    boolean existsRoleByName(RoleTypes name);
}
