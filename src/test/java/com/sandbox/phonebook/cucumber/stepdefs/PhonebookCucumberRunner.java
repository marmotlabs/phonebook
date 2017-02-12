package com.sandbox.phonebook.cucumber.stepdefs;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features/"},
        glue = {"com.sandbox.phonebook.cucumber.stepdefs"},
        format = {"pretty", "json:target/cucumber.json", "html:target/cucumber"}
)
public class PhonebookCucumberRunner {
}
