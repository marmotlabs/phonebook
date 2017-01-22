package com.sandbox.phonebook.repository;

import com.sandbox.phonebook.domain.Number;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Number entity.
 */
@SuppressWarnings("unused")
public interface NumberRepository extends JpaRepository<Number,Long> {

}
