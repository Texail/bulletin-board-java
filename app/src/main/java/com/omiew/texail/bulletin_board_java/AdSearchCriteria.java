package com.omiew.texail.bulletin_board_java;

import java.time.LocalDateTime;

public class AdSearchCriteria {
    private String keyword;
    private Long authorId;
    private AdStatus status;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private String sortDirection; // "NEW_TO_OLD" или "OLD_TO_NEW"

    // Getters and Setters
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public AdStatus getStatus() { return status; }
    public void setStatus(AdStatus status) { this.status = status; }

    public LocalDateTime getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDateTime dateFrom) { this.dateFrom = dateFrom; }

    public LocalDateTime getDateTo() { return dateTo; }
    public void setDateTo(LocalDateTime dateTo) { this.dateTo = dateTo; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
}