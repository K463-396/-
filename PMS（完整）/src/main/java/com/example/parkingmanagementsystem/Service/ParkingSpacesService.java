package com.example.parkingmanagementsystem.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.parkingmanagementsystem.Repository.ParkingSpacesRepository;
import com.example.parkingmanagementsystem.entity.ParkingSpaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Service
public class ParkingSpacesService {

    // 定义一个名为logger的私有静态最终变量，用于日志记录
    private static final Logger logger = LoggerFactory.getLogger(ParkingSpacesService.class);

    // 定义一个名为parkingSpacesRepository的私有最终字段，用于访问ParkingSpaces数据存储
    private final ParkingSpacesRepository parkingSpacesRepository;

    // 自动注入ParkingLotsService的实例，用于处理与停车相关的业务逻辑
    @Autowired
    private ParkingLotsService parkingLotsService;

    /**
     * 构造函数用于初始化ParkingSpacesService类的实例
     * 该构造函数接收一个ParkingSpacesRepository接口的实现作为参数，用于后续的操作
     *
     * @param parkingSpacesRepository 停车空间数据访问对象，用于与数据存储进行交互
     */
    public ParkingSpacesService(ParkingSpacesRepository parkingSpacesRepository) {
        this.parkingSpacesRepository = parkingSpacesRepository;
    }

    // 添加车位并同步停车场数据
@Transactional
public void addParkingSpace(ParkingSpaces parkingSpace, Integer lotId) {
    logger.info("Adding parking space: {}", parkingSpace);
    try {
        parkingSpacesRepository.addParkingSpace(parkingSpace, lotId);
        logger.info("Syncing parking lot spaces for lot ID: {}", lotId);
        parkingLotsService.syncParkingLotSpaces(lotId);
    } catch (Exception e) {
        logger.error("Error adding parking space: {}", e.getMessage(), e);
        throw new RuntimeException("Failed to add parking space", e);
    }
}



    // 查询所有车位信息
    public List<ParkingSpaces> getAllParkingSpaces() {
        return parkingSpacesRepository.findAll();
    }

    // 根据车位ID查询停车位信息
    public ParkingSpaces getParkingSpaceById(Integer spaceId) throws SQLException {
        return parkingSpacesRepository.getParkingSpaceById(spaceId);
    }

    // 更新车位状态并同步停车场数据
    @Transactional
    public void updateParkingSpace(ParkingSpaces parkingSpace, Integer lotId) {
        logger.info("Updating parking space: {}", parkingSpace);
        parkingSpacesRepository.updateParkingSpace(parkingSpace);
        logger.info("Syncing parking lot spaces for lot ID: {}", lotId);
        parkingLotsService.syncParkingLotSpaces(lotId);
    }

}
