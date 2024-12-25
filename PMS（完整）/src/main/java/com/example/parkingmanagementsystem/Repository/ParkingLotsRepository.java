package com.example.parkingmanagementsystem.Repository;

import com.example.parkingmanagementsystem.entity.ParkingLots;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ParkingLotsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 添加停车场数据
    public void addParkingLot(ParkingLots parkingLot) {
        String sql = "INSERT INTO parking_lots (name, address, total_spaces, available_spaces) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, parkingLot.getName(), parkingLot.getAddress(), parkingLot.getTotalSpaces(), parkingLot.getAvailableSpaces());
    }

    // 查询所有停车场
    public List<ParkingLots> getAllParkingLots() {
        String query = "SELECT * FROM parking_lots";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            ParkingLots parkingLot = new ParkingLots();
            parkingLot.setLotId(rs.getInt("lot_id"));
            parkingLot.setName(rs.getString("name"));
            parkingLot.setAddress(rs.getString("address"));
            parkingLot.setTotalSpaces(rs.getInt("total_spaces"));
            parkingLot.setAvailableSpaces(rs.getInt("available_spaces"));
            return parkingLot;
        });
    }

    // 查询停车场信息
    public ParkingLots getParkingLotById(Integer lotId) {
        String query = "SELECT * FROM parking_lots WHERE lot_id = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{lotId}, (rs, rowNum) -> {
            ParkingLots lot = new ParkingLots();
            lot.setLotId(rs.getInt("lot_id"));
            lot.setName(rs.getString("name"));
            lot.setAddress(rs.getString("address"));
            lot.setTotalSpaces(rs.getInt("total_spaces"));
            lot.setAvailableSpaces(rs.getInt("available_spaces"));
            return lot;
        });
    }

    // 更新停车场信息
    public void updateParkingLot(ParkingLots parkingLot) {
        String updateQuery = "UPDATE parking_lots SET total_spaces = ?, available_spaces = ? WHERE lot_id = ?";
        jdbcTemplate.update(updateQuery,
                parkingLot.getTotalSpaces(),
                parkingLot.getAvailableSpaces(),
                parkingLot.getLotId()
        );
    }
}

