package com.resolum.intiva.platform.finances.interfaces.rest.assemblers;

import com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.RecurringTransactionResource;

/**
 * Maps recurring transaction aggregates into REST response resources.
 */
public class RecurringTransactionResourceFromEntityAssembler {

    /**
     * Converts a recurring transaction aggregate into its API response representation.
     *
     * @param entity recurring transaction aggregate
     * @return response resource representing the aggregate
     */
    public static RecurringTransactionResource toResourceFromEntity(RecurringTransaction entity) {
        return new RecurringTransactionResource(
                entity.getId(),
                entity.getAmount().getAmount().toPlainString(),
                entity.getAmount().getCurrencyCode(),
                entity.getDescription(),
                entity.getOwnerId(),
                entity.getOwnerType().name(),
                entity.getFinancialAccountId().getValue(),
                entity.getPerformedByUserId().getValue(),
                entity.getTransactionType().name(),
                entity.getCategoryId().getValue(),
                entity.getFrequency().name(),
                entity.getStartDate().toString(),
                entity.getNextExecutionDate().toString(),
                entity.getLastExecutionDate() == null ? null : entity.getLastExecutionDate().toString(),
                entity.getEndDate() == null ? null : entity.getEndDate().toString(),
                entity.getActive(),
                entity.getReminderDaysBefore(),
                entity.getCreatedAt() == null ? null : entity.getCreatedAt().toString()
        );
    }
}
