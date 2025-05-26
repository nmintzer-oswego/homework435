package oswego.csc435.noah.homework435.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import oswego.csc435.noah.homework435.models.Ingredient;
import oswego.csc435.noah.homework435.models.User;

import java.util.List;
import java.util.Optional;


@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    List<Ingredient> findByUser(User user);
    Optional<Ingredient> findByIdAndUser(int id, User user);
}

