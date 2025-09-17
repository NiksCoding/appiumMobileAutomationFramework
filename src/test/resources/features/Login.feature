@login @regression
Feature: Login Functionality
  As a user of the mobile application
  I want to be able to login with valid credentials
  So that I can access the application features

  Background:
    #Given the user is on the login screen
  Given user selects the language
  @android @positive @smoke
  Scenario: Successful login with valid credentials
    When the user enters username "demo@yopmail.com"
    And the user enters password "Test@123"
    And the user clicks the login button
    Then the user should be logged in successfully

#  @ios @positive @smoke
#  Scenario: Successful login with valid credentials on iOS
#    When the user performs login with username "validuser@example.com" and password "ValidPassword123"
#    Then the user should be logged in successfully
#
#  @negative @android @ios
#  Scenario Outline: Login with invalid credentials
#    When the user enters username "<username>"
#    And the user enters password "<password>"
#    And the user clicks the login button
#    Then the user should see an error message "<error_message>"
#
#    Examples:
#      | username               | password        | error_message           |
#      | invalid@example.com    | wrongpassword   | Invalid credentials     |
#      | validuser@example.com  | wrongpassword   | Invalid credentials     |
#      | invalid@example.com    | ValidPassword123| Invalid credentials     |
#      | ""                     | ValidPassword123| Username is required    |
#      | validuser@example.com  | ""              | Password is required    |
#
#  @boundary @android @ios
#  Scenario: Login with empty fields
#    When the user clears all input fields
#    Then the login button should be "disabled"
#
#  @functional @android @ios
#  Scenario: Forgot password functionality
#    When the user clicks forgot password link
#    Then the user should be navigated to forgot password screen
#
#  @security @android @ios
#  Scenario: Login with SQL injection attempt
#    When the user enters username "admin'; DROP TABLE users; --"
#    And the user enters password "password"
#    And the user clicks the login button
#    Then the user should see an error message "Invalid credentials"
#
#  @performance @android @ios
#  Scenario: Multiple login attempts
#    When the user performs login with username "validuser@example.com" and password "wrongpassword"
#    Then the user should see an error message "Invalid credentials"
#    When the user clears all input fields
#    And the user performs login with username "validuser@example.com" and password "ValidPassword123"
#    Then the user should be logged in successfully