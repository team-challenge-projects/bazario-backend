package org.cyberrealm.tech.bazario.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ads")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(nullable = false)
    private String imageUrl;
    @Column(nullable = false)
    private BigDecimal price;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private LocalDate publicationDate;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category categories;
    @OneToMany
    private Set<TypeAdParameter> parameters = new HashSet<>();
    @Column(nullable = false)
    private boolean isActive = true;
}
