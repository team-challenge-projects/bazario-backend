package org.cyberrealm.tech.bazario.backend.api.impl;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.FavoriteApiDelegate;
import org.cyberrealm.tech.bazario.backend.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteApiDelegateImpl implements FavoriteApiDelegate {
    private final FavoriteService service;

    @Override
    public ResponseEntity<Void> addFavorite(Long id) {
        service.add(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteFavorite(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Page> getFavorite(Map<String, String> filters) {
        return ResponseEntity.ok(service.getAll(filters));
    }
}
