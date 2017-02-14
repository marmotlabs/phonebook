package com.sandbox.phonebook.cucumber.stepdefs;

import com.sandbox.phonebook.PhonebookApp;
import com.sandbox.phonebook.domain.Person;
import com.sandbox.phonebook.repository.NumberRepository;
import com.sandbox.phonebook.repository.PersonRepository;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PhonebookApp.class)
@ContextConfiguration
public class PhonebookStepDefs {

    private ResultActions actions;

    @Inject
    private WebApplicationContext context;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private NumberRepository numberRepository;

    private MockMvc mockMvc;

    /**
     * Called before each Scenario
     */
    @Before
    public void setup() {
        // Clear the DB
        numberRepository.deleteAll();
        personRepository.deleteAll();

        // Builds the mock MVC
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * Creates a person by POSTing on the mock MVC a JSON payload with all the required Person fields.
     *
     * @param name
     * @throws Throwable
     */
    @When("^I add a person with the name '(.*)'$")
    public void iAddPerson(String name) throws Throwable {
        actions = mockMvc.perform(post("/api/people")
                .content("{\"name\": \"" + name + "\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    /**
     * Checks that the person is created by asserting that the size of the DB is 1 (remember that the DB is cleared upon each scenario), and also by
     * asserting that the HTTP Status Code is 201.
     * <p>
     * If other persons will be added to the DB in a Given step, this won't work anymore.
     *
     * @throws Throwable
     */
    @Then("^the person is created$")
    public void thePersonIsCreated() throws Throwable {
        actions
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Then("^the person is not created$")
    public void thePersonIsNotCreated() throws Throwable {
        actions
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    /**
     * Checks that the name of the only person we expect in the DB is the given name, and also that the HTTP Response contains the correct name at the
     * correct Json path.
     * <p>
     * The HTTP assertions are made on the {@code actions} variable that's expected to be populated by a previous step.
     *
     * @param name
     * @throws Throwable
     */
    @Then("^her name is '(.*)'$")
    public void herNameIs(String name) throws Throwable {
        // According to: https://github.com/jayway/JsonPath
        // $.name is something like: [{"name": "sofia}]
        // ----------------------------^
        actions.andExpect(jsonPath("$.name").value(name));
    }

    /**
     * Loops over all the people properties from the Gerkin data tables and inserts a new Person into the database with that name.
     * <p>
     * It ignores the age.
     *
     * @param peopleDataTable
     */
    @Given("^these are the people already created$")
    public void theseAreThePeopleAlreadyCreated(DataTable peopleDataTable) {
        for (Map<String, String> row : peopleDataTable.asMaps(String.class, String.class)) {
            Person person = new Person();
            person.setName(row.get("name"));
            personRepository.saveAndFlush(person);
        }
    }

    /**
     * Calls the GET /api/people on the mock MVC, and stores the result in the {@code actions} variable.
     *
     * @throws Throwable
     */
    @When("^I get all people$")
    public void iGetAllPeople() throws Throwable {
        actions = mockMvc.perform(get("/api/people").accept(MediaType.APPLICATION_JSON));
    }

    /**
     * Loops over all the people properties from the Gerkin data table and checks if the JSON HTTP response contains an element having its "name"
     * field equal to the value from the data table.
     * <p>
     * To make sure that we don't have on the HTTP response more entities than the ones in the data table, it first performs a check on the size.
     *
     * @param peopleDataTable
     * @throws Exception
     */
    @Then("^these are the people I get$")
    public void theseAreThePeopleIGet(DataTable peopleDataTable) throws Exception {
        List<Map<String, String>> people = peopleDataTable.asMaps(String.class, String.class);

        actions.andExpect(jsonPath("$.length()").value(is(people.size())));

        for (Map<String, String> row : people) {
            // Take the Json element with the name equal to the data table name
            // This command: http://jsonpath.herokuapp.com/?path=$[?(@.name == 'sofia')] on this body:
            // [{"name":"sofia", "age":"22"},{"name":"yoyo", "age":"3"}] will return: {"name":"sofia", "age":"22"}

            // More here: https://github.com/jayway/JsonPath
            actions.andExpect(jsonPath("$[?(@.name =='" + row.get("name") + "')]").exists());
        }
    }

    @When("^I delete person with the name '(.*)'$")
    public void thePersonIsDeleted(String name) throws Throwable {
        Long id = personRepository
                .findAll()
                .stream()
                .filter(person -> person.getName().equals(name))
                .findFirst()
                .get()
                .getId();
        actions = mockMvc.perform(delete("/api/people/" + id).accept(MediaType.APPLICATION_JSON));
    }

    @When("^I update the person with the name '(.*)' by '(.*)'$")
    public void thePersonIsUpdated(String oldName, String newName) throws Throwable {
        Long id = personRepository
                .findAll()
                .stream()
                .filter(person -> person.getName().equals(oldName))
                .findFirst()
                .get()
                .getId();
        actions = mockMvc.perform(put("/api/people")
                .content("{\"name\": \"" + newName + "\", \"id\": \"" + id + "\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    @When("^I add phone number '(.*)' to person '(.*)'$")
    public void iAddPhoneNumberToPerson(String phoneNumber, String personName) throws Throwable {
        Long personId = personRepository
                .findAll()
                .stream()
                .filter(person -> person.getName().equals(personName))
                .findFirst()
                .get().getId();
        actions = mockMvc.perform(post("/api/numbers")
                .content("{\"number\": \"" + phoneNumber + "\",\"person\":{\"name\": \"" + personName + "\", \"id\": \"" + personId + "\"}}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


    }

    @Then("^the phone number is added$")
    public void thePhoneNumberIsAdded() throws Throwable {
        assertThat(numberRepository.count()).isEqualTo(1L);

        actions
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @And("^'(.*)' has now the new phoneNumber '(.*)'$")
    public void personHasNowTheNewPhoneNumber(String personName, String phoneNumber) throws Throwable {
        assertThat(
                numberRepository
                        .findAll()
                        .stream()
                        .findFirst()
                        .filter(it -> it.getPerson().getName().equals(personName))
                        .get().getNumber().equals(phoneNumber));

        // According to: https://github.com/jayway/JsonPath
        // $.name is something like: [{"name": "sofia}]
        // ----------------------------^
        actions.andExpect(jsonPath("$.number").value(phoneNumber));
        actions.andExpect(jsonPath("$.person.name").value(personName));
    }
}
