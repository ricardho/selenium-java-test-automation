@ui @bdd
Feature: Login
  As a Swag Labs customer
  I want to log in to the store
  So that I can browse and buy products

  Background:
    Given I am on the login page

  @smoke
  Scenario: Successful login with a standard user
    When I log in as a standard user
    Then I should see the products page

  Scenario Outline: Login is rejected for <case>
    When I try to log in with username "<username>" and password "<password>"
    Then I should see the login error "<message>"

    # ${SAUCE_PASSWORD} is resolved from configuration at runtime, so no secret is written here.
    Examples:
      | case              | username        | password          | message                                                                   |
      | a locked out user | locked_out_user | ${SAUCE_PASSWORD} | Epic sadface: Sorry, this user has been locked out.                       |
      | a wrong password  | standard_user   | wrong_pass        | Epic sadface: Username and password do not match any user in this service |
      | an empty username |                 | ${SAUCE_PASSWORD} | Epic sadface: Username is required                                        |
      | an empty password | standard_user   |                   | Epic sadface: Password is required                                        |
