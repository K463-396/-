package com.example.parkingmanagementsystem.Controller;

import com.example.parkingmanagementsystem.Service.ParkingSpacesService;
import com.example.parkingmanagementsystem.entity.ParkingSpaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking-spaces")
public class ParkingSpacesController {

    private final ParkingSpacesService parkingSpacesService;

    @Autowired
    public ParkingSpacesController(ParkingSpacesService parkingSpacesService) {
        this.parkingSpacesService = parkingSpacesService;
    }

    // 辅助方法，用于将日志信息写入文件
    private void logToFile(String message) {
        try (FileWriter fileWriter = new FileWriter("application.log", true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 添加车位
    @PostMapping
    public ResponseEntity<Map<String, String>> addParkingSpace(@RequestBody ParkingSpaces parkingSpace) {
        Map<String, String> response = new HashMap<>();
        try {
            parkingSpacesService.addParkingSpace(parkingSpace, parkingSpace.getLotId());
            response.put("message", "车位创建成功！");
            logToFile("车位创建成功，车位信息: " + parkingSpace);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "车位创建失败: " + e.getMessage());
            logToFile("车位创建失败，错误信息: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 查询所有车位信息
    @GetMapping
    public ResponseEntity<List<ParkingSpaces>> getAllParkingSpaces() {
        try {
            List<ParkingSpaces> parkingSpaces = parkingSpacesService.getAllParkingSpaces();
            logToFile("查询所有车位信息成功，共有 " + parkingSpaces.size() + " 个车位");
            return ResponseEntity.ok(parkingSpaces);
        } catch (Exception e) {
            logToFile("查询所有车位信息失败，错误信息: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 根据车位ID查询停车位信息
    @GetMapping("/{spaceId}")
    public ResponseEntity<Map<String, Object>> getParkingSpaceById(@PathVariable Integer spaceId) {
        Map<String, Object> response = new HashMap<>();
        try {
            ParkingSpaces parkingSpace = parkingSpacesService.getParkingSpaceById(spaceId);
            if (parkingSpace != null) {
                response.put("parkingSpace", parkingSpace);
                logToFile("查询车位信息成功，车位ID: " + spaceId);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "未找到车位，ID: " + spaceId);
                logToFile("未找到车位，ID: " + spaceId);
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("message", "查询车位信息失败: " + e.getMessage());
            logToFile("查询车位信息失败，ID: " + spaceId + "，错误信息: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // 更新车位信息
    @PutMapping("/{spaceId}")
    public ResponseEntity<String> updateParkingSpace(@PathVariable Integer spaceId,
                                                     @RequestBody ParkingSpaces parkingSpace,
                                                     @RequestParam Integer lotId) {
        try {
            ParkingSpaces currentSpace = parkingSpacesService.getParkingSpaceById(spaceId);
            if (currentSpace == null) {
                logToFile("更新车位信息失败，车位未找到，ID: " + spaceId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("车位未找到");
            }

            if (currentSpace.getOccupied() != 0) {
                logToFile("更新车位信息失败，只能更新空闲状态的车位，ID: " + spaceId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("只能更新空闲状态的车位");
            }

            parkingSpace.setSpaceId(spaceId);
            parkingSpacesService.updateParkingSpace(parkingSpace, lotId);
            logToFile("车位信息更新成功，ID: " + spaceId);
            return ResponseEntity.ok("车位更新成功！");
        } catch (Exception e) {
            logToFile("车位更新失败，ID: " + spaceId + "，错误信息: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("车位更新失败: " + e.getMessage());
        }
    }

}