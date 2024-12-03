package com.example.SMDSimulator;

import com.example.SMDSimulator.service.DeviceSimulatorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class SmdSimulatorApplication implements CommandLineRunner {

	@Autowired
	private DeviceSimulatorService deviceSimulatorService;

	public static void main(String[] args) {
		SpringApplication.run(SmdSimulatorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String deviceId;

		if (args.length >= 1) {
			deviceId = args[0];
		} else {
			deviceId = getDeviceIdFromConfig();
			if (deviceId == null) {
				System.out.println("Device ID not provided and not found in configuration file.");
				System.out.println("Usage: java -jar SmdSimulatorApplication.jar <device_id>");
				System.exit(1);
			}
		}

		deviceSimulatorService.startSimulation(deviceId);
	}

	private String getDeviceIdFromConfig() {
		Properties properties = new Properties();
		try (FileInputStream input = new FileInputStream("config.properties")) {
			properties.load(input);
			return properties.getProperty("device.id");
		} catch (IOException e) {
			System.err.println("Error reading configuration file: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
