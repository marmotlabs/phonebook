package com.sandbox.phonebook.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sandbox.phonebook.domain.Number;
import com.sandbox.phonebook.service.NumberService;
import com.sandbox.phonebook.web.rest.util.HeaderUtil;
import com.sandbox.phonebook.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Number.
 */
@RestController
@RequestMapping("/api")
public class NumberResource {

    private final Logger log = LoggerFactory.getLogger(NumberResource.class);
        
    @Inject
    private NumberService numberService;

    /**
     * POST  /numbers : Create a new number.
     *
     * @param number the number to create
     * @return the ResponseEntity with status 201 (Created) and with body the new number, or with status 400 (Bad Request) if the number has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/numbers")
    @Timed
    public ResponseEntity<Number> createNumber(@Valid @RequestBody Number number) throws URISyntaxException {
        log.debug("REST request to save Number : {}", number);
        if (number.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("number", "idexists", "A new number cannot already have an ID")).body(null);
        }
        Number result = numberService.save(number);
        return ResponseEntity.created(new URI("/api/numbers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("number", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /numbers : Updates an existing number.
     *
     * @param number the number to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated number,
     * or with status 400 (Bad Request) if the number is not valid,
     * or with status 500 (Internal Server Error) if the number couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/numbers")
    @Timed
    public ResponseEntity<Number> updateNumber(@Valid @RequestBody Number number) throws URISyntaxException {
        log.debug("REST request to update Number : {}", number);
        if (number.getId() == null) {
            return createNumber(number);
        }
        Number result = numberService.save(number);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("number", number.getId().toString()))
            .body(result);
    }

    /**
     * GET  /numbers : get all the numbers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of numbers in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/numbers")
    @Timed
    public ResponseEntity<List<Number>> getAllNumbers(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Numbers");
        Page<Number> page = numberService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/numbers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /numbers/:id : get the "id" number.
     *
     * @param id the id of the number to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the number, or with status 404 (Not Found)
     */
    @GetMapping("/numbers/{id}")
    @Timed
    public ResponseEntity<Number> getNumber(@PathVariable Long id) {
        log.debug("REST request to get Number : {}", id);
        Number number = numberService.findOne(id);
        return Optional.ofNullable(number)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /numbers/:id : delete the "id" number.
     *
     * @param id the id of the number to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/numbers/{id}")
    @Timed
    public ResponseEntity<Void> deleteNumber(@PathVariable Long id) {
        log.debug("REST request to delete Number : {}", id);
        numberService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("number", id.toString())).build();
    }

}
