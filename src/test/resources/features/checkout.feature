@ui @bdd
Feature: Checkout
  As a Swag Labs customer
  I want to check out the products in my cart
  So that I can complete my purchase

  Background:
    Given I am logged in as a standard user

  @smoke
  Scenario: Complete a purchase with multiple products
    When I add the following products to the cart:
      | Sauce Labs Backpack   |
      | Sauce Labs Bike Light |
    And I check out with valid customer information
    Then the order overview should list the selected products
    And the order total should equal the item total plus tax
    When I finish the order
    Then I should see the confirmation message "Thank you for your order!"

  Scenario Outline: Checkout requires the <field>
    When I add the following products to the cart:
      | Sauce Labs Onesie |
    And I check out leaving "<field>" empty
    Then I should see the checkout error "<message>"

    Examples:
      | field       | message                        |
      | first name  | Error: First Name is required  |
      | last name   | Error: Last Name is required   |
      | postal code | Error: Postal Code is required |
