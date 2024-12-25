package com.example.parkingmanagementsystem.Controller;

import com.example.parkingmanagementsystem.Service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
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

    @PostMapping
    public ResponseEntity<Map<String, Object>> createVehicle(@RequestBody Map<String, Object> vehicle) {
        String plateNumber = (String) vehicle.get("plateNumber");
        String vehicleType = (String) vehicle.get("vehicleType");
        String entryTime = (String) vehicle.get("entryTime");
        String exitTime = (String) vehicle.get("exitTime");

        logToFile("正在创建车辆，数据: " + vehicle);

        Map<String, Integer> result = vehicleService.createVehicle(plateNumber, vehicleType, entryTime, exitTime);

        Map<String, Object> response = new HashMap<>();
        if (result.get("vehicleId") > 0) {
            response.put("message", "车辆创建成功");
            response.put("vehicleId", result.get("vehicleId"));
            response.put("spaceId", result.get("spaceId"));
            logToFile("车辆创建成功，ID: " + result.get("vehicleId"));
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "车辆创建失败");
            logToFile("车辆创建失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public List<Map<String, Object>> getAllVehicles() {
        logToFile("正在获取所有车辆信息");
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable int id) {
        logToFile("正在获取ID为 " + id + " 的车辆信息");
        try {
            Map<String, Object> vehicle = vehicleService.getVehicleById(id);
            return ResponseEntity.ok(vehicle);
        } catch (NoSuchElementException e) {
            logToFile("未找到ID为 " + id + " 的车辆");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logToFile("获取ID为 " + id + " 的车辆信息时发生错误");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "服务器内部错误"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVehicle(@PathVariable int id, @RequestBody Map<String, Object> vehicle) {
        logToFile("正在更新ID为 " + id + " 的车辆信息，数据: " + vehicle);
        String plateNumber = (String) vehicle.get("plateNumber");
        String vehicleType = (String) vehicle.get("vehicleType");
        String entryTime = (String) vehicle.get("entryTime");
        String exitTime = (String) vehicle.get("exitTime");

        try {
            boolean success = vehicleService.updateVehicle(id, plateNumber, vehicleType, entryTime, exitTime);
            if (success) {
                logToFile("车辆信息更新成功，ID: " + id);
                return ResponseEntity.ok("车辆信息更新成功");
            } else {
                logToFile("车辆信息更新失败，ID: " + id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("车辆信息更新失败，请检查输入参数");
            }
        } catch (IllegalArgumentException e) {
            logToFile("非法参数，ID: " + id + "，错误信息: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("非法参数：" + e.getMessage());
        } catch (Exception e) {
            logToFile("更新ID为 " + id + " 的车辆信息时发生错误");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器错误，请稍后再试");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable int id) {
        logToFile("正在删除ID为 " + id + " 的车辆");
        try {
            boolean success = vehicleService.deleteVehicle(id);
            if (success) {
                logToFile("车辆删除成功，ID: " + id);
                return ResponseEntity.ok("车辆删除成功");
            } else {
                logToFile("车辆不存在，删除失败，ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("车辆不存在，删除失败");
            }
        } catch (Exception e) {
            logToFile("删除ID为 " + id + " 的车辆时发生错误");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除车辆时发生错误，请稍后再试");
        }
    }
}