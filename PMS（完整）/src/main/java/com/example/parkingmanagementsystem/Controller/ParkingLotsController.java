package com.example.parkingmanagementsystem.Controller;

import com.example.parkingmanagementsystem.Service.ParkingLotsService;
import com.example.parkingmanagementsystem.entity.ParkingLots;
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
@RequestMapping("/api/parking-lots")
public class ParkingLotsController {

    @Autowired
    private ParkingLotsService parkingLotsService;

    // 辅助方法，用于将日志信息写入文件
    private void logToFile(String message) {
        try (FileWriter fileWriter = new FileWriter("application.log", true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 添加停车场数据
    @PostMapping
    public ResponseEntity<Map<String, String>> addParkingLot(@RequestBody ParkingLots parkingLot) {
        Map<String, String> response = new HashMap<>();
        try {
            parkingLotsService.addParkingLot(parkingLot); // 假设您有一个服务层方法来保存停车场
            response.put("message", "停车场添加成功！");
            logToFile("停车场添加成功，停车场信息: " + parkingLot);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "添加停车场失败: " + e.getMessage());
            logToFile("添加停车场失败，错误信息: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 查询所有停车场信息
    @GetMapping
    public ResponseEntity<List<ParkingLots>> getAllParkingLots() {
        try {
            List<ParkingLots> parkingLots = parkingLotsService.getAllParkingLots();
            logToFile("查询所有停车场信息成功，共有 " + parkingLots.size() + " 个停车场");
            return ResponseEntity.ok(parkingLots);
        } catch (Exception e) {
            logToFile("查询所有停车场信息失败，错误信息: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 根据停车场ID查询停车场信息
    @GetMapping("/{lotId}")
    public ResponseEntity<ParkingLots> getParkingLotById(@PathVariable Integer lotId) {
        try {
            ParkingLots parkingLot = parkingLotsService.getParkingLotById(lotId);
            if (parkingLot != null) {
                logToFile("查询停车场信息成功，停车场ID: " + lotId);
                return ResponseEntity.ok(parkingLot);
            } else {
                logToFile("未找到停车场，ID: " + lotId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            logToFile("查询停车场信息失败，ID: " + lotId + "，错误信息: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}