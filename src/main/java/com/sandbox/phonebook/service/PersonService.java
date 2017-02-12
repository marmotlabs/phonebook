package com.sandbox.phonebook.service;

import com.sandbox.phonebook.domain.Person;
import com.sandbox.phonebook.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Service Implementation for managing Person.
 */
@Service
@Transactional
public class PersonService {

    private final Logger log = LoggerFactory.getLogger(PersonService.class);

    @Inject
    private PersonRepository personRepository;

    /**
     * Save a person.
     *
     * @param person the entity to save
     * @return the persisted entity
     */
    public Person save(Person person) {
        log.debug("Request to save Person : {}", person);
        if (personRepository.findAll().stream().anyMatch(it -> it.getName().equals(person.getName()))) {
            throw new RuntimeException();
        }
        return personRepository.save(person);
    }

    /**
     * Get all the people.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Person> findAll(Pageable pageable) {
        log.debug("Request to get all People");
        Page<Person> result = personRepository.findAll(pageable);
        return result;
    }

    /**
     * Get one person by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Person findOne(Long id) {
        log.debug("Request to get Person : {}", id);
        Person person = personRepository.findOne(id);
        return person;
    }

    /**
     * Delete the  person by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Person : {}", id);
        personRepository.delete(id);
    }
}
