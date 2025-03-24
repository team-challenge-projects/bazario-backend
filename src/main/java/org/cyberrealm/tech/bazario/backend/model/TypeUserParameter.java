package org.cyberrealm.tech.bazario.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "type_user_parameters")
public class TypeUserParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String restrictionPattern;
    private String descriptionPattern;

    @OneToMany
    @JoinColumn(name = "parent_id")
    private Set<TypeUserParameter> childrenType = new HashSet<>();
}
