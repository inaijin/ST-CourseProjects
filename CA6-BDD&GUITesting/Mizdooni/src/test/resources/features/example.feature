Feature: Basic Spring Boot Integration with Cucumber

  Scenario: Verify basic setup
    Given the application is running
    When I access the home page
    Then I should see "Welcome to Spring Boot"
