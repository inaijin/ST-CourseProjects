package controllers;

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
import java.lang.reflect.Method;
import java.time.LocalTime;
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
    private MockedStatic<ControllerUtils> mockedControllerUtils;

    @BeforeEach
    public void setup() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        setupStaticMocks();
    }

    @AfterEach
    public void closeMocks() throws Exception {
        resetStaticMocks();
        mocks.close();
        mockedControllerUtils.close();
    }

    private void setupStaticMocks() throws Exception {
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

        Method checkRestaurantMethod = ControllerUtils.class.getDeclaredMethod("checkRestaurant", int.class, RestaurantService.class);
        checkRestaurantMethod.setAccessible(true);
        mockedControllerUtils = mockStatic(ControllerUtils.class);
        mockedControllerUtils.when(() -> checkRestaurantMethod.invoke(null, anyInt(), any(RestaurantService.class)))
                .thenReturn(mockRestaurant);

        Method containsKeysMethod = ControllerUtils.class.getDeclaredMethod("containsKeys", Map.class, String[].class);
        containsKeysMethod.setAccessible(true);
        mockedControllerUtils.when(() -> containsKeysMethod.invoke(null, any(Map.class), any(String[].class)))
                .thenReturn(true);
    }

    private void resetStaticMocks() {
        mockedControllerUtils.clearInvocations();
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

        verify(reviewService).addReview(eq(restaurantId), any(Rating.class), eq("Great food!"));
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test addReview with missing parameters")
    public void testAddReviewMissingParams() throws NoSuchMethodException {
        int restaurantId = 1;
        Map<String, Object> params = new HashMap<>();

        Method containsKeysMethod = ControllerUtils.class.getDeclaredMethod("containsKeys", Map.class, String[].class);
        containsKeysMethod.setAccessible(true);
        mockedControllerUtils.when(() -> containsKeysMethod.invoke(null, any(Map.class), any(String[].class)))
                .thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reviewController.addReview(restaurantId, params);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test addReview with invalid parameter types")
    public void testAddReviewParamsBadType() {
        int restaurantId = 1;
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great food!");
        params.put("rating", "Invalid Rating");

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reviewController.addReview(restaurantId, params);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test addReview when reviewService throws an exception")
    public void testAddReviewServiceException() throws Exception {
        int restaurantId = 1;
        Map<String, Object> params = getDefaultParams();

        doThrow(new RuntimeException("Review service failure")).when(reviewService).addReview(eq(restaurantId), any(Rating.class), eq("Great food!"));

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reviewController.addReview(restaurantId, params);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Review service failure", exception.getMessage());
    }
}
