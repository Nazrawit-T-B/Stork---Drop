package com.minStork.Stork.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private Integer currentVersion = 1;

    @Column(nullable = false)
    private Boolean deleted = false;

       @Column(nullable = false)
private Boolean isPublic = false;
    
    @Column(nullable = false)
    private LocalDateTime lastModified = LocalDateTime.now();

    
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;


    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private List<FileVersionEntity> versions = new ArrayList<>();


    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private List<PermissionEntity> permissions = new ArrayList<>();



    public FileEntity() {
    }



    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getIsPublic() {
    return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
    this.isPublic = isPublic;
     }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public List<FileVersionEntity> getVersions() {
        return versions;
    }

    public List<PermissionEntity> getPermissions() {
        return permissions;
    }
}
