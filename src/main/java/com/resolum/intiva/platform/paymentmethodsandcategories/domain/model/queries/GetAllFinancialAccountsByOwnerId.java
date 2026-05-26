package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries;

/**
 * Query to get all financial accounts for a specific owner.
 * @param ownerId the ID of the owner
 */
public record GetAllFinancialAccountsByOwnerId(Long ownerId) {
}
