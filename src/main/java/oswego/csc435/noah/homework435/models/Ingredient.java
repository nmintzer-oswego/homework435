package oswego.csc435.noah.homework435.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.*;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String name;
    private boolean dangerous;

    @JsonIgnore
    @OneToMany(mappedBy = "ingredient")
    private Set<RecipeIngredient> recipeIngredients = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;


    protected Ingredient() {}

    public Ingredient(String name, boolean dangerous) {
        this.name = name;
        this.dangerous = dangerous;
    }

    public Ingredient(int id, String name, boolean dangerous) {
        this.id = id;
        this.name = name;
        this.dangerous = dangerous;
    }

        public Ingredient(String name, boolean dangerous, User user) {
            this.name = name;
            this.dangerous = dangerous;
            this.user = user;
        }
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
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

    public boolean getDangerous() {
        return dangerous;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDangerous(boolean dangerous) {
        this.dangerous = dangerous;
    }
    
    public Set<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(Set<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }
    
    @Override
    public String toString() {
      return String.format(
          "Ingredient[id=%d, name='%s', dangerous='%s']",
          id, name, dangerous);
    }
}