package com.example.parkingmanagementsystem.Service;

import com.example.parkingmanagementsystem.Repository.ParkingLotsRepository;
import com.example.parkingmanagementsystem.Repository.ParkingSpacesRepository;
import com.example.parkingmanagementsystem.entity.ParkingLots;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingLotsService {

    @Autowired
    private ParkingLotsRepository parkingLotsRepository;

    @Autowired
    private ParkingSpacesRepository parkingSpacesRepository;

    // 添加停车场
    public void addParkingLot(ParkingLots parkingLot) {
        parkingLotsRepository.addParkingLot(parkingLot); // 假设您有一个方法来将数据保存到数据库
    }

    // 查询所有停车场
    public List<ParkingLots> getAllParkingLots() {
        return parkingLotsRepository.getAllParkingLots();
    }

    // 获取停车场信息
    public ParkingLots getParkingLotById(Integer lotId) {
        return parkingLotsRepository.getParkingLotById(lotId);
    }

    // 同步停车场车位数量
    // ParkingLotsService.java
public void syncParkingLotSpaces(Integer lotId) {

    try {
        int totalSpaces = parkingSpacesRepository.countTotalSpacesByLotId(lotId);
        int availableSpaces = parkingSpacesRepository.countAvailableSpacesByLotId(lotId);



        ParkingLots parkingLot = parkingLotsRepository.getParkingLotById(lotId);
        if (parkingLot != null) {
            parkingLot.setTotalSpaces(totalSpaces);
            parkingLot.setAvailableSpaces(availableSpaces);
            parkingLotsRepository.updateParkingLot(parkingLot);

        } else {

        }
    } catch (Exception e) {

        throw new RuntimeException("Failed to synchronize parking lot spaces", e);
    }
}

}
