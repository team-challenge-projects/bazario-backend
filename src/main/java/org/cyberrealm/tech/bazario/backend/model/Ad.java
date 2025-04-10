package org.cyberrealm.tech.bazario.backend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.cyberrealm.tech.bazario.backend.model.enums.StatusAd;

@Entity
@Getter
@Setter
@Table(name = "ads")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title = "";
    private String description = "";
    private BigDecimal price = BigDecimal.ZERO;
    private LocalDate publicationDate = LocalDate.now();
    @Enumerated(EnumType.STRING)
    private StatusAd status = StatusAd.NEW;

    @ElementCollection
    @CollectionTable(name = "ad_images", joinColumns = @JoinColumn(name = "ad_id"))
    @Column(name = "url")
    private Set<String> images = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private Set<AdParameter> parameters = new HashSet<>();
}
