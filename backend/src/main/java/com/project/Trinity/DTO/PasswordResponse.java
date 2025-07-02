package com.project.Trinity.DTO;

import com.project.Trinity.Entity.Password;
import lombok.Data;

@Data
public class PasswordResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String username;
    private String description;
    private String status;
    private boolean isFeatured;

    public PasswordResponse(Password password) {
        this.id = password.getId();
        this.categoryId = password.getCategory().getId();
        this.categoryName = password.getCategory().getName();
        this.title = password.getTitle();
        this.username = password.getUsername();
        this.description = password.getDescription();
        this.status = password.getStatus().getDisplayName();
        this.isFeatured = password.getIsFeatured();
    }
}