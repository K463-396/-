package com.example.parkingmanagementsystem.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * (ParkingLots)实体类
 *
 * @author makejava
 * @since 2024-12-23 15:01:13
 */
@Entity
public class ParkingLots implements Serializable {

    private static final long serialVersionUID = -64834269849707456L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lotId;

    private String name;

    private String address;

    private Integer totalSpaces;

    private Integer availableSpaces;


    public ParkingLots() {
    }

    public ParkingLots(Integer availableSpaces, Integer totalSpaces, String address, String name, Integer lotId) {
        this.availableSpaces = availableSpaces;
        this.totalSpaces = totalSpaces;
        this.address = address;
        this.name = name;
        this.lotId = lotId;
    }

    public Integer getLotId() {
        return lotId;
    }

    public void setLotId(Integer lotId) {
        this.lotId = lotId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getTotalSpaces() {
        return totalSpaces;
    }

    public void setTotalSpaces(Integer totalSpaces) {
        this.totalSpaces = totalSpaces;
    }

    public Integer getAvailableSpaces() {
        return availableSpaces;
    }

    public void setAvailableSpaces(Integer availableSpaces) {
        this.availableSpaces = availableSpaces;
    }


}

