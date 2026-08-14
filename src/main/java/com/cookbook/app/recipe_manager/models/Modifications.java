package com.cookbook.app.recipe_manager.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@Entity
@Table(name = "modifications")
public class Modifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "my_ingredients", columnDefinition = "TEXT")
    private String myIngredients;
    @Column(name = "create_date")
    private LocalDate creationDate;
    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;


    //Defines the set of cascadable operations that are propagated
    //to the associated entity. The value cascade=ALL is equivalent to cascade={PERSIST, MERGE, REMOVE, REFRESH, DETACH}.
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    //TODO [Reverse Engineering] generate columns from DB
}