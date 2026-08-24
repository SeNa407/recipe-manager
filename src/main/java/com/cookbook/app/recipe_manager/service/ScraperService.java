package com.cookbook.app.recipe_manager.service;

import com.cookbook.app.recipe_manager.models.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class ScraperService {
//https://www.linkedin.com/pulse/scraping-recipe-websites-ben-awad
    //GuteKueche.at HTML, including title, ingredients, and instructions, are stored within a hidden application/ld+json script tag.
    // A Spring Boot application can use Jsoup to isolate this script tag and Jackson to parse the JSON content

        private final ObjectMapper objectMapper = new ObjectMapper();

        public Recipe scrapeRecipeFromUrl(String url) throws IOException {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(10000)
                    .get();

            // 1. Locate the hidden JSON-LD script tag
            Element scriptTag = doc.select("script[type=application/ld+json]").first();
            if (scriptTag == null) {
                throw new IllegalArgumentException("No JSON-LD metadata found on this page.");
            }

            String rawJson = scriptTag.html();
            JsonNode rootNode = objectMapper.readTree(rawJson);
            JsonNode recipeNode = null;

            // 2. Find the object inside the array that has "@type": "Recipe"
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    if (node.has("@type") && "Recipe".equals(node.get("@type").asText())) {
                        recipeNode = node;
                        break;
                    }
                }
            } else if (rootNode.isObject() && rootNode.has("@type") && "Recipe".equals(rootNode.get("@type").asText())) {
                recipeNode = rootNode;
            } else if (rootNode.has("@graph")) { // Chefkoch sometimes nests everything inside a @graph array
                JsonNode graphNode = rootNode.get("@graph");
                if (graphNode.isArray()) {
                    for (JsonNode node : graphNode) {
                        if (node.has("@type") && "Recipe".equals(node.get("@type").asText())) {
                            recipeNode = node;
                            break;
                        }
                    }
                }
            }


            if (recipeNode == null) {
                throw new IllegalArgumentException("No Recipe structural details found in metadata.");
            }

            // 3. Initialize your Database Entity
            Recipe recipe = new Recipe();
            recipe.setSourceUrl(url);

            // 4. Extract Title
            if (recipeNode.has("name")) {
                recipe.setTitle(recipeNode.get("name").asText());
            }
            // ExtractCategory
            if (recipeNode.has("recipeCategory")) {
                recipe.setCaregory(recipeNode.get("recipeCategory").asText());
            }

            // 5. Extract Ingredients Array
//            List<String> ingredientsList = new ArrayList<>();
//            if (recipeNode.has("recipeIngredient") && recipeNode.get("recipeIngredient").isArray()) {
//                for (JsonNode ingredient : recipeNode.get("recipeIngredient")) {
//                    ingredientsList.add(ingredient.asText());
//                }
//            }
//            // Save the list into your entity mapping
//            recipe.setIngredients(ingredientsList);

            // 1. Collect the ingredients into a temporary Java list
            List<String> ingredientsList = new ArrayList<>();
            if (recipeNode.has("recipeIngredient") && recipeNode.get("recipeIngredient").isArray()) {
                for (JsonNode ingredient : recipeNode.get("recipeIngredient")) {
                    ingredientsList.add(ingredient.asText());
                }
            }
// 2. Convert the list into a single String separated by newlines (\n)
            String flatIngredients = String.join("\n", ingredientsList);
            recipe.setIngredients(flatIngredients);


            // 6. Extract Preparation Instructions
            List<String> instructionsList = new ArrayList<>();
            if (recipeNode.has("recipeInstructions")) {
                JsonNode instructionsNode = recipeNode.get("recipeInstructions");

                if (instructionsNode.isArray()) {
                    for (JsonNode stepNode : instructionsNode) {
                        // Check if instructions are simple Text strings or Nested Objects (HowToStep)
                        if (stepNode.isObject() && stepNode.has("text")) {
                            instructionsList.add(stepNode.get("text").asText());
                        }

                        // Check if it's an alternate sub-object structure {"@type": "HowToSection", "itemListElement": [...]}
                        else if (stepNode.isObject() && stepNode.has("itemListElement")) {
                            for (JsonNode subStep : stepNode.get("itemListElement")) {
                                if (subStep.has("text")) {
                                    instructionsList.add(subStep.get("text").asText());
                                }
                            }
                        }


                        else {
                            instructionsList.add(stepNode.asText());
                        }
                    }
                } else {
                    // In case it's a single massive text block instead of an array
                    instructionsList.add(instructionsNode.asText());
                }
            }
            // 2. Convert the list into a single String separated by newlines (\n)
            String flatInstructions = String.join("\n", instructionsList);

            recipe.setInstructions(flatInstructions);
            //recipe.setCaregory("Deserts");

            //recipe.setId(1L);

            return recipe;
        }

}



