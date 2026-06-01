package com.minStork.Stork.data;

public class FilePermissionInfoDto {

    private Long id;
    private String name;
    private String owner;

    public FilePermissionInfoDto(Long id, String name, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.owner = ownerUsername;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setOwner(String ownerUsername) {
        this.owner = ownerUsername;
    } 
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getOwner() {
        return owner;
    }
}
