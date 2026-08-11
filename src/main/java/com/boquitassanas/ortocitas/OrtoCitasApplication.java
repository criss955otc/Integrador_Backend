package com.boquitassanas.ortocitas;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrtoCitasApplication {

    public static void main(String[] args) {
        // Carga las variables del archivo .env y las inyecta en las propiedades del sistema
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(OrtoCitasApplication.class, args);
    }
}