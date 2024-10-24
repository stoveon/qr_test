package kr.co.qr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class QrTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(QrTestApplication.class, args);
    }

}