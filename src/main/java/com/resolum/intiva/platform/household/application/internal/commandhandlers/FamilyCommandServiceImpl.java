package com.resolum.intiva.platform.household.application.internal.commandhandlers;

import com.resolum.intiva.platform.household.domain.model.aggregates.Family;
import com.resolum.intiva.platform.household.domain.model.aggregates.FamilyMember;
import com.resolum.intiva.platform.household.domain.model.commands.AddFamilyMemberCommand;
import com.resolum.intiva.platform.household.domain.model.commands.CreateFamilyCommand;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyRole;
import com.resolum.intiva.platform.household.domain.services.FamilyCommandService;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyMemberRepository;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of FamilyCommandService that handles family group creation.
 */
@Service
public class FamilyCommandServiceImpl implements FamilyCommandService {

    private final Logger LOGGER = LoggerFactory.getLogger(FamilyCommandServiceImpl.class);

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;

    /**
     * Creates the command service with the required repository dependencies.
     *
     * @param familyRepository       the family group repository
     * @param familyMemberRepository the family member repository
     */
    public FamilyCommandServiceImpl(FamilyRepository familyRepository, FamilyMemberRepository familyMemberRepository) {
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
    }

    /**
     * Creates a new family group and automatically adds the owner as an ADMIN member.
     *
     * @param command the creation command
     * @return the persisted Family aggregate
     * @throws IllegalArgumentException if name or ownerId is blank or null
     */
    @Override
    @Transactional
    public Family handle(CreateFamilyCommand command) {
        if (command.name() == null || command.name().isBlank()) {
            throw new IllegalArgumentException("Family name cannot be null or blank");
        }
        if (command.ownerId() == null) {
            throw new IllegalArgumentException("Owner ID cannot be null");
        }

        var family = new Family(command);
        var savedFamily = familyRepository.save(family);

        var addMemberCommand = new AddFamilyMemberCommand(savedFamily.getId(), command.ownerId(), FamilyRole.ADMIN);
        var member = new FamilyMember(addMemberCommand.familyId(), addMemberCommand.userId(), addMemberCommand.role());
        familyMemberRepository.save(member);

        LOGGER.info("Family group created successfully with id: {}", savedFamily.getId());

        return savedFamily;
    }
}
