package com.minStork.Stork.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "permissions", 
        uniqueConstraints={
        @UniqueConstraint(columnNames={"user_id", "file_id"})
    })
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // READ, WRITE, OWNER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionType permissionType;

    // Many permissions belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Many permissions belong to one file
    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private FileEntity file;


    public PermissionEntity() {
    }


    public Long getId() {
        return id;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }
}