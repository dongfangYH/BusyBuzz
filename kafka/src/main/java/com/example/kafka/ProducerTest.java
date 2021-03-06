package com.example.kafka;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

/**
 * @author yuanhang.liu@tcl.com
 * @description
 * @date 2020-04-18 17:44
 **/
public class ProducerTest {

    public static final String BROKER_LIST = "localhost:9092";
    public static final String TOPIC = "event-source";
    public static final String[] phones = {"iphone xs", "iphone 11", "s20 ultra", "P40+", "Mate 30 Pro"};

    public static Properties config(){
        Properties config = new Properties();
        config.put(BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        config.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(REQUEST_TIMEOUT_MS_CONFIG, "1000");
        config.put(DELIVERY_TIMEOUT_MS_CONFIG, "1000");
        //32m
        //config.put(BUFFER_MEMORY_CONFIG, 32 * 1024 * 1024L);
        config.put(CLIENT_ID_CONFIG, "c3");
        //重试一次
        //config.put(RETRIES_CONFIG, "1");
        return config;
    }

    public static void main(String[] args) throws Exception{

        Properties config = config();
        //config.put(INTERCEPTOR_CLASSES_CONFIG, "com.example.kafka.LogInterceptor");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(config);
        Gson gson = new Gson();
        Random random = new Random();
        long id = 0;

        for (;;){

            JsonObject object = new JsonObject();
            object.addProperty("id", ++id);
            object.addProperty("timestamp", System.currentTimeMillis());
            object.addProperty("phone", phones[random.nextInt(phones.length)]);
            String value = gson.toJson(object);

            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, value);
            try {
                producer.send(record);
                System.out.println("sent one record...");
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //关闭，释放资源
        //producer.close();
    }

}
