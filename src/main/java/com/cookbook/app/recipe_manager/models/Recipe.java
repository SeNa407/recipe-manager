package com.cookbook.app.recipe_manager.models;


import jakarta.persistence.*;
import lombok.*;
import tools.jackson.databind.ObjectMapper;

@Data
@RequiredArgsConstructor
@Entity
@Table(name="recipe")
public class Recipe {

   @Id
   @GeneratedValue(strategy= GenerationType.IDENTITY)
   private Long id;

   @Column(name="titel")
    private String title;

    @Column(name="category")
    private String caregory;
    @Column(name="source_url", length = 500)
    private String sourceUrl;
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    @Column(name="ingredients", columnDefinition = "TEXT")
    private String ingredients;
    @Column(name="instructions", columnDefinition = "TEXT")
    private String instructions; //Steps

    //private String imageUrl;
    @OneToOne(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Modifications myModification;

    //private String comments;
    //private String modification;

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
