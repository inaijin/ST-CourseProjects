package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.MizdooniApplication;
import mizdooni.controllers.RestaurantController;
import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.model.RestaurantSearchFilter;
import mizdooni.response.PagedList;
import mizdooni.response.ResponseException;
import mizdooni.response.ResponseExceptionHandler;
import mizdooni.service.RestaurantService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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
    private Map<String, Object> requestBody;

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

        requestBody = Map.of(
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

    @Test
    public void testGetRestaurant_Success() throws Exception {
        int restaurantId = 0;

        Mockito.when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);

        mockMvc.perform(get("/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurant found"))
                .andExpect(jsonPath("$.data.name").value("Mock Restaurant"));
    }

    @Test
    public void testGetRestaurant_NotFound() throws Exception {
        int restaurantId = 999;

        Mockito.when(restaurantService.getRestaurant(restaurantId)).thenReturn(null);

        mockMvc.perform(get("/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("restaurant not found"));
    }

    @Test
    public void testGetRestaurants_Success() throws Exception {
        int page = 1;

        PagedList<Restaurant> mockPagedList = new PagedList<>(
                Arrays.asList(
                        mockRestaurant,
                        mockRestaurant2
                ),
                page,
                10
        );

        Mockito.when(restaurantService.getRestaurants(eq(page), any(RestaurantSearchFilter.class))).thenReturn(mockPagedList);

        mockMvc.perform(get("/restaurants").param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurants listed"))
                .andExpect(jsonPath("$.data.pageList[0].name").value("Mock Restaurant"))
                .andExpect(jsonPath("$.data.pageList[1].name").value("Mock Restaurant 2"));
    }

    @Test
    public void testGetRestaurants_BadRequest() throws Exception {
        int page = -1;

        Mockito.when(restaurantService.getRestaurants(eq(page), any(RestaurantSearchFilter.class)))
                .thenThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Invalid page number"));

        mockMvc.perform(get("/restaurants").param("page", String.valueOf(page)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid page number"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testGetManagerRestaurants_Success() throws Exception {
        int managerId = 1;

        List<Restaurant> mockRestaurants = Arrays.asList(
                mockRestaurant,
                mockRestaurant2
        );

        Mockito.when(restaurantService.getManagerRestaurants(managerId)).thenReturn(mockRestaurants);

        mockMvc.perform(get("/restaurants/manager/{managerId}", managerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("manager restaurants listed"))
                .andExpect(jsonPath("$.data[0].name").value("Mock Restaurant"))
                .andExpect(jsonPath("$.data[1].name").value("Mock Restaurant 2"));
    }

    @Test
    public void testGetManagerRestaurants_BadRequest() throws Exception {
        int managerId = -1;

        Mockito.when(restaurantService.getManagerRestaurants(managerId))
                .thenThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Invalid manager ID"));

        mockMvc.perform(get("/restaurants/manager/{managerId}", managerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid manager ID"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testAddRestaurant_Success() throws Exception {
        int newRestaurantId = 1;

        Mockito.when(restaurantService.addRestaurant(anyString(), anyString(), any(LocalTime.class), any(LocalTime.class),
                anyString(), any(Address.class), anyString())).thenReturn(newRestaurantId);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurant added"))
                .andExpect(jsonPath("$.data").value(newRestaurantId));
    }

    @Test
    public void testAddRestaurant_MissingKeys() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "name", "Incomplete Restaurant",
                "type", "Fast Food"
        );

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("parameters missing"));
    }

    @Test
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

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBodyInvalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("bad parameter type"));
    }

    @Test
    public void testAddRestaurant_ServiceException() throws Exception {
        Mockito.when(restaurantService.addRestaurant(anyString(), anyString(), any(LocalTime.class), any(LocalTime.class),
                        anyString(), any(Address.class), anyString()))
                .thenThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Service error"));

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Service error"));
    }

    @Test
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

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBodyEmpty)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("parameters missing"));
    }

    @Test
    public void testValidateRestaurantName_Available() throws Exception {
        String name = "Unique Restaurant";

        Mockito.when(restaurantService.restaurantExists(name)).thenReturn(false);

        mockMvc.perform(get("/validate/restaurant-name").param("data", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurant name is available"));
    }

    @Test
    public void testValidateRestaurantName_Conflict() throws Exception {
        String name = "Existing Restaurant";

        Mockito.when(restaurantService.restaurantExists(name)).thenReturn(true);

        mockMvc.perform(get("/validate/restaurant-name").param("data", name))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("restaurant name is taken"));
    }

    @Test
    public void testGetRestaurantTypes_Success() throws Exception {
        Set<String> types = Set.of("Fast Food", "Italian", "Persian");

        Mockito.when(restaurantService.getRestaurantTypes()).thenReturn(types);

        mockMvc.perform(get("/restaurants/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurant types"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", containsInAnyOrder("Fast Food", "Italian", "Persian")));
    }

    @Test
    public void testGetRestaurantTypes_BadRequest() throws Exception {
        Mockito.when(restaurantService.getRestaurantTypes())
                .thenThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Error fetching types"));

        mockMvc.perform(get("/restaurants/types"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error fetching types"));
    }

    @Test
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
    public void testGetRestaurantLocations_BadRequest() throws Exception {
        Mockito.when(restaurantService.getRestaurantLocations())
                .thenThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Error fetching locations"));

        mockMvc.perform(get("/restaurants/locations"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error fetching locations"));
    }

}
