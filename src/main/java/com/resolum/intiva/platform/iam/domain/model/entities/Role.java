package com.resolum.intiva.platform.iam.domain.model.entities;

import com.resolum.intiva.platform.iam.domain.model.valueobjects.RoleTypes;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Role class represents a role in the system, which can be assigned to users to define their permissions and access levels.
 * It is an entity class that will be mapped to a database table using JPA annotations.
 */
@Entity
@Data
@NoArgsConstructor
public class Role {

    // The unique identifier for the role, generated automatically by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    // The name of the role, stored as a string in the database
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleTypes name;

    // Constructor
    public Role(RoleTypes name) {
        this.name = name;
    }

    /**
     * Get the name of the role
     * @return the name of the role
     */
    public String getName() {
        return this.name.name();
    }

    /**
     * Get the role from its name
     * @param name the name of the role
     * @return the role
     */
    public static Role toRoleFromName(String name) {
        return new Role(RoleTypes.valueOf(name));
    }
}
