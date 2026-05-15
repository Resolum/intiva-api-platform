package com.resolum.intiva.platform.categories.domain.model.aggregates;

import com.resolum.intiva.platform.categories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import jakarta.persistence.*;
import lombok.Getter;
import java.util.UUID;

@Entity
@Getter
@Table(name = "categories")
public class Category extends AuditableAbstractAggregate<Category> {


    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(name = "owner_type", nullable = false)
    private String ownerType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "is_active")
    private Boolean isActive;

    protected Category() {
        // Requerido por JPA
    }

    public Category(CreateCategoryCommand command) {
        this.name = command.name();
        this.color = command.color();
        this.ownerType = command.ownerType().toUpperCase();
        this.userId = command.userId();
        this.groupId = command.groupId();
        this.isActive = true;
    }

    // Métodos de negocio
    public void updateDetails(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void archive() {
        this.isActive = false;
    }
}