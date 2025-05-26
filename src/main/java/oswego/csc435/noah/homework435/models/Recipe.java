package oswego.csc435.noah.homework435.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeIngredient> recipeIngredients = new HashSet<>();

    public Recipe() {
    }

    public Recipe(String name) {
        this.name = name;
    }

    public Recipe(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(Set<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public void addIngredient(Ingredient ingredient, double quantity, String unit) {
        RecipeIngredient recipeIngredient = new RecipeIngredient(this, ingredient, quantity, unit);
        recipeIngredients.add(recipeIngredient);
    }

    public void removeIngredient(Ingredient ingredient) {
        recipeIngredients.removeIf(ri -> ri.getIngredient().getId() == ingredient.getId());
    }
    
    @Override
    public String toString() {
        return String.format("Recipe[id=%d, name='%s']", id, name);
    }
}