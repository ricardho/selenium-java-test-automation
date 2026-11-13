package com.automation.bdd;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Entry point for Cucumber scenarios on the JUnit Platform.
 * Filter by tag with: mvn test -DskipJunit -Dcucumber.filter.tags="@smoke"
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.automation.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "summary, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm, html:target/cucumber-report.html")
public class RunCucumberTest {
}
