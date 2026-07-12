package com.omiew.texail.bulletin_board_java;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String secondName;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String nickname;
    @Column(name = "hashed_password", nullable = false)
    private String hashedPassword;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role = Role.USER;
    @Column(nullable = false)
    private boolean status = false;

    protected User() {}

    public User(
            String firstName,
            String secondName,
            String email,
            String nickname,
            String password
    ) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.nickname = nickname;
        this.hashedPassword = password;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, nickname=%s]", id, nickname);
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() { return firstName; }
    public void  setFirstName(String firstName) { this.firstName = firstName; }

    public String getSecondName() { return secondName; }
    public void  setSecondName(String secondName) { this.secondName = secondName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String Nickname) { this.nickname = Nickname; }

    public String getHashedPassword() { return  hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean getStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}
