package org.cyberrealm.tech.bazario.backend.service.impl;

import jakarta.persistence.criteria.Predicate;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.EmailSender;
import org.cyberrealm.tech.bazario.backend.service.EmailTemplateBuilder;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutomaticChangeAdServiceImpl {
    public static final long UP_TO_MAX = 2L;
    private final EmailSender emailSender;
    private final EmailTemplateBuilder templateBuilder;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    @Value("${ad.capacity.disable}")
    private int capacityDisable;
    @Value("${ad.deadline.active}")
    private int deadlineActive;
    @Value("${ad.deadline.disable}")
    private int deadlineDisable;
    @Value("${ad.capacity.reserve}")
    private long reserveCapacity;
    @Value("${user.test.email}")
    private String testEmail;

    @Scheduled(cron = Scheduled.CRON_DISABLED)
    @Async
    protected void sendMessage() {
        var deadlineActiveDate = LocalDate.now().minusMonths(1).withDayOfMonth(deadlineActive);
        var deadlineDisableDate = deadlineActiveDate.minusDays(deadlineDisable);

        Specification<Ad> spec = (root, query, cb) -> {
            Predicate activeDeadline = cb.and(
                    cb.lessThan(root.get("publicationDate"), deadlineActiveDate),
                    cb.equal(root.get("status"), AdStatus.ACTIVE),
                    cb.greaterThan(root.get("id"), reserveCapacity)
            );
            Predicate disableDeadline = cb.and(
                    cb.lessThan(root.get("publicationDate"), deadlineDisableDate),
                    cb.equal(root.get("status"), AdStatus.DISABLE),
                    cb.greaterThan(root.get("id"), reserveCapacity)
            );
            return cb.or(activeDeadline, disableDeadline);
        };
        var ads = adRepository.findAll(spec).stream().collect(
                Collectors.groupingBy(ad -> ad.getUser().getId()));
        var users = userRepository.findAllById(ads.keySet());
        users.forEach(user -> {
            emailSender.sendEmail(testEmail, "Change ad status",
                    templateBuilder.buildChangeStatusEmail(ads.get(user.getId()),
                            capacityDisable));
        });
    }

    @Scheduled(cron = Scheduled.CRON_DISABLED)
    @Async
    protected void changeStatusActiveToDisable() {
        var ads = getDeadlineAds(LocalDate.now().minusMonths(1)
                .withDayOfMonth(deadlineActive), AdStatus.ACTIVE);
        var totalCountAd = getTotalCountActiveAndDisableAdsByUsers(ads);
        var deleteAds = changeStatusAndGetListDeleteAds(ads, totalCountAd);
        if (!deleteAds.isEmpty()) {
            adRepository.findImageUrlsByAdIds(deleteAds.stream().map(Ad::getId).toList())
                    .forEach(image -> imageService.deleteFile(URI.create(image)));
            deleteAds.forEach(ad -> ad.setImages(Set.of()));
        }
        adRepository.saveAll(Stream.concat(ads.stream(), deleteAds.stream()).distinct().toList());
    }

    private List<Ad> changeStatusAndGetListDeleteAds(
            List<Ad> ads, HashMap<Long, Long> totalCountAd) {
        var deleteAds = new ArrayList<Ad>();
        var userDisabledIds = new ArrayList<Long>();
        var rankDisableDateAsc = new ArrayList<Integer>();
        var countDisableAds = new HashMap<Long, Integer>();
        LocalDate now = LocalDate.now();
        for (Ad ad : ads) {
            Long userId = ad.getUser().getId();
            if (!(totalCountAd.get(userId) > (capacityDisable * UP_TO_MAX))) {
                if (totalCountAd.get(userId) > capacityDisable) {
                    userDisabledIds.add(userId);
                    rankDisableDateAsc.add(countDisableAds.merge(userId, 1, Integer::sum));
                }
                ad.setStatus(AdStatus.DISABLE);
            } else {
                deleteAds.add(ad);
            }
            ad.setPublicationDate(now);
        }
        if (!userDisabledIds.isEmpty()) {
            deleteAds.addAll(adRepository.findByStatusAndEarliestDate(
                    AdStatus.DISABLE.name(), userDisabledIds.toArray(Long[]::new),
                    rankDisableDateAsc.toArray(Integer[]::new)));
        }
        deleteAds.forEach(ad -> {
            ad.setStatus(AdStatus.DELETE);
            ad.setPublicationDate(now);
        });
        return deleteAds;
    }

    private HashMap<Long, Long> getTotalCountActiveAndDisableAdsByUsers(List<Ad> ads) {
        var activeCountAd = ads.stream().collect(Collectors.groupingBy(ad ->
                ad.getUser().getId(), Collectors.counting()));
        var disableCountAd = adRepository.countGroupByUser(AdStatus.DISABLE,
                        ads.stream().map(ad -> ad.getUser().getId()).toList())
                .stream().collect(Collectors.toMap(tuple ->
                                tuple.get("userId", Long.class),
                        tuple -> tuple.get("count", Long.class)));
        return Stream.concat(activeCountAd.entrySet().stream(),
                disableCountAd.entrySet().stream()).collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, Long::sum, HashMap::new
        ));
    }

    private List<Ad> getDeadlineAds(LocalDate deadlineDate, AdStatus status) {
        Specification<Ad> spec = (root, query, cb) ->
                cb.and(
                        cb.lessThan(root.get("publicationDate"), deadlineDate),
                        cb.equal(root.get("status"), status),
                        cb.greaterThan(root.get("id"), reserveCapacity)
                );
        return adRepository.findAll(spec);
    }

    @Scheduled(cron = Scheduled.CRON_DISABLED)
    @Async
    protected void changeStatusDisableToDelete() {
        LocalDate deadlineDate = LocalDate.now().minusMonths(1)
                .withDayOfMonth(deadlineActive).minusDays(deadlineDisable);
        var ads = getDeadlineAds(deadlineDate, AdStatus.DISABLE);
        adRepository.findImageUrlsByAdIds(ads.stream().map(Ad::getId).toList())
                .forEach(image -> imageService.deleteFile(URI.create(image)));
        ads.forEach(ad -> {
            ad.setStatus(AdStatus.DELETE);
            ad.setPublicationDate(LocalDate.now());
        });
        adRepository.saveAll(ads);
    }
}
