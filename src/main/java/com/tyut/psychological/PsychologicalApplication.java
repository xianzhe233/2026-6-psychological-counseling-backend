package com.tyut.psychological;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tyut.psychological.**.mapper")
public class PsychologicalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PsychologicalApplication.class, args);
    }
}
