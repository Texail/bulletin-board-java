package com.omiew.texail.bulletin_board_java;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nickname;

    protected User() {}

    public User(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, nickname=%s]", id, nickname);
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }
}
