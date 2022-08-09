package recipes;

import recipes.buisnessLayer.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import javax.validation.Valid;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication

public class RecipesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipesApplication.class, args);
    }

    @RestController
    @Validated
    public static class recipesController {
        @Autowired
        RecipeService recipeService;


        @PostMapping("/api/recipe/new")
        public Map<String, Long> recipePost(@Valid @RequestBody Recipe recipe) {
            long newId = recipeService.saveRecipe(recipe);
            Map<String, Long> returnId = new HashMap<String, Long>(1) {
                {
                    put("id", newId);
                }
            };

            return returnId;
        }

        @GetMapping("/api/recipe/{id}")
        public Map<String, Object> recipeGet(@PathVariable Long recipeId) {
            try {
                if (recipeService.findRecipeById(recipeId).isPresent()) {

                    Recipe recipeFound = recipeService.findRecipeById(recipeId).get();
                    Map<String, Object> recipeToTurn = new LinkedHashMap<>();

                    recipeToTurn.put("name", recipeFound.getName());
                    recipeToTurn.put("description", recipeFound.getDirections());
                    recipeToTurn.put("ingredients", recipeFound.getIngredients());
                    recipeToTurn.put("directions", recipeFound.getDirections());

                    return recipeToTurn;

                } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } catch (Exception exception) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }

        @DeleteMapping("api/recipe/{id}")
        public ResponseEntity<?> recipeDel(@Valid @PathVariable Long recipeId) {
            if (recipeService.findRecipeById(recipeId).isPresent()) {
                recipeService.deleteRecipe(recipeId);
                return ResponseEntity.noContent().build();
            } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }


        @ResponseStatus(code = HttpStatus.NOT_FOUND)
        class RecipeNotFoundException extends RuntimeException {
            public RecipeNotFoundException(String cause) {
                super(cause);
            }
        }
    }
}


