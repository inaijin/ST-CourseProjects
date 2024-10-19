package model;

import mizdooni.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RestaurantTest {
    private Restaurant restaurant;
    private User user;
    private User manager;
    private Table table1, table2;
    private Review review;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        manager = mock(User.class);
        restaurant = new Restaurant("Shila", manager, "Fast Food", LocalTime.of(12, 0),
                LocalTime.of(22, 0), "Best food!", mock(Address.class), " ");

        table1 = mock(Table.class);
        table2 = mock(Table.class);

        when(table1.getTableNumber()).thenReturn(1);
        when(table2.getTableNumber()).thenReturn(2);
        when(table1.getSeatsNumber()).thenReturn(4);
        when(table2.getSeatsNumber()).thenReturn(6);

        review = mock(Review.class);
        when(review.getUser()).thenReturn(user);
    }

    @Test
    @DisplayName("Test Get Table - Table Found")
    void testGetTableByTableNumber() {
        restaurant.addTable(table1);
        restaurant.addTable(table2);

        Table found = restaurant.getTable(2);
        assertNotNull(found, "Table should be found.");
        assertEquals(2, found.getTableNumber(), "The table number should match.");

        Table found2 = restaurant.getTable(1);
        assertNotNull(found2, "Table should be found.");
        assertEquals(1, found2.getTableNumber(), "The table number should match.");
    }

    @Test
    @DisplayName("Test Get Table - Table Not Found")
    void testGetTable_NotFound() {
        Table found = restaurant.getTable(99);
        assertNull(found, "Table should not be found for non-existing table number.");
    }

    @Test
    @DisplayName("Test AddTable - Add One Table to Restaurant")
    void testAddTable() {
        restaurant.addTable(table1);
        assertEquals(1, restaurant.getTables().size(), "Restaurant should have one table.");
        assertEquals(table1, restaurant.getTables().getFirst(), "The table added should match the one retrieved.");
    }

    @Test
    @DisplayName("Test AddTable - Add Multiple Tables to Restaurant")
    void testAddMultipleTables() {
        restaurant.addTable(table1);
        restaurant.addTable(table2);

        List<Table> tables = restaurant.getTables();
        assertEquals(2, tables.size(), "Restaurant should have two tables.");
        assertEquals(table1, tables.get(0), "First table should match the first added table.");
        assertEquals(table2, tables.get(1), "Second table should match the second added table.");
    }

    @Test
    @DisplayName("Test AddTable - Add One Table to Restaurant with Table Number Update")
    void testAddTableWithNumberUpdate() {
        restaurant.addTable(table1);
        verify(table1).setTableNumber(1);
    }

    @Test
    @DisplayName("Test AddTable - Add Multiple Tables to Restaurant with Table Number Update")
    void testAddMultipleTablesWithNumberUpdate() {
        restaurant.addTable(table1);
        restaurant.addTable(table2);

        verify(table1).setTableNumber(1);
        verify(table2).setTableNumber(2);
    }

    @Test
    @DisplayName("Test Add Table - Adding Duplicate Tables Should Assign Unique Table Numbers")
    void shouldAssignUniqueTableNumbersWhenAddingDuplicateTables() {
        Table duplicateTable1 = mock(Table.class);
        Table duplicateTable2 = mock(Table.class);

        restaurant.addTable(duplicateTable1);
        restaurant.addTable(duplicateTable2);

        verify(duplicateTable1).setTableNumber(1);
        verify(duplicateTable2).setTableNumber(2);
        assertEquals(2, restaurant.getTables().size(), "Restaurant should have two tables.");
    }

    @Test
    @DisplayName("Test Add Reviews - One Review to Restaurant")
    void testAddReviews() {
        restaurant.addReview(review);

        assertEquals(1, restaurant.getReviews().size(), "Restaurant should have one review.");
        assertEquals(review, restaurant.getReviews().getFirst(), "First review should have been selected.");
    }

    @Test
    @DisplayName("Test Add Reviews - Two Reviews by Different People to Restaurant")
    void testAddMultipleDifferentReviews() {
        restaurant.addReview(review);

        Review reviewNew = new Review(manager, mock(Rating.class), "Good", LocalDateTime.now());
        restaurant.addReview(reviewNew);

        List<Review> reviews = restaurant.getReviews();
        assertEquals(2, reviews.size(), "Restaurant should have two reviews.");
        assertEquals(review, reviews.get(0), "First review should have been selected.");
        assertEquals(reviewNew, reviews.get(1), "New review should have been selected.");
    }

    @Test
    @DisplayName("Test Add Reviews - Two Reviews by Same Person to Restaurant")
    void testAddMultipleSameReviews() {
        Review review2 = mock(Review.class);
        when(review2.getUser()).thenReturn(user);

        restaurant.addReview(review);
        restaurant.addReview(review2);

        List<Review> reviews = restaurant.getReviews();
        assertEquals(1, reviews.size(), "Restaurant should have only one review (override case).");
        assertEquals(review2, reviews.getFirst(), "Second review should have overridden the first.");
    }

    @ParameterizedTest
    @CsvSource({
            "5.0, 4.5, 4.5, 5.0, 4.5, 4.0, 4.5, 4.5",
            "4.0, 3.5, 4.5, 4.0, 4.0, 3.5, 4.5, 4.0",
            "3.1, 4.2, 2.1, 3.8, 4.2, 5, 3.9, 4.32"
    })
    @DisplayName("Test Calculate Average Rating - Multiple Reviews")
    void testCalculateAverageRating(double food1, double service1, double ambiance1, double overall1,
                                    double food2, double service2, double ambiance2, double overall2) {
        Rating rating1 = new Rating();
        rating1.food = food1;
        rating1.service = service1;
        rating1.ambiance = ambiance1;
        rating1.overall = overall1;

        Rating rating2 = new Rating();
        rating2.food = food2;
        rating2.service = service2;
        rating2.ambiance = ambiance2;
        rating2.overall = overall2;

        Review review1 = new Review(user, rating1, "Review 1", LocalDateTime.now());
        Review review2 = new Review(manager, rating2, "Review 2", LocalDateTime.now());
        restaurant.addReview(review1);
        restaurant.addReview(review2);

        Rating avgRating = restaurant.getAverageRating();
        assertEquals((food1 + food2) / 2, avgRating.food, 0.01, "Average food rating should match.");
        assertEquals((service1 + service2) / 2, avgRating.service, 0.01, "Average service rating should match.");
        assertEquals((ambiance1 + ambiance2) / 2, avgRating.ambiance, 0.01, "Average ambiance rating should match.");
        assertEquals((overall1 + overall2) / 2, avgRating.overall, 0.01, "Average overall rating should match.");
    }

    @Test
    @DisplayName("Test Calculate Average Rating - No Reviews")
    void testCalculateAverageRating_NoReviews() {
        Rating avgRating = restaurant.getAverageRating();

        assertEquals(0.0, avgRating.food, "Average food rating should be 0 when there are no reviews.");
        assertEquals(0.0, avgRating.service, "Average service rating should be 0 when there are no reviews.");
        assertEquals(0.0, avgRating.ambiance, "Average ambiance rating should be 0 when there are no reviews.");
        assertEquals(0.0, avgRating.overall, "Average overall rating should be 0 when there are no reviews.");
    }

    @Test
    @DisplayName("Test Get Max Seats Number of Tables")
    void testGetMaxSeatsNumber() {
        restaurant.addTable(table1);
        restaurant.addTable(table2);

        assertEquals(6, restaurant.getMaxSeatsNumber(), "Max seats number should be 6.");
    }

    @Test
    @DisplayName("Test Get Max Seats Number with No Tables")
    void testGetMaxSeatsNumber_NoTables() {
        int maxSeats = restaurant.getMaxSeatsNumber();

        assertEquals(1, maxSeats, "Max seats number should be 1 when there are no tables.");
    }

}
