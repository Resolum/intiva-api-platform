package com.resolum.intiva.platform.profiles.application.internal.outboundservices;

import com.resolum.intiva.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.resolum.intiva.platform.iam.domain.services.UserQueryService;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;

/**
 * Outbound service that enables the profiles bounded context to interact with the IAM bounded context.
 *
 * <p>This service acts as an anti-corruption layer (ACL) to fetch user-related data from the IAM
 * context, such as email addresses, without leaking IAM domain concepts into the profiles domain.</p>
 */
@Service
public class ProfilesExternalIamService {

    private final UserQueryService userQueryService;

    public ProfilesExternalIamService(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    /**
     * Retrieves the email address associated with a given user identifier.
     *
     * @param userId the identifier of the user whose email is being requested
     * @return the user's email string, or an empty string if the user was not found
     */
    public String getUserEmail(Long userId) {
        var user = userQueryService.handle(new GetUserByIdQuery(new UserId(userId)));
        return user.map(u -> u.getEmail().getValue()).orElse("");
    }
}
