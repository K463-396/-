package com.example.parkingmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;
import java.io.Serializable;

/**
 * (Vehicles)实体类
 *
 * @author makejava
 * @since 2024-12-22 09:38:55
 */
@Entity
public class Vehicles implements Serializable {

    private static final long serialVersionUID = 938654387996686216L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vehicleId;

    private String plateNumber;

    private String vehicleType;

    private Date entryTime;

    private Date exitTime;

    private Double fee;

    private Integer spaceId;

    public Vehicles() {
    }

    public Vehicles(Integer vehicleId ,String plateNumber, String vehicleType, Date entryTime, Date exitTime, Double fee) {
        this.vehicleId = vehicleId;
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.fee = fee;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Integer getSpaceId() {return spaceId;}

    public void setSpaceId(Integer spaceId) {this.spaceId = spaceId;}


    // 计算费用的方法
    public double calculateFee() {
        if (entryTime == null || exitTime == null) {
            throw new IllegalArgumentException("Entry time and exit time must not be null.");
        }

        long durationInMillis = exitTime.getTime() - entryTime.getTime();

        if (durationInMillis <= 0) {
            throw new IllegalArgumentException("Exit time must be after entry time.");
        }

        // 转换为小时（向上取整）
        double durationInHours = Math.ceil(durationInMillis / (1000.0 * 60 * 60));

        // 费用计算逻辑
        if (durationInHours <= 12) {
            return durationInHours * 10;
        } else {
            return 12 * 10 + (durationInHours - 12) * 8;
        }
    }
}

