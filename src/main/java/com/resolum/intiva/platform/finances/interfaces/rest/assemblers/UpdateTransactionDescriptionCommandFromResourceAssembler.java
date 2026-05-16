package com.resolum.intiva.platform.finances.interfaces.rest.assemblers;

import com.resolum.intiva.platform.finances.domain.model.commands.UpdateTransactionDescriptionCommand;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.UpdateTransactionDescriptionResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionEntryId;

/**
 * UpdateTransactionDescriptionCommandFromResourceAssembler is a utility class that provides a method to convert an UpdateTransactionDescriptionResource, which represents the data received from the REST API for updating a transaction description, into an UpdateTransactionDescriptionCommand, which is a command object used in the application's domain logic to perform the update operation on a transaction entry. This assembler helps in separating the concerns of data transfer and business logic by transforming the incoming resource into a command that can be processed by the application's service layer.
 */
public class UpdateTransactionDescriptionCommandFromResourceAssembler {

    /**
     * Converts an UpdateTransactionDescriptionResource to an UpdateTransactionDescriptionCommand, using the provided transaction entry ID. This method extracts the description from the resource and combines it with the transaction entry ID to create a command that can be used to update the description of a transaction entry in the system.
     * @param id the ID of the transaction entry to be updated
     * @param resource the resource containing the new description for the transaction entry
     * @return an UpdateTransactionDescriptionCommand that encapsulates the transaction entry ID and the new description for the transaction entry
     */
    public static UpdateTransactionDescriptionCommand toCommandFromResource(Long id, UpdateTransactionDescriptionResource resource) {
        var transactionId = new TransactionEntryId(id);

        return new UpdateTransactionDescriptionCommand(
                transactionId,
                resource.description()
        );
    }
}
