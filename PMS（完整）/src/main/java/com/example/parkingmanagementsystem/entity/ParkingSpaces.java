package com.example.parkingmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;

/**
 * (ParkingSpaces)实体类
 *
 * @author makejava
 * @since 2024-12-23 22:23:52
 */
@Entity
public class ParkingSpaces implements Serializable {
    private static final long serialVersionUID = 759168915139762164L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer spaceId;

    private String spaceType;

    private Integer occupied;

    private Integer lotId;

    public ParkingSpaces() {
    }

    public ParkingSpaces(Integer spaceId, String spaceType, Integer occupied, Integer lotId) {
        this.spaceId = spaceId;
        this.spaceType = spaceType;
        this.occupied = occupied;
        this.lotId = lotId;
    }

    public Integer getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Integer spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    public Integer getOccupied() {
        return occupied;
    }

    public void setOccupied(Integer occupied) {
        this.occupied = occupied;
    }

    public Integer getLotId() {
        return lotId;
    }

    public void setLotId(Integer lotId) {
        this.lotId = lotId;
    }

}

