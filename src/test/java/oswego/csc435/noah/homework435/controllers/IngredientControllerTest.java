package oswego.csc435.noah.homework435.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import oswego.csc435.noah.homework435.models.Ingredient;
import oswego.csc435.noah.homework435.services.IngredientService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class IngredientControllerTest {

    private MockMvc mockMvc;
    private MockMvc mockMvcNoAuth;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private IngredientController ingredientController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "test-user-id");
        attributes.put("name", "Test User");
        when(oAuth2User.getAttributes()).thenReturn(attributes);
        when(oAuth2User.getAttribute("sub")).thenReturn("test-user-id");
        when(oAuth2User.getAttribute("name")).thenReturn("Test User");
        
        HandlerMethodArgumentResolver oauth2UserResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().equals(OAuth2User.class) && 
                       parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                         NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return oAuth2User;
            }
        };
        
        HandlerMethodArgumentResolver nullOauth2UserResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().equals(OAuth2User.class) && 
                       parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                         NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return null;
            }
        };
        
        mockMvc = MockMvcBuilders.standaloneSetup(ingredientController)
                .setCustomArgumentResolvers(oauth2UserResolver)
                .build();
                
        mockMvcNoAuth = MockMvcBuilders.standaloneSetup(ingredientController)
                .setCustomArgumentResolvers(nullOauth2UserResolver)
                .build();
    }

    @Test
    void testGetAllIngredientsAuthenticated() throws Exception {
        when(ingredientService.getAllIngredients(any(OAuth2User.class))).thenReturn(
            Arrays.asList(
                new Ingredient(1, "Salt", false),
                new Ingredient(2, "Pepper", false)
            )
        );

        mockMvc.perform(get("/ingredients"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("Salt")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].name", is("Pepper")));

        verify(ingredientService, times(1)).getAllIngredients(any(OAuth2User.class));
        verify(ingredientService, never()).getAllIngredients();
    }

    @Test
    void testGetAllIngredientsNonAuthenticated() throws Exception {
        when(ingredientService.getAllIngredients()).thenReturn(
            Arrays.asList(
                new Ingredient(1, "Salt", false),
                new Ingredient(2, "Pepper", false)
            )
        );

        mockMvcNoAuth.perform(get("/ingredients"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("Salt")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].name", is("Pepper")));

        verify(ingredientService, times(1)).getAllIngredients();
        verify(ingredientService, never()).getAllIngredients(any(OAuth2User.class));
    }

    @Test
    void testGetAllIngredientsEmpty() throws Exception {
        when(ingredientService.getAllIngredients()).thenReturn(Collections.emptyList());

        mockMvcNoAuth.perform(get("/ingredients"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(ingredientService, times(1)).getAllIngredients();
        verify(ingredientService, never()).getAllIngredients(any(OAuth2User.class));
    }

    @Test
    void testGetIngredientByIdAuthenticated_Success() throws Exception {
        when(ingredientService.getIngredientById(eq(1), any(OAuth2User.class)))
            .thenReturn(Optional.of(new Ingredient(1, "Salt", false)));

        mockMvc.perform(get("/ingredients/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Salt")))
            .andExpect(jsonPath("$.dangerous", is(false)));

        verify(ingredientService, times(1)).getIngredientById(eq(1), any(OAuth2User.class));
        verify(ingredientService, never()).getIngredientById(anyInt());
    }

    @Test
    void testGetIngredientByIdNonAuthenticated_Success() throws Exception {
        when(ingredientService.getIngredientById(1))
            .thenReturn(Optional.of(new Ingredient(1, "Salt", false)));

        mockMvcNoAuth.perform(get("/ingredients/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Salt")))
            .andExpect(jsonPath("$.dangerous", is(false)));

        verify(ingredientService, times(1)).getIngredientById(1);
        verify(ingredientService, never()).getIngredientById(anyInt(), any(OAuth2User.class));
    }

    @Test
    void testGetIngredientById_NotFound() throws Exception {
        when(ingredientService.getIngredientById(eq(999), any(OAuth2User.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/ingredients/999"))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(ingredientService, times(1)).getIngredientById(eq(999), any(OAuth2User.class));
    }

    @Test
    void testCreateIngredientAuthenticated() throws Exception {
        Ingredient newIngredient = new Ingredient("Garlic", false);
        Ingredient savedIngredient = new Ingredient(3, "Garlic", false);
        
        when(ingredientService.saveIngredient(any(Ingredient.class), any(OAuth2User.class))).thenReturn(savedIngredient);

        mockMvc.perform(post("/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newIngredient)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.name", is("Garlic")))
            .andExpect(jsonPath("$.dangerous", is(false)));

        verify(ingredientService, times(1)).saveIngredient(any(Ingredient.class), any(OAuth2User.class));
        verify(ingredientService, never()).saveIngredient(any(Ingredient.class));
    }

    @Test
    void testCreateIngredientNonAuthenticated() throws Exception {
        Ingredient newIngredient = new Ingredient("Garlic", false);
        Ingredient savedIngredient = new Ingredient(3, "Garlic", false);
        
        when(ingredientService.saveIngredient(any(Ingredient.class))).thenReturn(savedIngredient);

        mockMvcNoAuth.perform(post("/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newIngredient)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.name", is("Garlic")))
            .andExpect(jsonPath("$.dangerous", is(false)));

        verify(ingredientService, times(1)).saveIngredient(any(Ingredient.class));
        verify(ingredientService, never()).saveIngredient(any(Ingredient.class), any(OAuth2User.class));
    }

    @Test
    void testUpdateIngredientAuthenticated_Success() throws Exception {
        Ingredient updatedIngredient = new Ingredient(1, "Sea Salt", true);
        
        when(ingredientService.getIngredientById(eq(1), any(OAuth2User.class)))
            .thenReturn(Optional.of(new Ingredient(1, "Salt", false)));
        when(ingredientService.saveIngredient(any(Ingredient.class), any(OAuth2User.class)))
            .thenReturn(updatedIngredient);

        mockMvc.perform(put("/ingredients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedIngredient)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Sea Salt")))
            .andExpect(jsonPath("$.dangerous", is(true)));

        verify(ingredientService, times(1)).getIngredientById(eq(1), any(OAuth2User.class));
        verify(ingredientService, times(1)).saveIngredient(any(Ingredient.class), any(OAuth2User.class));
    }

    @Test
    void testUpdateIngredientNonAuthenticated_Success() throws Exception {
        Ingredient updatedIngredient = new Ingredient(1, "Sea Salt", true);
        
        when(ingredientService.getIngredientById(1))
            .thenReturn(Optional.of(new Ingredient(1, "Salt", false)));
        when(ingredientService.saveIngredient(any(Ingredient.class)))
            .thenReturn(updatedIngredient);

        mockMvcNoAuth.perform(put("/ingredients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedIngredient)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Sea Salt")))
            .andExpect(jsonPath("$.dangerous", is(true)));

        verify(ingredientService, times(1)).getIngredientById(1);
        verify(ingredientService, times(1)).saveIngredient(any(Ingredient.class));
    }

    @Test
    void testUpdateIngredient_NotFound() throws Exception {
        Ingredient updatedIngredient = new Ingredient(999, "Ghost Ingredient", true);
        
        when(ingredientService.getIngredientById(eq(999), any(OAuth2User.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/ingredients/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedIngredient)))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(ingredientService, times(1)).getIngredientById(eq(999), any(OAuth2User.class));
        verify(ingredientService, never()).saveIngredient(any(Ingredient.class), any(OAuth2User.class));
    }

    @Test
    void testDeleteIngredientAuthenticated_Success() throws Exception {
        when(ingredientService.getIngredientById(eq(1), any(OAuth2User.class)))
            .thenReturn(Optional.of(new Ingredient(1, "Salt", false)));
        doNothing().when(ingredientService).deleteIngredient(1);

        mockMvc.perform(delete("/ingredients/1"))
            .andDo(print())
            .andExpect(status().isOk());

        verify(ingredientService, times(1)).getIngredientById(eq(1), any(OAuth2User.class));
        verify(ingredientService, times(1)).deleteIngredient(1);
    }

    @Test
    void testDeleteIngredientNonAuthenticated_Success() throws Exception {
        when(ingredientService.getIngredientById(1))
            .thenReturn(Optional.of(new Ingredient(1, "Salt", false)));
        doNothing().when(ingredientService).deleteIngredient(1);

        mockMvcNoAuth.perform(delete("/ingredients/1"))
            .andDo(print())
            .andExpect(status().isOk());

        verify(ingredientService, times(1)).getIngredientById(1);
        verify(ingredientService, times(1)).deleteIngredient(1);
    }

    @Test
    void testDeleteIngredient_NotFound() throws Exception {
        when(ingredientService.getIngredientById(eq(999), any(OAuth2User.class))).thenReturn(Optional.empty());

        mockMvc.perform(delete("/ingredients/999"))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(ingredientService, times(1)).getIngredientById(eq(999), any(OAuth2User.class));
        verify(ingredientService, never()).deleteIngredient(anyInt());
    }
    
    @Test
    void testCannotAccessOtherUsersIngredient() throws Exception {
        when(ingredientService.getIngredientById(eq(1), any(OAuth2User.class)))
            .thenReturn(Optional.empty());
        
        when(ingredientService.getIngredientById(1))
            .thenReturn(Optional.of(new Ingredient(1, "Restricted Ingredient", true)));
        
        mockMvc.perform(get("/ingredients/1"))
            .andDo(print())
            .andExpect(status().isNotFound());
        
        verify(ingredientService, times(1)).getIngredientById(eq(1), any(OAuth2User.class));
    }
    
    @Test
    void testCannotUpdateOtherUsersIngredient() throws Exception {
        Ingredient updatedIngredient = new Ingredient(1, "Altered Ingredient", true);
        
        when(ingredientService.getIngredientById(eq(1), any(OAuth2User.class)))
            .thenReturn(Optional.empty());
        
        mockMvc.perform(put("/ingredients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedIngredient)))
            .andDo(print())
            .andExpect(status().isNotFound());
            
        verify(ingredientService, times(1)).getIngredientById(eq(1), any(OAuth2User.class));
        verify(ingredientService, never()).saveIngredient(any(Ingredient.class), any(OAuth2User.class));
    }
    
    @Test
    void testCannotDeleteOtherUsersIngredient() throws Exception {
        when(ingredientService.getIngredientById(eq(1), any(OAuth2User.class)))
            .thenReturn(Optional.empty());
        
        when(ingredientService.getIngredientById(1))
            .thenReturn(Optional.of(new Ingredient(1, "Another User's Ingredient", false)));
        
        mockMvc.perform(delete("/ingredients/1"))
            .andDo(print())
            .andExpect(status().isNotFound());
            
        verify(ingredientService, times(1)).getIngredientById(eq(1), any(OAuth2User.class));
        verify(ingredientService, never()).deleteIngredient(anyInt());
    }
}