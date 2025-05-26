package oswego.csc435.noah.homework435.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import oswego.csc435.noah.homework435.models.Ingredient;
import oswego.csc435.noah.homework435.models.User;
import oswego.csc435.noah.homework435.repositories.IngredientRepository;

import java.util.List;
import java.util.Optional;

/*
TRACE: For very fine details (user context checking)
DEBUG: For detailed information
INFO: For significant application events (initialization, saving, deleting)
WARN: For potential issues
ERROR: For exception conditions
*/


@Service
public class IngredientService {
    //SLF4j logger
    private static final Logger logger = LoggerFactory.getLogger(IngredientService.class);
    
    private final IngredientRepository ingredientRepository;
    private final UserService userService;
    
    @Autowired
    public IngredientService(IngredientRepository ingredientRepository, UserService userService) {
        this.ingredientRepository = ingredientRepository;
        this.userService = userService;
        logger.info("IngredientService initialized");
    }
    
    public List<Ingredient> getAllIngredients() {
        User user = getCurrentUser();
        if (user != null) {
            logger.debug("Fetching all ingredients for user: {}", user.getName());
            return ingredientRepository.findByUser(user);
        }
        logger.debug("Fetching all ingredients (no user context)");
        return ingredientRepository.findAll();
    }
    
    public List<Ingredient> getAllIngredients(OAuth2User oauth2User) {
        User user = userService.getOrCreateUser(oauth2User);
        logger.debug("Fetching all ingredients for OAuth2User: {}", user.getName());
        List<Ingredient> ingredients = ingredientRepository.findByUser(user);
        logger.info("Found {} ingredients for user {}", ingredients.size(), user.getName());
        return ingredients;
    }
    
    public Optional<Ingredient> getIngredientById(int id) {
        User user = getCurrentUser();
        if (user != null) {
            logger.debug("Fetching ingredient ID: {} for user: {}", id, user.getName());
            return ingredientRepository.findByIdAndUser(id, user);
        }
        logger.debug("Fetching ingredient ID: {} (no user context)", id);
        return ingredientRepository.findById(id);
    }
    
    public Optional<Ingredient> getIngredientById(int id, OAuth2User oauth2User) {
        User user = userService.getOrCreateUser(oauth2User);
        logger.debug("Fetching ingredient ID: {} for OAuth2User: {}", id, user.getName());
        Optional<Ingredient> ingredient = ingredientRepository.findByIdAndUser(id, user);
        if (ingredient.isPresent()) {
            logger.info("Found ingredient: {} (ID: {}) for user: {}", 
                    ingredient.get().getName(), id, user.getName());
        } else {
            logger.warn("Ingredient with ID: {} not found for user: {}", id, user.getName());
        }
        return ingredient;
    }
    
    public Ingredient saveIngredient(Ingredient ingredient) {
        User user = getCurrentUser();
        if (user != null) {
            ingredient.setUser(user);
            logger.info("Saving ingredient: {} for user: {}", ingredient.getName(), user.getName());
        } else {
            logger.info("Saving ingredient: {} (no user context)", ingredient.getName());
        }
        
        try {
            Ingredient savedIngredient = ingredientRepository.save(ingredient);
            logger.debug("Ingredient saved successfully with ID: {}", savedIngredient.getId());
            return savedIngredient;
        } catch (Exception e) {
            logger.error("Error saving ingredient: {}", ingredient.getName(), e);
            throw e;
        }
    }
    
    public Ingredient saveIngredient(Ingredient ingredient, OAuth2User oauth2User) {
        User user = userService.getOrCreateUser(oauth2User);
        ingredient.setUser(user);
        logger.info("Saving ingredient: {} for OAuth2User: {}", ingredient.getName(), user.getName());
        
        try {
            Ingredient savedIngredient = ingredientRepository.save(ingredient);
            logger.debug("Ingredient saved successfully with ID: {} for user: {}", 
                    savedIngredient.getId(), user.getName());
            return savedIngredient;
        } catch (Exception e) {
            logger.error("Error saving ingredient: {} for user: {}", 
                    ingredient.getName(), user.getName(), e);
            throw e;
        }
    }
    
    public void deleteIngredient(int id) {
        User user = getCurrentUser();
        if (user != null) {
            logger.info("Attempting to delete ingredient ID: {} for user: {}", id, user.getName());
            Optional<Ingredient> ingredient = ingredientRepository.findByIdAndUser(id, user);
            if (ingredient.isPresent()) {
                logger.info("Deleting ingredient: {} (ID: {}) for user: {}", 
                        ingredient.get().getName(), id, user.getName());
                ingredientRepository.deleteById(id);
                logger.debug("Ingredient ID: {} deleted successfully", id);
            } else {
                logger.warn("Cannot delete - Ingredient ID: {} not found for user: {}", id, user.getName());
            }
        } else {
            logger.info("Deleting ingredient ID: {} (no user context)", id);
            ingredientRepository.deleteById(id);
            logger.debug("Ingredient ID: {} deleted successfully", id);
        }
    }
    
    private User getCurrentUser() {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null && 
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof OAuth2User) {
                OAuth2User oauth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User user = userService.getOrCreateUser(oauth2User);
                logger.trace("Current user identified as: {}", user.getName());
                return user;
            }
        } catch (Exception e) {
            logger.error("Error retrieving current user", e);
        }
        logger.trace("No authenticated user found in current context");
        return null;
    }
}