package com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Fíjate que aquí ahora recibe un Long
    List<Category> findAllByUserId(Long userId);
    List<Category> findAllByGroupId(Long groupId);
}