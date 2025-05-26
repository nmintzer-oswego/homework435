package oswego.csc435.noah.homework435.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import oswego.csc435.noah.homework435.models.RecipeIngredient;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Integer> {
    List<RecipeIngredient> findByRecipeId(int recipeId);
    List<RecipeIngredient> findByIngredientId(int ingredientId);
}