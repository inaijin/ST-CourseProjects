Feature: Add reviews to a restaurant

  Scenario: Add one review when no other reviews exist
    Given a restaurant "La Piazza" managed by user "manager_1"
    When a user "john_doe" adds a review with the comment "Great food!" and rating 5
    Then the restaurant should have 1 review
    And the review comment should be "Great food!"

  Scenario: Add two reviews by the same user, where the second review overrides the first
    Given a restaurant "Sushi Spot" managed by user "manager_2"
    And a user "emma_smith" adds a review with the comment "Nice ambiance" and rating 4
    When the same user adds another review with the comment "Excellent service" and rating 5
    Then the restaurant should have 1 review
    And the review comment should be "Excellent service"

  Scenario: Add two reviews by different users, both reviews should be present
    Given a restaurant "Grill House" managed by user "manager_3"
    And a user "john_doe" adds a review with the comment "Tasty BBQ" and rating 5
    When another user "jane_doe" adds a review with the comment "Loved the ribs!" and rating 4
    Then the restaurant should have 2 reviews
    And the first review comment should be "Tasty BBQ"
    And the second review comment should be "Loved the ribs!"
