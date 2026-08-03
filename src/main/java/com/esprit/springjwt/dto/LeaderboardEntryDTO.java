package com.esprit.springjwt.dto;

public class LeaderboardEntryDTO {
    private Long userId;
    private String fullName;
    private String image;
    private int totalXp;
    private String rank;

    public LeaderboardEntryDTO(Long userId, String fullName, String image, int totalXp, String rank) {
        this.userId = userId;
        this.fullName = fullName;
        this.image = image;
        this.totalXp = totalXp;
        this.rank = rank;
    }

    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getImage() { return image; }
    public int getTotalXp() { return totalXp; }
    public String getRank() { return rank; }
}
