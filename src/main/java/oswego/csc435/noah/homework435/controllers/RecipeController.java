package oswego.csc435.noah.homework435.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import oswego.csc435.noah.homework435.models.Recipe;
import oswego.csc435.noah.homework435.models.RecipeIngredient;
import oswego.csc435.noah.homework435.services.RecipeService;

import java.util.List;
import java.util.Map;

//HTTP endpoint start
//
@RestController
@RequestMapping("/recipes")
public class RecipeController {
    
    private final RecipeService recipeService;
    
    // @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable int id) {
        return recipeService.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeService.saveRecipe(recipe);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable int id, @RequestBody Recipe recipe) {
        return recipeService.getRecipeById(id)
                .map(existingRecipe -> {
                    recipe.setId(id);
                    return ResponseEntity.ok(recipeService.saveRecipe(recipe));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int id) {
        return recipeService.getRecipeById(id)
                .map(recipe -> {
                    recipeService.deleteRecipe(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
        
    @GetMapping("/{recipeId}/ingredients")
    public List<RecipeIngredient> getRecipeIngredients(@PathVariable int recipeId) {
        return recipeService.getRecipeIngredients(recipeId);
    }

    @PostMapping("/{recipeId}/ingredients/{ingredientId}")
    public ResponseEntity<?> addIngredientToRecipe(
            @PathVariable int recipeId,
            @PathVariable int ingredientId,
            @RequestBody Map<String, Object> payload) {
        
        // Validate required fields are present
        if (payload.get("quantity") == null) {
            return ResponseEntity.badRequest().body("Quantity is required");
        }
        if (payload.get("unit") == null) {
            return ResponseEntity.badRequest().body("Unit is required");
        }
        
        try {
            double quantity = Double.parseDouble(payload.get("quantity").toString());
            String unit = payload.get("unit").toString();
            
            Recipe updatedRecipe = recipeService.addIngredientToRecipe(recipeId, ingredientId, quantity, unit);
            return ResponseEntity.ok(updatedRecipe);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Quantity must be a valid number");
        }
    }
        
    @DeleteMapping("/{recipeId}/ingredients/{ingredientId}")
    public ResponseEntity<Recipe> removeIngredientFromRecipe(
            @PathVariable int recipeId,
            @PathVariable int ingredientId) {
        
        Recipe updatedRecipe = recipeService.removeIngredientFromRecipe(recipeId, ingredientId);
        return ResponseEntity.ok(updatedRecipe);
    }
}