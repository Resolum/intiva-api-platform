package com.resolum.intiva.platform.household.domain.services;

import com.resolum.intiva.platform.household.domain.model.aggregates.FamilyMember;
import com.resolum.intiva.platform.household.domain.model.queries.GetMemberByIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetMembersByFamilyIdQuery;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberQueryService {
    List<FamilyMember> handle(GetMembersByFamilyIdQuery query);
    Optional<FamilyMember> handle(GetMemberByIdQuery query);
}
