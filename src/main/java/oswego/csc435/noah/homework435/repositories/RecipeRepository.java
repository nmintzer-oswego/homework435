package oswego.csc435.noah.homework435.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import oswego.csc435.noah.homework435.models.Recipe;


//basic CRUD
//db access
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
}