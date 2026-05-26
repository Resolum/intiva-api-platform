package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.valueobjects.CategoryDescription;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.Color;
import com.resolum.intiva.platform.shared.domain.valueobjects.Icon;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "categories")
public class Category extends AuditableAbstractAggregate<Category> {


    @Column(nullable = false)
    private String name;

    @Column(name = "owner_type", nullable = false)
    private String ownerType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "is_active")
    private Boolean isActive;

    @Embedded
    private CategoryDescription description;

    @Embedded
    private Color color;

    @Embedded
    private Icon icon;

    protected Category() {

    }

    public Category(CreateCategoryCommand command) {
        this.name = command.name();
        this.ownerType = command.ownerType().toUpperCase();
        this.userId = command.userId();
        this.groupId = command.groupId();
        this.isActive = true;
        this.description = new CategoryDescription(command.description());
        this.color = new Color(command.color());
        this.icon = new Icon(command.icon());
    }

    public void updateDetails(String name, String description, String color, String icon) {
        this.name = name;
        this.description = new CategoryDescription(description);
        this.color = new Color(color);
        this.icon = new Icon(icon);
    }

    public void archive() {
        this.isActive = false;
    }

    public static Category createDefault(Long userId) {
        return new Category(
                new CreateCategoryCommand(
                        "Salario",
                        "USER",
                        userId,
                        null,
                        "Ingresos mensuales",
                        "#4CAF50",
                        "wallet"
                )
        );
    }
}