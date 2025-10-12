package org.cyberrealm.tech.bazario.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.cyberrealm.tech.bazario.backend.dto.TypeView;

@Entity
@Getter
@Setter
@Table(name = "type_user_parameters")
public class TypeUserParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private TypeView typeView;
    private String descriptionPattern;

    @ManyToMany(mappedBy = "userParameters")
    private Set<Category> categories;
}
