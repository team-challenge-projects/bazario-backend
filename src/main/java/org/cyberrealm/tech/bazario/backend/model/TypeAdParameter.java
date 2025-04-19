package org.cyberrealm.tech.bazario.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "type_ad_parameters")
public class TypeAdParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String restrictionPattern;
    private String descriptionPattern;

    @ManyToMany(mappedBy = "adParameters")
    private Set<Category> categories;
}
