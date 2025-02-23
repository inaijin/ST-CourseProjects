package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.MizdooniApplication;
import mizdooni.controllers.RestaurantController;
import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.response.PagedList;
import mizdooni.response.ResponseException;
import mizdooni.response.ResponseExceptionHandler;
import mizdooni.service.RestaurantService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MizdooniApplication.class)
public class RestaurantControllerTest {
    private MockMvc mockMvc;
    private Restaurant mockRestaurant;
    private Restaurant mockRestaurant2;
    private Map<String, Object> validRequestBody;

    @Autowired
    private RestaurantController restaurantController;

    @MockBean
    private RestaurantService restaurantService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(restaurantController)
                .setControllerAdvice(new ResponseExceptionHandler())
                .build();

        mockRestaurant = new Restaurant(
                "Mock Restaurant",
                null,
                "Fast Food",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                "A cozy Italian restaurant.",
                new Address("Country", "City", "Street"),
                " "
        );

        mockRestaurant2 = new Restaurant(
                "Mock Restaurant 2",
                null,
                "Persian Food",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                "A cozy Persian restaurant.",
                new Address("Country", "City", "Street"),
                " "
        );

        validRequestBody = Map.of(
                "name", "New Restaurant",
                "type", "Fast Food",
                "startTime", "09:00",
                "endTime", "22:00",
                "description", "A new restaurant in town.",
                "image", "image_link",
                "address", Map.of(
                        "country", "Country A",
                        "city", "City A",
                        "street", "Street A"
                )
        );
    }

    private void performRequest(String method, String url, Object requestBody, int expectedStatus) throws Exception {
        if (method.equalsIgnoreCase("GET")) {
            mockMvc.perform(get(url))
                    .andExpect(status().is(expectedStatus));
        } else if (method.equalsIgnoreCase("POST")) {
            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().is(expectedStatus));
        }
    }

    @Test
    @DisplayName("Should retrieve a restaurant successfully")
    public void testGetRestaurant_Success() throws Exception {
        int restaurantId = 0;

        Mockito.when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);

        performRequest("GET", "/restaurants/" + restaurantId, null, HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should return 404 for a non-existent restaurant")
    public void testGetRestaurant_NotFound() throws Exception {
        int restaurantId = 999;

        Mockito.when(restaurantService.getRestaurant(restaurantId)).thenReturn(null);

        performRequest("GET", "/restaurants/" + restaurantId, null, HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Should list restaurants for valid input")
    public void testGetRestaurants_Success() throws Exception {
        int page = 1;

        List<Restaurant> restaurants = Arrays.asList(mockRestaurant, mockRestaurant2);

        Mockito.when(restaurantService.getRestaurants(eq(page), any())).thenReturn(
                new PagedList<>(restaurants, page, 10)
        );

        mockMvc.perform(get("/restaurants").param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurants listed"))
                .andExpect(jsonPath("$.data.pageList[0].name").value("Mock Restaurant"))
                .andExpect(jsonPath("$.data.pageList[1].name").value("Mock Restaurant 2"));
    }

    @Test
    @DisplayName("Should return 400 for invalid page number in restaurant list")
    public void testGetRestaurants_BadRequest() throws Exception {
        int page = -1;

        Mockito.when(restaurantService.getRestaurants(eq(page), any())).thenThrow(
                new ResponseException(HttpStatus.BAD_REQUEST, "Invalid page number")
        );

        performRequest("GET", "/restaurants?page=" + page, null, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should retrieve manager's restaurants successfully")
    public void testGetManagerRestaurants_Success() throws Exception {
        int managerId = 1;

        List<Restaurant> mockRestaurants = Arrays.asList(mockRestaurant, mockRestaurant2);

        Mockito.when(restaurantService.getManagerRestaurants(managerId)).thenReturn(mockRestaurants);

        performRequest("GET", "/restaurants/manager/" + managerId, null, HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should return 400 for invalid manager ID")
    public void testGetManagerRestaurants_BadRequest() throws Exception {
        int managerId = -1;

        Mockito.when(restaurantService.getManagerRestaurants(managerId))
                .thenThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Invalid manager ID"));

        performRequest("GET", "/restaurants/manager/" + managerId, null, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should add a new restaurant successfully")
    public void testAddRestaurant_Success() throws Exception {
        int newRestaurantId = 1;

        Mockito.when(restaurantService.addRestaurant(anyString(), anyString(), any(LocalTime.class), any(LocalTime.class),
                anyString(), any(Address.class), anyString())).thenReturn(newRestaurantId);

        performRequest("POST", "/restaurants", validRequestBody, HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should return 400 for missing keys in addRestaurant")
    public void testAddRestaurant_MissingKeys() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "name", "Incomplete Restaurant",
                "type", "Fast Food"
        );

        performRequest("POST", "/restaurants", requestBody, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should return 400 for invalid data types in addRestaurant")
    public void testAddRestaurant_InvalidDataType() throws Exception {
        Map<String, Object> requestBodyInvalid = Map.of(
                "name", "Invalid Restaurant",
                "type", "Fast Food",
                "startTime", 900,
                "endTime", "22:00",
                "description", "A new restaurant in town.",
                "address", Map.of(
                        "country", "Country A",
                        "city", "City A",
                        "street", "Street A"
                )
        );

        performRequest("POST", "/restaurants", requestBodyInvalid, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should handle service exception during addRestaurant")
    public void testAddRestaurant_ServiceException() throws Exception {
        Mockito.when(restaurantService.addRestaurant(anyString(), anyString(), any(LocalTime.class), any(LocalTime.class),
                        anyString(), any(Address.class), anyString()))
                .thenThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Service error"));

        performRequest("POST", "/restaurants", validRequestBody, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should return 400 for empty fields in addRestaurant")
    public void testAddRestaurant_EdgeCase_EmptyFields() throws Exception {
        Map<String, Object> requestBodyEmpty = Map.of(
                "name", "",
                "type", "",
                "startTime", "09:00",
                "endTime", "22:00",
                "description", "",
                "address", Map.of(
                        "country", "",
                        "city", "",
                        "street", ""
                )
        );

        performRequest("POST", "/restaurants", requestBodyEmpty, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should validate restaurant name as available")
    public void testValidateRestaurantName_Available() throws Exception {
        String name = "Unique Restaurant";

        Mockito.when(restaurantService.restaurantExists(name)).thenReturn(false);

        performRequest("GET", "/validate/restaurant-name?data=" + name, null, HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should validate restaurant name as taken")
    public void testValidateRestaurantName_Conflict() throws Exception {
        String name = "Existing Restaurant";

        Mockito.when(restaurantService.restaurantExists(name)).thenReturn(true);

        performRequest("GET", "/validate/restaurant-name?data=" + name, null, HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Should retrieve restaurant types successfully")
    public void testGetRestaurantTypes_Success() throws Exception {
        Set<String> types = Set.of("Fast Food", "Italian", "Persian");

        Mockito.when(restaurantService.getRestaurantTypes()).thenReturn(types);

        mockMvc.perform(get("/restaurants/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurant types"))
                .andExpect(jsonPath("$.data", containsInAnyOrder("Fast Food", "Italian", "Persian")));
    }

    @Test
    @DisplayName("Should return 400 for restaurant types fetch failure")
    public void testGetRestaurantTypes_BadRequest() throws Exception {
        Mockito.when(restaurantService.getRestaurantTypes())
                .thenThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Error fetching types"));

        performRequest("GET", "/restaurants/types", null, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should retrieve restaurant locations successfully")
    public void testGetRestaurantLocations_Success() throws Exception {
        Map<String, Set<String>> locations = Map.of(
                "Country A", Set.of("City X", "City Y"),
                "Country B", Set.of("City Z")
        );

        Mockito.when(restaurantService.getRestaurantLocations()).thenReturn(locations);

        mockMvc.perform(get("/restaurants/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurant locations"))
                .andExpect(jsonPath("$.data['Country A']", Matchers.containsInAnyOrder("City X", "City Y")))
                .andExpect(jsonPath("$.data['Country B']", Matchers.contains("City Z")));
    }

    @Test
    @DisplayName("Should return 400 for restaurant locations fetch failure")
    public void testGetRestaurantLocations_BadRequest() throws Exception {
        Mockito.when(restaurantService.getRestaurantLocations())
                .thenThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Error fetching locations"));

        performRequest("GET", "/restaurants/locations", null, HttpStatus.BAD_REQUEST.value());
    }
}
