package com.resolum.intiva.platform.finances.application.internal.outboundservices.acl;

import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.acl.CategoriesContextFacade;
import com.resolum.intiva.platform.shared.domain.valueobjects.CategoryId;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for interacting with external categories services.
 */
@Service
public class FinancesExternalCategoriesService {

    /**
     * The CategoriesContextFacade is a facade that provides access to the categories context.
     */
    private final CategoriesContextFacade categoriesContextFacade;

    /**
     * Constructor for ExternalCategoriesService.
     *
     * @param categoriesContextFacade the CategoriesContextFacade to be used by this service
     */
    public FinancesExternalCategoriesService(CategoriesContextFacade categoriesContextFacade) {
        this.categoriesContextFacade = categoriesContextFacade;
    }

    /**
     * Checks if a category exists by its ID.
     * @param categoryId the ID of the category to check
     * @return an Optional containing a boolean indicating whether the category exists
     */
    public Boolean existsCategoryById(Long categoryId) {
        return categoriesContextFacade.existsCategoryById(categoryId);
    }

    /**
     * Retrieves the color and name of a category by its ID.
     * @param categoryId the ID of the category to retrieve
     * @return an ImmutablePair containing the color and name of the category
     */
    public ImmutablePair<String, String> getCategoryColorAndNameById(Long categoryId) {
        return categoriesContextFacade.getCategoryColorAndIconById(categoryId);
    }
}
