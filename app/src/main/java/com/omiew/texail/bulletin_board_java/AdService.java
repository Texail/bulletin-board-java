package com.omiew.texail.bulletin_board_java;

import jakarta.transaction.Transactional;
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

    public Ad createAd(String title, String description, float price, User author) {
        Ad ad = new Ad(title, description, price, author);
        return adRepository.save(ad);
    }

    public List<Ad> getVisibleAds() {
        return adRepository.findByStatus(AdStatus.ACTIVE);
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
    public Ad updateAd(Long adId, String newTitle, String newDescription, Float newPrice, User currentUser) {
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
        if (newPrice != null) {
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
