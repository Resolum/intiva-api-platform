package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.valueobjects.CategoryDescription;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.Color;
import com.resolum.intiva.platform.shared.domain.valueobjects.Icon;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

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

    /**
     * Protected no-args constructor for JPA.
     */
    protected Category() {

    }

    /**
     * Constructs a new Category instance based on the provided CreateCategoryCommand.
     *
     * @param command the command containing the details for creating a new category
     */
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

    /**
     * Creates a list of default categories for a given user ID.
     *
     * @param userId the ID of the user for whom the default categories should be created
     * @return a list of default Category instances
     */
    public static List<Category> createDefault(Long userId) {
        return List.of(
                new Category(new CreateCategoryCommand(
                        "Salario", "USER", userId, null,
                        "Ingresos mensuales por empleo",
                        "#4CAF50", "briefcase"
                )),
                new Category(new CreateCategoryCommand(
                        "Freelance", "USER", userId, null,
                        "Ingresos por trabajo independiente",
                        "#2196F3", "laptop"
                )),
                new Category(new CreateCategoryCommand(
                        "Negocio", "USER", userId, null,
                        "Ingresos de tu negocio o empresa",
                        "#FF9800", "store"
                )),
                new Category(new CreateCategoryCommand(
                        "Inversión", "USER", userId, null,
                        "Rendimientos e intereses de inversiones",
                        "#9C27B0", "trending_up"
                )),
                new Category(new CreateCategoryCommand(
                        "Renta", "USER", userId, null,
                        "Ingresos por alquiler de propiedades",
                        "#00BCD4", "home"
                )),
                new Category(new CreateCategoryCommand(
                        "Pensión", "USER", userId, null,
                        "Pensión, jubilación o subsidios",
                        "#FF5722", "shield"
                )),
                new Category(new CreateCategoryCommand(
                        "Otros", "USER", userId, null,
                        "Otros ingresos no clasificados",
                        "#607D8B", "more_horiz"
                ))
        );
    }
}