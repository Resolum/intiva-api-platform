package com.resolum.intiva.platform.household.domain.services;

import com.resolum.intiva.platform.household.domain.model.aggregates.Invitation;
import com.resolum.intiva.platform.household.domain.model.queries.GetActiveInvitationByFamilyIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetInvitationByIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetInvitationByTokenQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetInvitationsByUserIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetPendingInvitationsByUserIdQuery;
import com.resolum.intiva.platform.household.application.internal.InvitationPublicInfo;

import java.util.List;
import java.util.Optional;

public interface InvitationQueryService {
    Optional<Invitation> handle(GetInvitationByIdQuery query);
    List<Invitation> handle(GetInvitationsByUserIdQuery query);
    List<Invitation> handle(GetPendingInvitationsByUserIdQuery query);
    Optional<Invitation> handle(GetActiveInvitationByFamilyIdQuery query);
    InvitationPublicInfo getInvitationByToken(GetInvitationByTokenQuery query);
}
