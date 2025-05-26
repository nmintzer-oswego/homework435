package oswego.csc435.noah.homework435.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import oswego.csc435.noah.homework435.models.Recipe;
import oswego.csc435.noah.homework435.models.RecipeIngredient;
import oswego.csc435.noah.homework435.models.Ingredient;
import oswego.csc435.noah.homework435.services.RecipeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RecipeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();
    }

    @Test
    void testGetAllRecipes() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(
            Arrays.asList(
                new Recipe(1, "Pasta Carbonara"),
                new Recipe(2, "Pizza Margherita")
            )
        );

        // Act & Assert
        mockMvc.perform(get("/recipes"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("Pasta Carbonara")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].name", is("Pizza Margherita")));

        verify(recipeService, times(1)).getAllRecipes();
    }

    @Test
    void testGetAllRecipesEmpty() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/recipes"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(recipeService, times(1)).getAllRecipes();
    }

    @Test
    void testGetRecipeById_Success() throws Exception {
        // Arrange
        when(recipeService.getRecipeById(1))
            .thenReturn(Optional.of(new Recipe(1, "Pasta Carbonara")));

        // Act & Assert
        mockMvc.perform(get("/recipes/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Pasta Carbonara")));

        verify(recipeService, times(1)).getRecipeById(1);
    }

    @Test
    void testGetRecipeById_NotFound() throws Exception {
        // Arrange
        when(recipeService.getRecipeById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/recipes/999"))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(recipeService, times(1)).getRecipeById(999);
    }

    @Test
    void testCreateRecipe() throws Exception {
        // Arrange
        Recipe newRecipe = new Recipe("Chocolate Cake");
        Recipe savedRecipe = new Recipe(3, "Chocolate Cake");
        
        when(recipeService.saveRecipe(any(Recipe.class))).thenReturn(savedRecipe);

        // Act & Assert
        mockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRecipe)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.name", is("Chocolate Cake")));

        verify(recipeService, times(1)).saveRecipe(any(Recipe.class));
    }

    @Test
    void testUpdateRecipe_Success() throws Exception {
        // Arrange
        Recipe updatedRecipe = new Recipe(1, "Pasta Bolognese");
        
        when(recipeService.getRecipeById(1))
            .thenReturn(Optional.of(new Recipe(1, "Pasta Carbonara")));
        when(recipeService.saveRecipe(any(Recipe.class)))
            .thenReturn(updatedRecipe);

        // Act & Assert
        mockMvc.perform(put("/recipes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRecipe)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Pasta Bolognese")));

        verify(recipeService, times(1)).getRecipeById(1);
        verify(recipeService, times(1)).saveRecipe(any(Recipe.class));
    }

    @Test
    void testUpdateRecipe_NotFound() throws Exception {
        // Arrange
        Recipe updatedRecipe = new Recipe(999, "Ghost Recipe");
        
        when(recipeService.getRecipeById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/recipes/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRecipe)))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(recipeService, times(1)).getRecipeById(999);
        verify(recipeService, never()).saveRecipe(any(Recipe.class));
    }

    @Test
    void testDeleteRecipe_Success() throws Exception {
        // Arrange
        when(recipeService.getRecipeById(1))
            .thenReturn(Optional.of(new Recipe(1, "Pasta Carbonara")));
        doNothing().when(recipeService).deleteRecipe(1);

        // Act & Assert
        mockMvc.perform(delete("/recipes/1"))
            .andDo(print())
            .andExpect(status().isOk());

        verify(recipeService, times(1)).getRecipeById(1);
        verify(recipeService, times(1)).deleteRecipe(1);
    }

    @Test
    void testDeleteRecipe_NotFound() throws Exception {
        // Arrange
        when(recipeService.getRecipeById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/recipes/999"))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(recipeService, times(1)).getRecipeById(999);
        verify(recipeService, never()).deleteRecipe(anyInt());
    }

    @Test
    void testGetRecipeIngredients() throws Exception {
        // Arrange
        int recipeId = 1;
        Recipe recipe = new Recipe(recipeId, "Pasta Carbonara");
        Ingredient ingredient1 = new Ingredient(1, "Pasta", false);
        Ingredient ingredient2 = new Ingredient(2, "Eggs", false);
        
        List<RecipeIngredient> recipeIngredients = Arrays.asList(
            new RecipeIngredient(recipe, ingredient1, 500.0, "g"),
            new RecipeIngredient(recipe, ingredient2, 4.0, "units")
        );
        
        when(recipeService.getRecipeIngredients(recipeId)).thenReturn(recipeIngredients);

        // Act & Assert
        mockMvc.perform(get("/recipes/1/ingredients"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].ingredient.id", is(1)))
            .andExpect(jsonPath("$[0].ingredient.name", is("Pasta")))
            .andExpect(jsonPath("$[0].quantity", is(500.0)))
            .andExpect(jsonPath("$[0].unit", is("g")))
            .andExpect(jsonPath("$[1].ingredient.id", is(2)))
            .andExpect(jsonPath("$[1].ingredient.name", is("Eggs")))
            .andExpect(jsonPath("$[1].quantity", is(4.0)))
            .andExpect(jsonPath("$[1].unit", is("units")));

        verify(recipeService, times(1)).getRecipeIngredients(recipeId);
    }

    @Test
    void testAddIngredientToRecipe() throws Exception {
        // Arrange
        int recipeId = 1;
        int ingredientId = 3;
        Recipe recipe = new Recipe(recipeId, "Pasta Carbonara");
        
        // Create request payload
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("quantity", 100.0);
        requestPayload.put("unit", "g");
        
        when(recipeService.addIngredientToRecipe(eq(recipeId), eq(ingredientId), eq(100.0), eq("g")))
            .thenReturn(recipe);

        // Act & Assert
        mockMvc.perform(post("/recipes/1/ingredients/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Pasta Carbonara")));

        verify(recipeService, times(1)).addIngredientToRecipe(recipeId, ingredientId, 100.0, "g");
    }

    @Test
    void testRemoveIngredientFromRecipe() throws Exception {
        // Arrange
        int recipeId = 1;
        int ingredientId = 3;
        Recipe recipe = new Recipe(recipeId, "Pasta Carbonara");
        
        when(recipeService.removeIngredientFromRecipe(recipeId, ingredientId))
            .thenReturn(recipe);

        // Act & Assert
        mockMvc.perform(delete("/recipes/1/ingredients/3"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Pasta Carbonara")));

        verify(recipeService, times(1)).removeIngredientFromRecipe(recipeId, ingredientId);
    }
    
    @Test
    void testGetRecipeIngredients_EmptyList() throws Exception {
        // Arrange
        int recipeId = 1;
        when(recipeService.getRecipeIngredients(recipeId)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/recipes/1/ingredients"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(recipeService, times(1)).getRecipeIngredients(recipeId);
    }
    
    @Test
    void testAddIngredientToRecipe_InvalidPayload() throws Exception {
        // Arrange
        int recipeId = 1;
        int ingredientId = 3;
        
        // Create an invalid request payload (missing unit)
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("quantity", 100.0);
        // Unit is missing

        // Act & Assert - expect a 400 Bad Request
        mockMvc.perform(post("/recipes/1/ingredients/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload)))
            .andDo(print())
            .andExpect(status().isBadRequest());  // Expecting 400 Bad Request
            
        // Verify the service was never called
        verify(recipeService, never()).addIngredientToRecipe(anyInt(), anyInt(), anyDouble(), anyString());
    }
}