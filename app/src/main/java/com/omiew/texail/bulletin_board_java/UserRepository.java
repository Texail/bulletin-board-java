package com.omiew.texail.bulletin_board_java;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByNickname(String nickname);
    User findById(long id);
}
