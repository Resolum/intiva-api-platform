package com.resolum.intiva.platform.household.domain.services;

import com.resolum.intiva.platform.household.domain.model.aggregates.Family;
import com.resolum.intiva.platform.household.domain.model.queries.GetFamiliesByOwnerIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetFamilyByIdQuery;

import java.util.List;
import java.util.Optional;

public interface FamilyQueryService {
    Optional<Family> handle(GetFamilyByIdQuery query);
    List<Family> handle(GetFamiliesByOwnerIdQuery query);
}
