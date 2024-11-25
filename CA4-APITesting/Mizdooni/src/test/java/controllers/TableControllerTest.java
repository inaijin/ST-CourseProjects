package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.MizdooniApplication;
import mizdooni.controllers.TableController;
import mizdooni.model.Restaurant;
import mizdooni.model.Table;
import mizdooni.response.ResponseException;
import mizdooni.response.ResponseExceptionHandler;
import mizdooni.service.RestaurantService;
import mizdooni.service.TableService;
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

import java.util.Arrays;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MizdooniApplication.class)
public class TableControllerTest {
    private MockMvc mockMvc;

    private Table mockTable1;
    private Table mockTable2;

    private Restaurant mockRestaurant;
    private int restaurantId;

    private Map<String, String> validRequestBody;

    @Autowired
    private TableController tableController;

    @MockBean
    private RestaurantService restaurantService;
    @MockBean
    private TableService tableService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(tableController)
                .setControllerAdvice(new ResponseExceptionHandler())
                .build();

        mockTable1 = new Table(1, 100, 4);
        mockTable2 = new Table(2, 100, 6);

        mockRestaurant = new Restaurant("Valid Restaurant", null, "Type", null, null,
                "Description", null, " ");
        restaurantId = 101;

        validRequestBody = Map.of("seatsNumber", "4");
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
    @DisplayName("Should retrieve tables for a valid restaurant")
    public void testGetTables_Success() throws Exception {
        Mockito.when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);

        Mockito.when(tableService.getTables(restaurantId)).thenReturn(Arrays.asList(mockTable1, mockTable2));

        mockMvc.perform(get("/tables/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("tables listed"))
                .andExpect(jsonPath("$.data[0].tableNumber").value(mockTable1.getTableNumber()))
                .andExpect(jsonPath("$.data[1].tableNumber").value(mockTable2.getTableNumber()));
    }

    @Test
    @DisplayName("Should return 400 if tables cannot be retrieved")
    public void testGetTables_BadRequest() throws Exception {
        Mockito.when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);
        Mockito.when(tableService.getTables(restaurantId)).thenThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Error retrieving tables"));

        performRequest("GET", "/tables/" + restaurantId, null, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should add a table to a valid restaurant")
    public void testAddTable_Success() throws Exception {
        Mockito.when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);

        Mockito.doNothing().when(tableService).addTable(restaurantId, 4);

        performRequest("POST", "/tables/" + restaurantId, validRequestBody, HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should return 400 if table parameters are missing")
    public void testAddTable_MissingParameters() throws Exception {
        Map<String, String> invalidRequestBody = Map.of();

        Mockito.when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);

        performRequest("POST", "/tables/" + restaurantId, invalidRequestBody, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should return 400 if table parameters have invalid data types")
    public void testAddTable_InvalidDataType() throws Exception {
        Map<String, String> invalidRequestBody = Map.of("seatsNumber", "invalid_number");

        Mockito.when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);

        performRequest("POST", "/tables/" + restaurantId, invalidRequestBody, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should return 400 if table cannot be added due to service error")
    public void testAddTable_ServiceException() throws Exception {
        Mockito.when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);

        Mockito.doThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Error adding table"))
                .when(tableService).addTable(restaurantId, 4);

        performRequest("POST", "/tables/" + restaurantId, validRequestBody, HttpStatus.BAD_REQUEST.value());
    }

}
