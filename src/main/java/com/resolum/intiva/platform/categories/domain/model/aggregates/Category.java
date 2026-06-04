package com.resolum.intiva.platform.categories.domain.model.aggregates;

import com.resolum.intiva.platform.categories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.CategoryDescription;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.CategoryType;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.Color;
import com.resolum.intiva.platform.shared.domain.valueobjects.Icon;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

/**
 * Represents a financial category within the system, which can be associated with either an individual user or a family.
 * Each category has a name, description, color, and icon, and can be active or archived. Categories are used to classify financial transactions and can be customized by users.
 */
@Entity
@Getter
@Table(name = "categories")
public class Category extends AuditableAbstractAggregate<Category> {

    @Column(nullable = false)
    private String name;

    @Column(name = "owner_type", nullable = false)
    private String ownerType;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "is_active")
    private Boolean isActive;

    @Embedded
    private CategoryDescription description;

    @Embedded
    private Color color;

    @Embedded
    private Icon icon;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CategoryType type;

    /**
     * Protected no-args constructor for JPA.
     */
    protected Category() {}

    /**
     * Constructs a new Category instance based on the provided CreateCategoryCommand.
     *
     * @param command the command containing the details for creating a new category
     */
    public Category(CreateCategoryCommand command) {
        this.name = command.name();
        this.ownerType = command.ownerType().toUpperCase();
        this.ownerId = command.ownerId();
        this.isActive = true;
        this.description = new CategoryDescription(command.description());
        this.color = new Color(command.color());
        this.icon = new Icon(command.icon());
        this.type = command.type();
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

    /**
     * Creates a list of default categories for a given user ID.
     *
     * @param ownerId the ID of the user for whom the default categories should be created
     * @return a list of default Category instances
     */
    public static List<Category> createDefault(Long ownerId) {
        return List.of(
                new Category(new CreateCategoryCommand(
                        "Salario",
                        "INDIVIDUAL",
                        ownerId,
                        "Ingresos mensuales por empleo",
                        "#4CAF50",
                        "briefcase",
                        CategoryType.INCOME
                )),
                new Category(new CreateCategoryCommand(
                        "Freelance",
                        "INDIVIDUAL",
                        ownerId,
                        "Ingresos por trabajo independiente",
                        "#2196F3",
                        "laptop",
                        CategoryType.INCOME
                )),
                new Category(new CreateCategoryCommand(
                        "Negocio",
                        "INDIVIDUAL",
                        ownerId,
                        "Ingresos de tu negocio o empresa",
                        "#FF9800",
                        "store",
                        CategoryType.INCOME
                )),
                new Category(new CreateCategoryCommand(
                        "Inversión",
                        "INDIVIDUAL", ownerId,
                        "Rendimientos e intereses de inversiones",
                        "#9C27B0",
                        "trending_up",
                        CategoryType.INCOME
                )),
                new Category(new CreateCategoryCommand(
                        "Pensión",
                        "INDIVIDUAL",
                        ownerId,
                        "Pensión, jubilación o subsidios",
                        "#FF5722",
                        "shield",
                        CategoryType.INCOME
                )),
                new Category(new CreateCategoryCommand(
                        "Otros",
                        "INDIVIDUAL",
                        ownerId,
                        "Otros ingresos no clasificados",
                        "#607D8B",
                        "more_horiz",
                        CategoryType.INCOME
                ))
        );
    }
}