package com.minStork.Stork.data;

public class FilePermissionInfoDto {

    private Long id;
    private String name;
    private String ownerUsername;

    public FilePermissionInfoDto(Long id, String name, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.ownerUsername = ownerUsername;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    } 
}
