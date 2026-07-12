package com.omiew.texail.bulletin_board_java;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String title;
    private String description;

    private float price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
    @CreationTimestamp
    @Column(name = "publication_date", updatable = false)
    private LocalDateTime publicationDate;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AdStatus status = AdStatus.ACTIVE;

    protected Ad() {}

    public Ad(String title, String  description, float price, User user) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.author = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public AdStatus getStatus() { return status; }
    public void setStatus(AdStatus status) { this.status = status; }
}
