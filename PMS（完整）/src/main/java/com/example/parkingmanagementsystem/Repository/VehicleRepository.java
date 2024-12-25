package com.example.parkingmanagementsystem.Repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.GetMapping;


import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class VehicleRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public VehicleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 创建车辆
    public int createVehicle(String plateNumber, String vehicleType, String entryTime, String exitTime, Double fee, Integer spaceId) {
        // 插入车辆信息的 SQL 语句
        String sql = "INSERT INTO vehicles (plate_number, vehicle_type, entry_time, exit_time, fee, space_id) VALUES (?, ?, ?, ?, ?, ?)";

        // 使用 jdbcTemplate 执行插入操作，返回影响的行数
        int result = jdbcTemplate.update(sql, plateNumber, vehicleType, entryTime, exitTime, fee, spaceId);

        // 如果插入成功，返回生成的车辆 ID
        if (result > 0) {
            // 获取生成的车辆 ID
            String idSql = "SELECT LAST_INSERT_ID()";
            return jdbcTemplate.queryForObject(idSql, Integer.class);
        }
        return 0; // 如果插入失败，返回 0
    }

    // 查询所有车辆
    @GetMapping
    public List<Map<String, Object>> getAllVehicles() {
        String sql = "SELECT vehicle_id, plate_number, vehicle_type, entry_time, exit_time, fee ,space_id FROM vehicles";
        List<Map<String, Object>> vehicles = jdbcTemplate.queryForList(sql);
        System.out.println("查询到的车辆信息：" + vehicles);
        return vehicles;
    }

    // 根据ID查询车辆
    public Map<String, Object> getVehicleById(int id) {
        String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, id);
        } catch (EmptyResultDataAccessException e) {
            return  Collections.emptyMap();  // 如果没有找到结果，返回空Map更安全
        }
    }

    // 获取车辆的 spaceId
    public Integer getVehicleSpaceId(int vehicleId) {
        String sql = "SELECT space_id FROM vehicles WHERE vehicle_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, vehicleId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /// 更新车辆信息
    public int updateVehicle(int id, String plateNumber, String vehicleType, String entryTime, String exitTime, Double fee, Integer spaceId) {
        String sql = "UPDATE vehicles SET plate_number = ?, vehicle_type = ?, entry_time = ?, exit_time = ?, fee = ?, space_id = ? WHERE vehicle_id = ?";
        return jdbcTemplate.update(sql, plateNumber, vehicleType, entryTime, exitTime, fee, spaceId, id);
    }


    // 删除车辆
    public int deleteVehicle(int id) {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";

        try {
            return jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            // 日志记录异常，可以帮助调试问题
            // log.error("删除车辆时发生错误，车辆ID: {}", id, e);
            return 0;
        }
    }

    //检查数据库中是否存在指定ID的车辆
    public boolean vehicleExists(int id) {
        String sql = "SELECT COUNT(1) FROM vehicles WHERE vehicle_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

}
