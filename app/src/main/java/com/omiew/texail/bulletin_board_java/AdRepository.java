package com.omiew.texail.bulletin_board_java;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AdRepository extends CrudRepository<Ad, Long> {
    List<Ad> findByStatus(AdStatus status);
    List<Ad> findByAuthor(User author);
    Optional<Ad> findById(Long id);
}
