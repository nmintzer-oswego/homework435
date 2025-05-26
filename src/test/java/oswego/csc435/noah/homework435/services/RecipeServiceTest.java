package oswego.csc435.noah.homework435.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import oswego.csc435.noah.homework435.models.Ingredient;
import oswego.csc435.noah.homework435.models.Recipe;
import oswego.csc435.noah.homework435.models.RecipeIngredient;
import oswego.csc435.noah.homework435.repositories.IngredientRepository;
import oswego.csc435.noah.homework435.repositories.RecipeIngredientRepository;
import oswego.csc435.noah.homework435.repositories.RecipeRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;
    
    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;
    
    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRecipes() {
        List<Recipe> mockRecipes = Arrays.asList(
            new Recipe(1, "Pasta"),
            new Recipe(2, "Pizza")
        );
        when(recipeRepository.findAll()).thenReturn(mockRecipes);

        List<Recipe> recipes = recipeService.getAllRecipes();

        assertEquals(2, recipes.size());
        assertEquals("Pasta", recipes.get(0).getName());
        assertEquals("Pizza", recipes.get(1).getName());
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    void testGetRecipeById() {
        Recipe mockRecipe = new Recipe(1, "Pasta");
        when(recipeRepository.findById(1)).thenReturn(Optional.of(mockRecipe));
        when(recipeRepository.findById(2)).thenReturn(Optional.empty());

        Optional<Recipe> foundRecipe = recipeService.getRecipeById(1);
        assertTrue(foundRecipe.isPresent());
        assertEquals("Pasta", foundRecipe.get().getName());
        
        Optional<Recipe> notFoundRecipe = recipeService.getRecipeById(2);
        assertFalse(notFoundRecipe.isPresent());
        
        verify(recipeRepository, times(1)).findById(1);
        verify(recipeRepository, times(1)).findById(2);
    }

    @Test
    void testSaveRecipe() {
        Recipe recipeToSave = new Recipe("Lasagna");
        Recipe savedRecipe = new Recipe(3, "Lasagna");
        when(recipeRepository.save(recipeToSave)).thenReturn(savedRecipe);

        Recipe result = recipeService.saveRecipe(recipeToSave);

        assertEquals(3, result.getId());
        assertEquals("Lasagna", result.getName());
        verify(recipeRepository, times(1)).save(recipeToSave);
    }

    @Test
    void testDeleteRecipe() {
        int recipeId = 1;
        
        recipeService.deleteRecipe(recipeId);
        
        verify(recipeRepository, times(1)).deleteById(recipeId);
    }

    @Test
    void testAddIngredientToRecipe() {
        int recipeId = 1;
        int ingredientId = 2;
        double quantity = 2.5;
        String unit = "cups";
        
        Recipe mockRecipe = new Recipe(recipeId, "Pasta");
        Ingredient mockIngredient = new Ingredient(ingredientId, "Flour", false);
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(mockIngredient));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(mockRecipe);
        
        Recipe result = recipeService.addIngredientToRecipe(recipeId, ingredientId, quantity, unit);
        
        assertEquals(recipeId, result.getId());
        assertEquals("Pasta", result.getName());
        
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(ingredientRepository, times(1)).findById(ingredientId);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
        
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(recipeCaptor.capture());
        Recipe capturedRecipe = recipeCaptor.getValue();
        
        assertTrue(capturedRecipe.getRecipeIngredients().stream()
            .anyMatch(ri -> ri.getIngredient().getId() == ingredientId 
                      && ri.getQuantity() == quantity 
                      && ri.getUnit().equals(unit)));
    }

    @Test
    void testRemoveIngredientFromRecipe() {
        int recipeId = 1;
        int ingredientId = 2;
        
        Recipe mockRecipe = new Recipe(recipeId, "Pasta");
        Ingredient mockIngredient = new Ingredient(ingredientId, "Flour", false);
        
        RecipeIngredient recipeIngredient = new RecipeIngredient(mockRecipe, mockIngredient, 2.5, "cups");
        Set<RecipeIngredient> ingredients = new HashSet<>();
        ingredients.add(recipeIngredient);
        mockRecipe.setRecipeIngredients(ingredients);
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(mockRecipe);
        
        Recipe result = recipeService.removeIngredientFromRecipe(recipeId, ingredientId);
        
        assertEquals(recipeId, result.getId());
        
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
        
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(recipeCaptor.capture());
        Recipe capturedRecipe = recipeCaptor.getValue();
        
        assertFalse(capturedRecipe.getRecipeIngredients().stream()
            .anyMatch(ri -> ri.getIngredient().getId() == ingredientId));
    }

    @Test
    void testGetRecipeIngredients() {
        int recipeId = 1;
        List<RecipeIngredient> mockIngredients = Arrays.asList(
            new RecipeIngredient(new Recipe(recipeId, "Pasta"), new Ingredient(2, "Flour", false), 2.5, "cups"),
            new RecipeIngredient(new Recipe(recipeId, "Pasta"), new Ingredient(3, "Salt", false), 1.0, "tsp")
        );
        
        when(recipeIngredientRepository.findByRecipeId(recipeId)).thenReturn(mockIngredients);
        
        List<RecipeIngredient> result = recipeService.getRecipeIngredients(recipeId);
        
        assertEquals(2, result.size());
        verify(recipeIngredientRepository, times(1)).findByRecipeId(recipeId);
    }
    
    @Test
    void testAddIngredientToRecipe_RecipeNotFound() {
        int recipeId = 1;
        int ingredientId = 2;
        double quantity = 2.5;
        String unit = "cups";
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            recipeService.addIngredientToRecipe(recipeId, ingredientId, quantity, unit);
        });
        
        assertEquals("Recipe not found", exception.getMessage());
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(ingredientRepository, never()).findById(anyInt());
        verify(recipeRepository, never()).save(any(Recipe.class));
    }
    
    @Test
    void testAddIngredientToRecipe_IngredientNotFound() {
        int recipeId = 1;
        int ingredientId = 2;
        double quantity = 2.5;
        String unit = "cups";
        
        Recipe mockRecipe = new Recipe(recipeId, "Pasta");
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            recipeService.addIngredientToRecipe(recipeId, ingredientId, quantity, unit);
        });
        
        assertEquals("Ingredient not found", exception.getMessage());
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(ingredientRepository, times(1)).findById(ingredientId);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }
    
    @Test
    void testRemoveIngredientFromRecipe_RecipeNotFound() {
        int recipeId = 999;
        int ingredientId = 1;
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            recipeService.removeIngredientFromRecipe(recipeId, ingredientId);
        });
        
        assertEquals("Recipe not found", exception.getMessage());
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void testRemoveIngredientFromRecipe_IngredientNotInRecipe() {
        int recipeId = 1;
        int ingredientId = 999;
        
        Recipe mockRecipe = new Recipe(recipeId, "Pasta");
        Set<RecipeIngredient> ingredients = new HashSet<>();
        ingredients.add(new RecipeIngredient(mockRecipe, new Ingredient(2, "Flour", false), 2.5, "cups"));
        mockRecipe.setRecipeIngredients(ingredients);
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(mockRecipe);
        
        Recipe result = recipeService.removeIngredientFromRecipe(recipeId, ingredientId);
        
        assertEquals(recipeId, result.getId());
        assertEquals(1, result.getRecipeIngredients().size());
        
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void testGetRecipeIngredients_EmptyList() {
        int recipeId = 1;
        when(recipeIngredientRepository.findByRecipeId(recipeId)).thenReturn(List.of());
        
        List<RecipeIngredient> result = recipeService.getRecipeIngredients(recipeId);
        
        assertTrue(result.isEmpty());
        verify(recipeIngredientRepository, times(1)).findByRecipeId(recipeId);
    }
}