package oswego.csc435.noah.homework435.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import oswego.csc435.noah.homework435.models.Ingredient;
import oswego.csc435.noah.homework435.services.IngredientService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
    
    private final IngredientService ingredientService;
    
    @Autowired
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }
    
    @GetMapping
    public List<Ingredient> getAllIngredients(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            return ingredientService.getAllIngredients(principal);
        }
        return ingredientService.getAllIngredients();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable int id, 
                                                       @AuthenticationPrincipal OAuth2User principal) {
        Optional<Ingredient> ingredient;
        if (principal != null) {
            ingredient = ingredientService.getIngredientById(id, principal);
        } else {
            ingredient = ingredientService.getIngredientById(id);
        }
        
        return ingredient
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ingredient createIngredient(@RequestBody Ingredient ingredient, 
                                      @AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            return ingredientService.saveIngredient(ingredient, principal);
        }
        return ingredientService.saveIngredient(ingredient);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable int id, 
                                                     @RequestBody Ingredient ingredient,
                                                     @AuthenticationPrincipal OAuth2User principal) {
        Optional<Ingredient> existingIngredient;
        if (principal != null) {
            existingIngredient = ingredientService.getIngredientById(id, principal);
        } else {
            existingIngredient = ingredientService.getIngredientById(id);
        }
        
        return existingIngredient
                .map(existing -> {
                    ingredient.setId(id);
                    Ingredient savedIngredient;
                    if (principal != null) {
                        savedIngredient = ingredientService.saveIngredient(ingredient, principal);
                    } else {
                        savedIngredient = ingredientService.saveIngredient(ingredient);
                    }
                    return ResponseEntity.ok(savedIngredient);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable int id,
                                               @AuthenticationPrincipal OAuth2User principal) {
        Optional<Ingredient> existingIngredient;
        if (principal != null) {
            existingIngredient = ingredientService.getIngredientById(id, principal);
        } else {
            existingIngredient = ingredientService.getIngredientById(id);
        }
        
        if (existingIngredient.isPresent()) {
            ingredientService.deleteIngredient(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}