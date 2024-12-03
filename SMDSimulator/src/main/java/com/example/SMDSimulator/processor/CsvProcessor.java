package com.example.SMDSimulator.processor;

import com.example.SMDSimulator.producer.MessageProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.concurrent.TimeUnit;

@Service
public class CsvProcessor {

    @Autowired
    private MessageProducer messageProducer;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final long timeIncrement = 60000;//0 -> pe 1 minut acum

    public void readAndSendData(String deviceId) {
        try (InputStream inputStream = getClass().getResourceAsStream("/sensor.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }

                float measurementValue = Float.parseFloat(line);
                ObjectNode jsonNode = objectMapper.createObjectNode();
                jsonNode.put("timestamp", computeTime(deviceId));
                jsonNode.put("device_id", deviceId);
                jsonNode.put("measure", measurementValue);
                String message = jsonNode.toString();

                messageProducer.sendMessage(message);
                TimeUnit.MILLISECONDS.sleep(timeIncrement);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static long computeTime(String id) {
        long timeMillis = System.currentTimeMillis();
        long timer = 0;
        timer += timeIncrement;
        saveDateToFile(id + ".txt", timeMillis + timer);
        return timeMillis + timer;
    }

    private static void saveDateToFile(String fileName, long date) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println(date);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

