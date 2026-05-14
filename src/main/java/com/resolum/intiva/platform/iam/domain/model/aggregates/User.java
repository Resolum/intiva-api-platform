package com.resolum.intiva.platform.iam.domain.model.aggregates;

import com.resolum.intiva.platform.iam.domain.model.entities.Role;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.Email;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.PasswordHash;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a user within the IAM system, encapsulating their email, password hash, and assigned role. This entity is responsible for managing user-related data and behaviors, including authentication credentials and role-based access control.
 *
 * @summary
 * The User entity extends AuditableAbstractAggregate to inherit common auditing fields such as createdAt and updatedAt.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class User extends AuditableAbstractAggregate<User> {

    /**
     * The email address of the user, which serves as a unique identifier for authentication and communication purposes. This field is mandatory and must be a valid Email value object.
     */
    @NotBlank
    @Size(max = 64)
    @Column(unique = true)
    @Valid
    @Embedded
    private Email email;

    /**
     * The hashed password of the user, which is used for authentication purposes. This field is mandatory and must be a valid PasswordHash value object.
     */
    @NotBlank
    @Valid
    @Embedded
    private PasswordHash passwordHash;

    /**
     * The role assigned to the user, which determines their permissions and access levels within the system. This field is mandatory and must reference a valid Role entity.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Constructors, getters, setters, and other methods
    public User(Email email, PasswordHash passwordHash, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /**
     * Updates the user's password hash.
     * @param newPasswordHash The new password hash to set for the user. Must be valid and not null.
     */
    public void updatePassword(@Valid PasswordHash newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }
}
