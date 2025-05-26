package oswego.csc435.noah.homework435.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import oswego.csc435.noah.homework435.models.Ingredient;
import oswego.csc435.noah.homework435.repositories.IngredientRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllIngredients() {
        List<Ingredient> mockIngredients = Arrays.asList(
            new Ingredient(1, "Salt", false),
            new Ingredient(2, "Sugar", false)
                );
        when(ingredientRepository.findAll()).thenReturn(mockIngredients);

        List<Ingredient> ingredients = ingredientService.getAllIngredients();

        assertEquals(2, ingredients.size());
        verify(ingredientRepository, times(1)).findAll();
    }

    @Test
    void testGetAllIngredientsReturnsEmptyList() {
        when(ingredientRepository.findAll()).thenReturn(List.of());
        
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        
        assertTrue(ingredients.isEmpty());
        verify(ingredientRepository, times(1)).findAll();
    }

    @Test
    void testGetIngredientById() {
        Ingredient mockIngredient = new Ingredient(1, "Salt", false);
        when(ingredientRepository.findById(1)).thenReturn(Optional.of(mockIngredient));

        Optional<Ingredient> ingredient = ingredientService.getIngredientById(1);

        assertTrue(ingredient.isPresent());
        assertEquals("Salt", ingredient.get().getName());
        verify(ingredientRepository, times(1)).findById(1);
    }
    
    @Test
    void testGetIngredientByIdNotFound() {
        when(ingredientRepository.findById(99)).thenReturn(Optional.empty());
        
        Optional<Ingredient> result = ingredientService.getIngredientById(99);
        
        assertFalse(result.isPresent());
        verify(ingredientRepository, times(1)).findById(99);
    }

    @Test
    void testSaveIngredient() {
        Ingredient mockIngredient = new Ingredient(1, "Salt", false);
        when(ingredientRepository.save(mockIngredient)).thenReturn(mockIngredient);

        Ingredient savedIngredient = ingredientService.saveIngredient(mockIngredient);

        assertNotNull(savedIngredient);
        assertEquals("Salt", savedIngredient.getName());
        verify(ingredientRepository, times(1)).save(mockIngredient);
    }
    
    @Test
    void testSaveIngredientVerifyProperties() {
        Ingredient inputIngredient = new Ingredient("Pepper", true);
        Ingredient savedIngredient = new Ingredient(3, "Pepper", true);
        
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(savedIngredient);
        
        ingredientService.saveIngredient(inputIngredient);
        
        ArgumentCaptor<Ingredient> ingredientCaptor = ArgumentCaptor.forClass(Ingredient.class);
        verify(ingredientRepository).save(ingredientCaptor.capture());
        
        Ingredient capturedIngredient = ingredientCaptor.getValue();
        assertEquals("Pepper", capturedIngredient.getName());
        assertTrue(capturedIngredient.getDangerous());
    }
    
    @Test
    void testSaveIngredientWithNullName() {
        Ingredient inputIngredient = new Ingredient(null, false);
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(inputIngredient);
        
        Ingredient result = ingredientService.saveIngredient(inputIngredient);
        
        assertNull(result.getName());
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void testDeleteIngredient() {
        int ingredientId = 1;

        ingredientService.deleteIngredient(ingredientId);

        verify(ingredientRepository, times(1)).deleteById(ingredientId);
    }
    
    @Test
    void testDeleteIngredientWithInvalidId() {
        int nonExistentId = 999;
        
        doNothing().when(ingredientRepository).deleteById(nonExistentId);
        
        ingredientService.deleteIngredient(nonExistentId);
        
        verify(ingredientRepository, times(1)).deleteById(nonExistentId);
    }
    
    @Test
    void testRepositoryExceptionHandling() {
        when(ingredientRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));
        
        assertThrows(RuntimeException.class, () -> ingredientService.getAllIngredients());
    }
    
    @Test
    void testGetIngredientByIdWithZeroId() {
        when(ingredientRepository.findById(0)).thenReturn(Optional.empty());
        
        Optional<Ingredient> result = ingredientService.getIngredientById(0);
        
        assertFalse(result.isPresent());
        verify(ingredientRepository, times(1)).findById(0);
    }

    @Test
    void testGetIngredientByIdWithNegativeId() {
        when(ingredientRepository.findById(-1)).thenReturn(Optional.empty());
        
        Optional<Ingredient> result = ingredientService.getIngredientById(-1);
        
        assertFalse(result.isPresent());
        verify(ingredientRepository, times(1)).findById(-1);
    }

    @Test
    void testGetIngredientByIdWithMaxIntegerId() {
        when(ingredientRepository.findById(Integer.MAX_VALUE)).thenReturn(Optional.empty());
        
        Optional<Ingredient> result = ingredientService.getIngredientById(Integer.MAX_VALUE);
        
        assertFalse(result.isPresent());
        verify(ingredientRepository, times(1)).findById(Integer.MAX_VALUE);
    }

    @Test
    void testSaveIngredientWithEmptyName() {
        Ingredient inputIngredient = new Ingredient("", false);
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(inputIngredient);
        
        Ingredient result = ingredientService.saveIngredient(inputIngredient);
        
        assertEquals("", result.getName());
        assertFalse(result.getDangerous());
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void testSaveIngredientWithLongName() {
        String longName = "a".repeat(1000);
        Ingredient inputIngredient = new Ingredient(longName, false);
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(inputIngredient);
        
        Ingredient result = ingredientService.saveIngredient(inputIngredient);
        
        assertEquals(longName, result.getName());
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void testRepositoryModifiesDataDuringSave() {
        Ingredient inputIngredient = new Ingredient("salt", false);
        Ingredient modifiedIngredient = new Ingredient(1, "SALT", false);
        
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(modifiedIngredient);
        
        Ingredient result = ingredientService.saveIngredient(inputIngredient);
        
        assertEquals("SALT", result.getName());
        assertEquals(1, result.getId());
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void testDeleteIngredientWithMaxValue() {
        int extremeId = Integer.MAX_VALUE;
        
        ingredientService.deleteIngredient(extremeId);
        
        verify(ingredientRepository, times(1)).deleteById(extremeId);
    }

    @Test
    void testGetAllIngredientsWithMultipleIngredients() {
        List<Ingredient> mockIngredients = Arrays.asList(
            new Ingredient(1, "Salt", false),
            new Ingredient(2, "Sugar", false),
            new Ingredient(3, "Flour", false),
            new Ingredient(4, "Pepper", false),
            new Ingredient(5, "Cyanide", true)
        );
        when(ingredientRepository.findAll()).thenReturn(mockIngredients);

        List<Ingredient> ingredients = ingredientService.getAllIngredients();

        assertEquals(5, ingredients.size());
        assertTrue(ingredients.stream().anyMatch(i -> i.getDangerous()));
        verify(ingredientRepository, times(1)).findAll();
    }
}