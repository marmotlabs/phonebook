package com.sandbox.phonebook.web.rest;

import com.sandbox.phonebook.PhonebookApp;

import com.sandbox.phonebook.domain.Number;
import com.sandbox.phonebook.repository.NumberRepository;
import com.sandbox.phonebook.service.NumberService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the NumberResource REST controller.
 *
 * @see NumberResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PhonebookApp.class)
public class NumberResourceIntTest {

    private static final String DEFAULT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_NUMBER = "BBBBBBBBBB";

    @Inject
    private NumberRepository numberRepository;

    @Inject
    private NumberService numberService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restNumberMockMvc;

    private Number number;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        NumberResource numberResource = new NumberResource();
        ReflectionTestUtils.setField(numberResource, "numberService", numberService);
        this.restNumberMockMvc = MockMvcBuilders.standaloneSetup(numberResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Number createEntity(EntityManager em) {
        Number number = new Number()
                .number(DEFAULT_NUMBER);
        return number;
    }

    @Before
    public void initTest() {
        number = createEntity(em);
    }

    @Test
    @Transactional
    public void createNumber() throws Exception {
        int databaseSizeBeforeCreate = numberRepository.findAll().size();

        // Create the Number

        restNumberMockMvc.perform(post("/api/numbers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(number)))
            .andExpect(status().isCreated());

        // Validate the Number in the database
        List<Number> numberList = numberRepository.findAll();
        assertThat(numberList).hasSize(databaseSizeBeforeCreate + 1);
        Number testNumber = numberList.get(numberList.size() - 1);
        assertThat(testNumber.getNumber()).isEqualTo(DEFAULT_NUMBER);
    }

    @Test
    @Transactional
    public void createNumberWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = numberRepository.findAll().size();

        // Create the Number with an existing ID
        Number existingNumber = new Number();
        existingNumber.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restNumberMockMvc.perform(post("/api/numbers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingNumber)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Number> numberList = numberRepository.findAll();
        assertThat(numberList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = numberRepository.findAll().size();
        // set the field null
        number.setNumber(null);

        // Create the Number, which fails.

        restNumberMockMvc.perform(post("/api/numbers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(number)))
            .andExpect(status().isBadRequest());

        List<Number> numberList = numberRepository.findAll();
        assertThat(numberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllNumbers() throws Exception {
        // Initialize the database
        numberRepository.saveAndFlush(number);

        // Get all the numberList
        restNumberMockMvc.perform(get("/api/numbers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(number.getId().intValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER.toString())));
    }

    @Test
    @Transactional
    public void getNumber() throws Exception {
        // Initialize the database
        numberRepository.saveAndFlush(number);

        // Get the number
        restNumberMockMvc.perform(get("/api/numbers/{id}", number.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(number.getId().intValue()))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingNumber() throws Exception {
        // Get the number
        restNumberMockMvc.perform(get("/api/numbers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNumber() throws Exception {
        // Initialize the database
        numberService.save(number);

        int databaseSizeBeforeUpdate = numberRepository.findAll().size();

        // Update the number
        Number updatedNumber = numberRepository.findOne(number.getId());
        updatedNumber
                .number(UPDATED_NUMBER);

        restNumberMockMvc.perform(put("/api/numbers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedNumber)))
            .andExpect(status().isOk());

        // Validate the Number in the database
        List<Number> numberList = numberRepository.findAll();
        assertThat(numberList).hasSize(databaseSizeBeforeUpdate);
        Number testNumber = numberList.get(numberList.size() - 1);
        assertThat(testNumber.getNumber()).isEqualTo(UPDATED_NUMBER);
    }

    @Test
    @Transactional
    public void updateNonExistingNumber() throws Exception {
        int databaseSizeBeforeUpdate = numberRepository.findAll().size();

        // Create the Number

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restNumberMockMvc.perform(put("/api/numbers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(number)))
            .andExpect(status().isCreated());

        // Validate the Number in the database
        List<Number> numberList = numberRepository.findAll();
        assertThat(numberList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteNumber() throws Exception {
        // Initialize the database
        numberService.save(number);

        int databaseSizeBeforeDelete = numberRepository.findAll().size();

        // Get the number
        restNumberMockMvc.perform(delete("/api/numbers/{id}", number.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Number> numberList = numberRepository.findAll();
        assertThat(numberList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
