package controllers;

import mizdooni.controllers.ReviewController;
import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.model.Rating;
import mizdooni.model.Restaurant;
import mizdooni.model.Review;
import mizdooni.response.PagedList;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.ReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import mizdooni.controllers.ControllerUtils;
import mizdooni.controllers.ReviewController;
import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.model.Rating;
import mizdooni.model.Restaurant;
import mizdooni.model.Review;
import mizdooni.response.PagedList;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.ReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mizdooni.controllers.ControllerUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ReviewControllerTest {
    @Mock
    private RestaurantService restaurantService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private AutoCloseable mocks;

    @BeforeEach
    public void setup() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);

        Restaurant mockRestaurant = new Restaurant(
                "Mock Restaurant",
                null,
                null,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                "A cozy Italian restaurant.",
                null,
                " "
        );

        Field idField = Restaurant.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockRestaurant, 0);
        idField.setAccessible(false);

        when(restaurantService.getRestaurant(0)).thenReturn(mockRestaurant);
    }

    @AfterEach
    public void closeMocks() throws Exception {
        mocks.close();
    }

    private Map<String, Object> getDefaultParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great food!");
        Map<String, Number> rating = new HashMap<>();
        rating.put("food", 4.5);
        rating.put("service", 4.0);
        rating.put("ambiance", 3.5);
        rating.put("overall", 4.2);
        params.put("rating", rating);
        return params;
    }

    private void assertResponseMatches(Response expectedResponse, Response actualResponse) throws Exception {
        assertEquals(getField(expectedResponse, "status"), getField(actualResponse, "status"), "Status field mismatch");
        assertEquals(getField(expectedResponse, "message"), getField(actualResponse, "message"), "Message field mismatch");
        assertEquals(getField(expectedResponse, "success"), getField(actualResponse, "success"), "Success field mismatch");
        assertEquals(getField(expectedResponse, "data"), getField(actualResponse, "data"), "Data field mismatch");
    }

    private Object getField(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object receivedObj = field.get(object);
        field.setAccessible(false);
        return receivedObj;
    }

    @Test
    @DisplayName("Test getReviews with valid input")
    public void testGetReviewsSuccess() throws Exception {
        int restaurantId = 0;
        int page = 1;

        PagedList<Review> reviews = new PagedList<>(List.of(new Review(null, new Rating(), "Great!", null)), page, 10);
        when(reviewService.getReviews(restaurantId, page)).thenReturn(reviews);

        Response response = reviewController.getReviews(restaurantId, page);
        Response expectedResponse = Response.ok("reviews for restaurant (0): Mock Restaurant", reviews);

        verify(restaurantService).getRestaurant(restaurantId);
        verify(reviewService).getReviews(restaurantId, page);
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test getReviews when reviewService throws an exception")
    public void testGetReviewsServiceException() throws RestaurantNotFound {
        int restaurantId = 0;
        int page = 1;

        doThrow(new RuntimeException("Service failure")).when(reviewService).getReviews(restaurantId, page);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reviewController.getReviews(restaurantId, page);
        });

        verify(restaurantService).getRestaurant(restaurantId);
        verify(reviewService).getReviews(restaurantId, page);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Service failure", exception.getMessage());
    }

    @Test
    @DisplayName("Test addReview with valid parameters")
    public void testAddReviewSuccess() throws Exception {
        int restaurantId = 0;
        Map<String, Object> params = getDefaultParams();

        doNothing().when(reviewService).addReview(eq(restaurantId), any(Rating.class), eq("Great food!"));

        Response response = reviewController.addReview(restaurantId, params);
        Response expectedResponse = Response.ok("review added successfully");

        verify(restaurantService).getRestaurant(restaurantId);
        verify(reviewService).addReview(eq(restaurantId), any(Rating.class), eq("Great food!"));
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test addReview with missing parameters")
    public void testAddReviewMissingParams() {
        int restaurantId = 0;
        Map<String, Object> params = new HashMap<>();

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reviewController.addReview(restaurantId, params);
        });

        verifyNoInteractions(reviewService);
        verify(restaurantService).getRestaurant(restaurantId);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test addReview with invalid parameter types")
    public void testAddReviewParamsBadType() {
        int restaurantId = 0;
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great food!");
        params.put("rating", "Invalid Rating");

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reviewController.addReview(restaurantId, params);
        });

        verifyNoInteractions(reviewService);
        verify(restaurantService).getRestaurant(restaurantId);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test addReview when reviewService throws an exception")
    public void testAddReviewServiceException() throws Exception {
        int restaurantId = 0;
        Map<String, Object> params = getDefaultParams();

        doThrow(new RuntimeException("Review service failure")).when(reviewService).addReview(eq(restaurantId), any(Rating.class), eq("Great food!"));

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reviewController.addReview(restaurantId, params);
        });

        verify(restaurantService).getRestaurant(restaurantId);
        verify(reviewService).addReview(eq(restaurantId), any(Rating.class), eq("Great food!"));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Review service failure", exception.getMessage());
    }
}
