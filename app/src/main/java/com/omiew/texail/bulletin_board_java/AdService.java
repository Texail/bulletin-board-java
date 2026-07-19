package com.omiew.texail.bulletin_board_java;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdService {
    private final AdRepository adRepository;
    private final UserService userService;

    public AdService(AdRepository adRepository, UserService userService) {
        this.adRepository = adRepository;
        this.userService = userService;
    }

    public Ad createAd(String title, String description, String price, User author) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (price == null || price.trim().isEmpty()) {
            throw new IllegalArgumentException("Price cannot be empty");
        }
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        Ad ad = new Ad(title, description, price, author);
        ad.setStatus(AdStatus.ACTIVE);

        return adRepository.save(ad);
    }

    public List<Ad> getVisibleAds() {
        return adRepository.findByStatus(AdStatus.ACTIVE);
    }

    public Page<Ad> searchAds(AdSearchCriteria criteria, Pageable pageable) {
        Specification<Ad> spec = (root, query, cb) -> cb.conjunction();

        // Фильтр по ключевому слову (Title или Description)
        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            String keyword = "%" + criteria.getKeyword().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), keyword),
                    cb.like(cb.lower(root.get("description")), keyword)
            ));
        }

        // Фильтр по автору
        if (criteria.getAuthorId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("author").get("id"), criteria.getAuthorId()));
        }

        // Фильтр по статусу
        if (criteria.getStatus() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), criteria.getStatus()));
        } else {
            // По умолчанию показываем только активные объявления (для обычных пользователей)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), AdStatus.ACTIVE));
        }

        // Фильтр по дате "от"
        if (criteria.getDateFrom() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("publicationDate"), criteria.getDateFrom()));
        }

        // Фильтр по дате "до"
        if (criteria.getDateTo() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("publicationDate"), criteria.getDateTo()));
        }

        return adRepository.findAll(spec, pageable);
    }

    public List<Ad> getUserAds(User user) {
        return adRepository.findByAuthor(user);
    }

    @Transactional
    public Ad toggleAdStatus(Long adId, User currentUser) {
        Ad ad = adRepository.findById(adId).orElseThrow(() -> new RuntimeException("Ad not found"));

        checkAdOwnership(ad, currentUser);
        userService.ensureUserNotBlocked(currentUser);

        if (ad.getStatus() == AdStatus.ACTIVE) {
            ad.setStatus(AdStatus.INACTIVE);
        } else if (ad.getStatus() == AdStatus.INACTIVE){
            ad.setStatus(AdStatus.ACTIVE);
        } else {
            throw new RuntimeException("The ad has been blocked by the administrator.");
        }
        return adRepository.save(ad);
    }

    public Ad getAdById(Long id) {
        return adRepository.findById(id).orElseThrow(() -> new RuntimeException("Ad not found"));
    }

    @Transactional
    public Ad updateAd(Long adId, String newTitle, String newDescription, String newPrice, User currentUser) {
        Ad ad = getAdById(adId);

        ensureAdNotBlocked(ad);
        checkAdOwnership(ad, currentUser);
        userService.ensureUserNotBlocked(currentUser);

        if (newTitle != null && !newTitle.trim().isEmpty()) {
            ad.setTitle(newTitle);
        }
        if (newDescription != null && !newDescription.trim().isEmpty()) {
            ad.setDescription(newDescription);
        }
        if (newPrice != null && !newPrice.trim().isEmpty()) {
            ad.setPrice(newPrice);
        }

        return adRepository.save(ad);
    }

    public void checkAdOwnership(Ad ad, User currentUser) {
        if (!ad.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only edit your ads.");
        }
    }
    
    public void ensureAdNotBlocked(Ad ad) {
        if (ad.getStatus() == AdStatus.BANNED) {
            throw new RuntimeException("Cannot edit an ad blocked by the administrator.");
        }
    }
}
