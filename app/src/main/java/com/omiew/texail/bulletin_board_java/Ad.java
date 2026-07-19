package com.omiew.texail.bulletin_board_java;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @CreationTimestamp
    @Column(name = "publication_date", updatable = false)
    private LocalDateTime publicationDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AdStatus status;


    protected Ad() {}

    public Ad(String title, String  description, String price, User user) {
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

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public AdStatus getStatus() { return status; }
    public void setStatus(AdStatus status) { this.status = status; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = publicationDate != null ? publicationDate.format(formatter) : "N/A";

        return "Ad{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", author_id=" + (author != null ? author.getId() : "null") +
                ", publicationDate=" + formattedDate +
                ", status=" + status +
                '}';
    }
}
