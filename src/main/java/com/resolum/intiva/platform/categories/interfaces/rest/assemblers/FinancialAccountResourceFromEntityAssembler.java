package com.resolum.intiva.platform.categories.interfaces.rest.assemblers;

import com.resolum.intiva.platform.categories.domain.model.entities.CreditCardAccount;
import com.resolum.intiva.platform.categories.domain.model.entities.DebitCardAccount;
import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.categories.domain.model.entities.WalletAccount;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.responses.FinancialAccountResource;
import jakarta.persistence.DiscriminatorValue;

/**
 * Assembler to convert a FinancialAccount entity into a FinancialAccountResource for REST responses.
 */
public class FinancialAccountResourceFromEntityAssembler {

    /**
     * Converts a FinancialAccount entity into a FinancialAccountResource.
     * @param entity the FinancialAccount entity to convert
     * @return the corresponding FinancialAccountResource
     */
    public static FinancialAccountResource toResourceFromEntity(FinancialAccount entity) {
        return new FinancialAccountResource(
                entity.getId(),
                entity.getName().getName(),
                entity.getClass().getAnnotation(DiscriminatorValue.class).value(),
                entity.getCurrentAmount().currencyCode().name(),
                entity.getCurrentAmount().amount(),
                entity instanceof DebitCardAccount debit && debit.getInstitution() != null
                        ? debit.getInstitution().getInstitutionName() :
                        entity instanceof CreditCardAccount credit && credit.getInstitution() != null
                        ? credit.getInstitution().getInstitutionName() :
                        entity instanceof WalletAccount wallet && wallet.getInstitution() != null
                        ? wallet.getInstitution().getInstitutionName() : null,
                entity instanceof CreditCardAccount credit
                        ? credit.getCreditLimit() : null,
                entity.getIsActive(),
                entity.getVersion()
        );
    }
}
