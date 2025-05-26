package oswego.csc435.noah.homework435.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oswego.csc435.noah.homework435.models.Ingredient;
import oswego.csc435.noah.homework435.models.Recipe;
import oswego.csc435.noah.homework435.models.RecipeIngredient;
import oswego.csc435.noah.homework435.repositories.IngredientRepository;
import oswego.csc435.noah.homework435.repositories.RecipeIngredientRepository;
import oswego.csc435.noah.homework435.repositories.RecipeRepository;

import java.util.List;
import java.util.Optional;

//business logic
@Service
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;
    
    public RecipeService(RecipeRepository recipeRepository, 
                         RecipeIngredientRepository recipeIngredientRepository,
                         IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
    }
    
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
    
    public Optional<Recipe> getRecipeById(int id) {
        return recipeRepository.findById(id);
    }
    
    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
    
    public void deleteRecipe(int id) {
        recipeRepository.deleteById(id);
    }
    
    public Recipe addIngredientToRecipe(int recipeId, int ingredientId, double quantity, String unit) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        
        RecipeIngredient recipeIngredient = new RecipeIngredient(recipe, ingredient, quantity, unit);
        recipe.getRecipeIngredients().add(recipeIngredient);
        
        return recipeRepository.save(recipe);
    }
    
    public Recipe removeIngredientFromRecipe(int recipeId, int ingredientId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        
        recipe.getRecipeIngredients().removeIf(ri -> ri.getIngredient().getId() == ingredientId);
        
        return recipeRepository.save(recipe);
    }
    
    public List<RecipeIngredient> getRecipeIngredients(int recipeId) {
        return recipeIngredientRepository.findByRecipeId(recipeId);
    }
}