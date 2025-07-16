package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.AdCredentials;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.AdInitializer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdInitializerImpl implements AdInitializer {
    private static final int COMPARE_TO_EQUALS = 0;
    private final AdRepository repository;
    private final AdMapper mapper;

    @Override
    public void createAd(AdCredentials credentials, List<User> users, List<Category> categories,
                         List<TypeAdParameter> adTypes) {
        if (!repository.existsByTitleAndPrice(credentials.getTitle(), credentials.getPrice())) {
            var ad = mapper.toAd(credentials);
            ad.setUser(users.get(credentials.getUser()));
            ad.setCategory(categories.get(credentials.getCategory()));
            repository.save(ad);
        }
    }

    @Override
    public List<Ad> createAds(List<AdCredentials> credentials, List<User> users,
                          List<Category> categories) {
        Specification<Ad> spec = (root, query, cb) ->
                credentials.stream().map(dto -> {
                    var titlePredicate = cb.equal(root.get("title"), dto.getTitle());
                    var pricePredicate = cb.equal(root.get("price"), dto.getPrice());
                    return cb.and(titlePredicate, pricePredicate);
                }).reduce(cb::or).orElse(cb.disjunction());
        var ads = repository.findAll(spec);

        if (!ads.isEmpty()) {
            var notExistsCredentials = getNotExistsCredentials(credentials, ads);

            if (!notExistsCredentials.isEmpty()) {
                var newAds = repository.saveAll(
                        createNewAds(notExistsCredentials, users, categories));
                return Stream.of(ads, newAds).flatMap(Collection::stream).toList();
            }
        }
        return ads;
    }

    private List<Ad> createNewAds(List<AdCredentials> credentials, List<User> users,
                                  List<Category> categories) {
        return credentials.stream().map(dto -> {
            var ad = mapper.toAd(dto);
            ad.setUser(users.get(dto.getUser()));
            ad.setCategory(categories.get(dto.getCategory()));
            return ad;
        }).toList();
    }

    private List<AdCredentials> getNotExistsCredentials(List<AdCredentials> credentials,
                                                        List<Ad> ads) {
        return credentials.stream().filter(dto -> ads.stream()
                .noneMatch(ad -> ad.getTitle().equals(dto.getTitle())
                        && ad.getPrice().compareTo(dto.getPrice()) == COMPARE_TO_EQUALS))
                .toList();
    }
}
