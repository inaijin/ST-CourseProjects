Feature: Add a reservation for a user in a restaurant

  Scenario: User adds a reservation successfully
    Given a user with username "john_doe" and role "client"
    And a restaurant "La Piazza" managed by "manager_1"
    And the restaurant has a table with table number 1, restaurant ID 101, and 4 seats
    When the user reserves table 1 at "2024-06-01T19:00"
    Then the user should have 1 reservation
    And table 1 should be reserved at "2024-06-01T19:00"

  Scenario: User cannot reserve an already reserved table at the same time
    Given a user with username "jane_doe" and role "client"
    And a restaurant "Sushi Place" managed by "manager_2"
    And the restaurant has a table with table number 2, restaurant ID 102, and 6 seats
    And the table 2 with 4 seats is already reserved at "2024-06-02T18:00"
    When the user tries to reserve table 2 at "2024-06-02T18:00"
    Then the reservation should fail
