package com.example.parkingmanagementsystem.Service;

import com.example.parkingmanagementsystem.Repository.ParkingSpacesRepository;
import com.example.parkingmanagementsystem.Repository.VehicleRepository;
import com.example.parkingmanagementsystem.entity.Vehicles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ParkingSpacesRepository parkingSpaceRepository;

    private final Map<String, String> vehicleToSpaceTypeMap = Map.of(
            "小型车辆", "小型车位",
            "中型车辆", "中型车位",
            "大型车辆", "大型车位"
    );

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, ParkingSpacesRepository parkingSpaceRepository) {
        this.vehicleRepository = vehicleRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
    }

    public Map<String, Integer> createVehicle(String plateNumber, String vehicleType, String entryTime, String exitTime) {
        // 获取对应的停车位类型
        String spaceType = vehicleToSpaceTypeMap.get(vehicleType);
        if (spaceType == null) {
            throw new IllegalArgumentException("未知的车辆类型: " + vehicleType);
        }

        // 查找一个空闲的停车位
        Map<String, Object> availableSpace = parkingSpaceRepository.findAvailableSpace(spaceType);
        if (availableSpace.isEmpty()) {
            throw new IllegalStateException("没有可用的停车位");
        }
        int spaceId = (int) availableSpace.get("space_id");

        // 转换时间字符串为 Date 对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            Date entryDate = dateFormat.parse(entryTime);
            Date exitDate = exitTime != null ? dateFormat.parse(exitTime) : null;

            // 创建车辆对象并计算费用
            Vehicles vehicle = new Vehicles();
            vehicle.setPlateNumber(plateNumber);
            vehicle.setVehicleType(vehicleType);
            vehicle.setEntryTime(entryDate);
            vehicle.setExitTime(exitDate);
            vehicle.setSpaceId(spaceId);

            Double fee = exitDate != null ? vehicle.calculateFee() : 0.0;
            vehicle.setFee(fee);

            // 调用仓库层创建车辆信息
            int vehicleId = vehicleRepository.createVehicle(
                    plateNumber,
                    vehicleType,
                    entryTime,
                    exitTime,
                    fee,
                    spaceId
            );

            if (vehicleId > 0) {
                // 更新停车位的占用状态
                parkingSpaceRepository.updateSpaceOccupiedStatus(spaceId, 1);

                // 获取停车场ID（假设从 availableSpace 中获取）
                int lotId = (int) availableSpace.get("lot_id");

                // 重新统计停车场空闲车位
                int availableSpaces = parkingSpaceRepository.countAvailableSpacesByLotId(lotId);

                // 更新 parking_lots 表中的空闲车位数量
                parkingSpaceRepository.updateAvailableSpacesInLot(lotId, availableSpaces);

                // 返回生成的车辆ID、停车位ID和空闲车位数量
                Map<String, Integer> result = new HashMap<>();
                result.put("vehicleId", vehicleId);
                result.put("spaceId", spaceId);
                result.put("availableSpaces", availableSpaces);
                return result;
            } else {
                throw new IllegalStateException("创建车辆信息失败");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("时间格式错误或其他参数无效", e);
        }
    }

    public List<Map<String, Object>> getAllVehicles() {
        return vehicleRepository.getAllVehicles();
    }

    //根据ID查询车辆信息
    public Map<String, Object> getVehicleById(int id) {
        Map<String, Object> vehicle = vehicleRepository.getVehicleById(id);
        if (vehicle == null || vehicle.isEmpty()) {
            throw new NoSuchElementException("车辆信息未找到");
        }
        return vehicle;
    }

//更新车辆信息
public boolean updateVehicle(int id, String plateNumber, String vehicleType, String entryTime, String exitTime) {
    if (id <= 0 || plateNumber == null || vehicleType == null || entryTime == null) {
        return false;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    try {
        Date entryDate = dateFormat.parse(entryTime);
        Date exitDate = exitTime != null ? dateFormat.parse(exitTime) : null;

        Vehicles vehicle = new Vehicles();
        vehicle.setPlateNumber(plateNumber);
        vehicle.setVehicleType(vehicleType);
        vehicle.setEntryTime(entryDate);
        vehicle.setExitTime(exitDate);

        Double fee = exitDate != null ? vehicle.calculateFee() : 0.0;

        // 获取原来的 spaceId 并将其 occupied 状态改为 0
        Integer originalSpaceId = vehicleRepository.getVehicleSpaceId(id);
        if (originalSpaceId != null) {
            parkingSpaceRepository.updateSpaceOccupiedStatus(originalSpaceId, 0);
        }

        // 查找新的空闲停车位
        String spaceType = vehicleToSpaceTypeMap.get(vehicleType);
        if (spaceType == null) {
            throw new IllegalArgumentException("未知的车辆类型: " + vehicleType);
        }

        Map<String, Object> availableSpace = parkingSpaceRepository.findAvailableSpace(spaceType);
        if (availableSpace.isEmpty()) {
            throw new IllegalStateException("没有可用的停车位");
        }
        int newSpaceId = (int) availableSpace.get("space_id");

        // 更新车辆信息
        int rowsUpdated = vehicleRepository.updateVehicle(
                id,
                plateNumber,
                vehicleType,
                entryTime,
                exitTime,
                fee,
                newSpaceId
        );

        if (rowsUpdated > 0) {
            // 更新新的停车位的 occupied 状态为 1
            parkingSpaceRepository.updateSpaceOccupiedStatus(newSpaceId, 1);
        }

        return rowsUpdated > 0;
    } catch (Exception e) {
        throw new IllegalArgumentException("时间格式错误或其他参数无效", e);
    }
}


    public boolean deleteVehicle(int id) {
    // 获取车辆信息
    Map<String, Object> vehicle = vehicleRepository.getVehicleById(id);
    if (vehicle.isEmpty()) {
        return false; // 如果车辆不存在，返回 false
    }

    int spaceId = (int) vehicle.get("space_id");

    // 获取停车位所在的停车场ID
    Map<String, Object> space = parkingSpaceRepository.getSpaceBySpaceId(spaceId);
    if (space.isEmpty()) {
        throw new IllegalStateException("停车位信息未找到");
    }
    int lotId = (int) space.get("lot_id");

    int rowsDeleted = vehicleRepository.deleteVehicle(id);
    if (rowsDeleted > 0) {
        // 更新停车位的占用状态
        parkingSpaceRepository.updateSpaceOccupiedStatus(spaceId, 0);

        // 更新停车场的空闲车位数量
        parkingSpaceRepository.updateAvailableSpacesInLot2(lotId, 1);
    }

    return rowsDeleted > 0;
}


}

