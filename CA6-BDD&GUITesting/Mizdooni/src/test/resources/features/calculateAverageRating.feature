Feature: Calculate average rating for a restaurant

  Scenario: Calculate average rating when there are no reviews
    Given a Restaurant "Perperok" managed by "manager_1"
    When the average rating is calculated
    Then the average rating should be 0.0 for all categories

  Scenario: Calculate average rating with multiple reviews same person
    Given a Restaurant "Sushi Spot" managed by "manager_2"
    And a user "mamad" adds a review with food rating 5.0, service rating 4.5, ambiance rating 4.5, and overall rating 5.0
    And another user "mamad" adds a review with food rating 4.5, service rating 4.0, ambiance rating 4.5, and overall rating 4.5
    When the average rating is calculated
    Then the average food rating should be 4.75
    And the average service rating should be 4.25
    And the average ambiance rating should be 4.5
    And the average overall rating should be 4.75

  Scenario: Calculate average rating when all reviews have identical ratings different people
    Given a Restaurant "Grill House" managed by "manager_3"
    And a user "hesam" adds a review with food rating 3.0, service rating 3.0, ambiance rating 3.0, and overall rating 3.0
    And another user "ali" adds a review with food rating 3.0, service rating 3.0, ambiance rating 3.0, and overall rating 3.0
    When the average rating is calculated
    Then the average food rating should be 3.0
    And the average service rating should be 3.0
    And the average ambiance rating should be 3.0
    And the average overall rating should be 3.0
