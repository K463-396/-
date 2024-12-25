package com.example.parkingmanagementsystem.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * (Tickets)实体类
 *
 * @author makejava
 * @since 2024-12-23 15:10:52
 */
public class Tickets implements Serializable {
    private static final long serialVersionUID = -44441445107111128L;

    private Integer ticketId;

    private Integer vehicleId;

    private Integer spaceId;

    private Date issueTime;

    public Tickets() {
    }

    public Tickets(Integer ticketId, Integer vehicleId, Integer spaceId, Date issueTime) {
        this.ticketId = ticketId;
        this.vehicleId = vehicleId;
        this.spaceId = spaceId;
        this.issueTime = issueTime;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Integer spaceId) {
        this.spaceId = spaceId;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

}

