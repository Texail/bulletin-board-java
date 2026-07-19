package com.omiew.texail.bulletin_board_java;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AdRepository extends JpaRepository<Ad, Long>, JpaSpecificationExecutor<Ad> {
    List<Ad> findByStatus(AdStatus status);
    List<Ad> findByAuthor(User author);
    Optional<Ad> findById(Long id);
}
