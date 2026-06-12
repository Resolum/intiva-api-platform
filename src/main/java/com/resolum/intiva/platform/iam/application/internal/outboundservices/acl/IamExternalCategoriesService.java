package com.resolum.intiva.platform.iam.application.internal.outboundservices.acl;

import com.resolum.intiva.platform.categories.interfaces.acl.CategoriesContextFacade;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with external categories services.
 */
@Service
public class IamExternalCategoriesService {

    private final CategoriesContextFacade categoriesContextFacade;

    /**
     * Constructor for ExternalCategoriesService.
     *
     * @param categoriesContextFacade the CategoriesContextFacade to be used by this service
     */
    public IamExternalCategoriesService(CategoriesContextFacade categoriesContextFacade) {
        this.categoriesContextFacade = categoriesContextFacade;
    }

    /**
     * Retrieves the default category for a given user ID.
     *
     * @param userId the ID of the user for whom to retrieve the default category
     */
    public void createDefaultCategory(Long userId) {
        categoriesContextFacade.createDefaultCategory(userId);
    }
}
