package com.cookbook.app.recipe_manager.repositories;

import com.cookbook.app.recipe_manager.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository <Recipe, Integer> {

}
