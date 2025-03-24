package org.cyberrealm.tech.bazario.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE roles SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted=false")
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleName role;
    @Column(nullable = false)
    private boolean isDeleted = false;

    @Override
    public String getAuthority() {
        return role.name();
    }

    public enum RoleName implements GrantedAuthority{
        ROLE_ADMIN, ROLE_USER;

        @Override
        public String getAuthority() {
            return name();
        }
    }
}
