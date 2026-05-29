package com.minStork.Stork.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    // Stores the active validation token string used to authorize parallel requests securely
    @Column(unique = true, length = 255)
    private String authToken;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "owner")
    private List<FileEntity> ownedFiles = new ArrayList<>();

    // One user can upload many versions
    @OneToMany(mappedBy = "uploadedBy")
    private List<FileVersionEntity> uploadedVersions = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PermissionEntity> permissions = new ArrayList<>();

    public UserEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<FileEntity> getOwnedFiles() {
        return ownedFiles;
    }

    public List<FileVersionEntity> getUploadedVersions() {
        return uploadedVersions;
    }

    public List<PermissionEntity> getPermissions() {
        return permissions;
    }
}
