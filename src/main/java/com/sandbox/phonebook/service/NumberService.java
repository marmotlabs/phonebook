package com.sandbox.phonebook.service;

import com.sandbox.phonebook.domain.Number;
import com.sandbox.phonebook.repository.NumberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Number.
 */
@Service
@Transactional
public class NumberService {

    private final Logger log = LoggerFactory.getLogger(NumberService.class);
    
    @Inject
    private NumberRepository numberRepository;

    /**
     * Save a number.
     *
     * @param number the entity to save
     * @return the persisted entity
     */
    public Number save(Number number) {
        log.debug("Request to save Number : {}", number);
        Number result = numberRepository.save(number);
        return result;
    }

    /**
     *  Get all the numbers.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Number> findAll(Pageable pageable) {
        log.debug("Request to get all Numbers");
        Page<Number> result = numberRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one number by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Number findOne(Long id) {
        log.debug("Request to get Number : {}", id);
        Number number = numberRepository.findOne(id);
        return number;
    }

    /**
     *  Delete the  number by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Number : {}", id);
        numberRepository.delete(id);
    }
}
