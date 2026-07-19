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
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(name = "hashed_password", nullable = false)
    private String hashedPassword;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role;
    @Column(nullable = false)
    private boolean blocked;

    protected User() {}

    public User(
            String firstName,
            String lastName,
            String email,
            String username,
            String password
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.hashedPassword = password;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, username=%s]", id, username);
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() { return firstName; }
    public void  setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void  setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() {
        return username;
    }
    public void setUsername(String Username) { this.username = Username; }

    public String getHashedPassword() { return  hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isBlocked() { return blocked; }
    public void setIsBlocked(boolean blocked) { this.blocked = blocked; }
}
