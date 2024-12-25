package com.example.parkingmanagementsystem.Repository;


import com.example.parkingmanagementsystem.entity.ParkingSpaces;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class ParkingSpacesRepository {

    private final JdbcTemplate jdbcTemplate;

    public ParkingSpacesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // 添加车位，增加 lotId 参数
    public void addParkingSpace(ParkingSpaces parkingSpace, Integer lotId) {
        String insertQuery = "INSERT INTO parking_spaces (space_type, occupied, lot_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertQuery,
                parkingSpace.getSpaceType(),
                parkingSpace.getOccupied(),
                parkingSpace.getLotId()
        );
    }

    // 查询所有车位信息
    /**
     * 查询所有停车位信息
     *
     * 该方法通过查询数据库中的parking_spaces表来获取所有停车位的信息
     * 使用jdbcTemplate进行数据库操作，提高了数据访问的灵活性和效率
     *
     * @return List<ParkingSpaces> 返回一个ParkingSpaces对象列表，包含所有停车位的信息
     */
    public List<ParkingSpaces> findAll() {
        // SQL查询语句，用于获取parking_spaces表中的所有列
        String query = "SELECT * FROM parking_spaces";
        // 使用jdbcTemplate执行查询，并将结果映射到ParkingSpaces对象列表中
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            // 创建一个新的ParkingSpaces对象
            ParkingSpaces parkingSpace = new ParkingSpaces();
            // 设置停车位的ID
            parkingSpace.setSpaceId(rs.getInt("space_id"));
            // 设置停车位的类型
            parkingSpace.setSpaceType(rs.getString("space_type"));
            // 设置停车位是否被占用（1表示占用，0表示空闲）
            parkingSpace.setOccupied(rs.getInt("occupied"));

            parkingSpace.setLotId(rs.getInt("lot_id"));
            // 返回构建好的ParkingSpaces对象
            return parkingSpace;
        });
    }

    // 根据车位ID查询停车位信息
    public ParkingSpaces getParkingSpaceById(Integer spaceId) throws SQLException {
        String query = "SELECT * FROM parking_spaces WHERE space_id = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{spaceId}, (rs, rowNum) -> {
            ParkingSpaces space = new ParkingSpaces();
            space.setSpaceId(rs.getInt("space_id"));
            space.setSpaceType(rs.getString("space_type"));
            space.setOccupied(rs.getInt("occupied"));
            space.setLotId(rs.getInt("lot_id"));
            return space;
        });
    }

    /**
     * 更新车位状态的方法
     * 该方法通过接受一个ParkingSpaces对象作为参数，更新数据库中对应车位的空间类型和占用状态
     *
     * @param parkingSpace ParkingSpaces对象，包含要更新的车位信息，包括空间类型、是否占用和车位ID
     */
    public void updateParkingSpace(ParkingSpaces parkingSpace) {
        // 定义更新SQL语句，通过车位ID定位记录，更新空间类型和占用状态
        String updateQuery = "UPDATE parking_spaces SET space_type = ?, occupied = ? WHERE space_id = ?";

        // 执行更新操作
        jdbcTemplate.update(updateQuery,
                parkingSpace.getSpaceType(), // 车位空间类型
                parkingSpace.getOccupied(),  // 车位占用状态
                parkingSpace.getSpaceId()    // 车位ID，用于定位要更新的记录
        );
    }

    // 获取某停车场的车位数量
    /**
     * 根据停车场ID查询该停车场的总车位数量
     *
     * @param lotId 停车场ID，用于标识特定的停车场
     * @return 停车场的总车位数量
     */
    public int countTotalSpacesByLotId(Integer lotId) {
        // 定义SQL查询语句，用于计算指定停车场ID的车位总数
        String query = "SELECT COUNT(*) FROM parking_spaces WHERE lot_id = ?";
        // 执行查询，并将结果转换为Integer类型返回
        return jdbcTemplate.queryForObject(query, new Object[]{lotId}, Integer.class);
    }

    // 获取某停车场的空闲车位数量
    /**
     * 根据停车场ID查询该停车场中的空闲车位数量。
     *
     * @param lotId 停车场ID，用于标识特定的停车场。
     * @return 空闲车位的数量。
     */
    public int countAvailableSpacesByLotId(Integer lotId) {
        // SQL查询语句，用于计算空闲车位数量。
        // 通过lot_id筛选特定停车场，并通过occupied=0筛选空闲状态的车位。
        String query = "SELECT COUNT(*) FROM parking_spaces WHERE lot_id = ? AND occupied = 0";
        // 使用JDBC模板执行查询，返回空闲车位的数量。
        // 参数包括查询语句、查询参数（停车场ID）和期望的返回值类型（Integer）。
        return jdbcTemplate.queryForObject(query, new Object[]{lotId}, Integer.class);
    }

    // 更新停车场的空闲车位数量
    public void updateAvailableSpacesInLot(int lotId, int availableSpaces) {
        String updateQuery = "UPDATE parking_lots SET available_spaces = ? WHERE lot_id = ?";
        jdbcTemplate.update(updateQuery, availableSpaces, lotId);
    }

    public void updateAvailableSpacesInLot2(int lotId, int increment) {
        String sql = "UPDATE parking_lots SET available_spaces = available_spaces + ? WHERE lot_id = ?";
        jdbcTemplate.update(sql, increment, lotId);
    }

    //删除车位
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

    // 查找一个空闲的停车位
    public Map<String, Object> findAvailableSpace(String spaceType) {
        String sql = "SELECT * FROM parking_spaces WHERE space_type = ? AND occupied = 0 ORDER BY space_id LIMIT 1";
        try {
            return jdbcTemplate.queryForMap(sql, spaceType);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyMap();
        }
    }

    // 更新停车位的占用状态
    public int updateSpaceOccupiedStatus(int spaceId, int occupied) {
        String sql = "UPDATE parking_spaces SET occupied = ? WHERE space_id = ?";
        return jdbcTemplate.update(sql, occupied, spaceId);
    }

    public Map<String, Object> getSpaceBySpaceId(int spaceId) {
        String sql = "SELECT lot_id FROM parking_spaces WHERE space_id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, spaceId);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyMap();
        }
    }
}
