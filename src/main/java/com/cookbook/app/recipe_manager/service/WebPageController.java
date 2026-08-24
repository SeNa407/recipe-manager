package com.cookbook.app.recipe_manager.service;


import com.cookbook.app.recipe_manager.repositories.RecipeRepository;
import com.cookbook.app.recipe_manager.models.Modifications;
import com.cookbook.app.recipe_manager.models.Recipe;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/cookmanager")
public class WebPageController {

   private final String uri = "https://www.gutekueche.at/kirschkuchen-rezept-9938";
    // private final String uri =  "https://www.chefkoch.de/rezepte/4307951716383426/Baiser-Stachelbeersahne-Kuchen.html";
   //private final String uri = https://www.chefkoch.de/rezepte/1326001237450819/Kaesefondue-ohne-Alkohol.html


    private WebPageService webPageService;
    private ScraperService scraperService;
    private RecipeRepository recipeRepository;

    @Autowired
    public WebPageController(WebPageService webPageService, ScraperService scraperService, RecipeRepository recipeRepository) {
        this.webPageService = webPageService;
        this.scraperService = scraperService;
        this.recipeRepository = recipeRepository;
    }


    @GetMapping("/getPage")
    public String showPage(String url) {
        String htmlContent = webPageService.getPage(uri);
        return htmlContent;
    }

    @GetMapping("/import")     //PostMapping
    public String importRecipe() {

        try {
            Recipe recipe = scraperService.scrapeRecipeFromUrl(uri);
            return recipe.toJson();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @PostMapping("/importNewRecipe")
    public ResponseEntity<String> importNewRecipe(@RequestParam String url) {
        try {
            Recipe recipe = scraperService.scrapeRecipeFromUrl(url);
            Modifications initialMod = new Modifications();
            initialMod.setRecipe(recipe);
            recipe.setMyModification(initialMod);

            Recipe savedRecipe = (Recipe) recipeRepository.save(recipe);
            // 4. Return HTTP 201 Created along with the fresh saved database record
            return ResponseEntity.status(HttpStatus.CREATED).body("Successfiley loaded and saved !");
            //return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
        } catch (IllegalArgumentException e) {
            // Catches unsupported websites or missing metadata structures
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteRecipe/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id) {

        if(!recipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }
        recipeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping ("/getRecipe/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable long id) {
            Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
       return ResponseEntity.status(HttpStatus.OK).body(recipe);
    }

}
