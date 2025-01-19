package com.pedro.apiwk;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

@SpringBootApplication
public class ApiWkApplication implements CommandLineRunner {

    private final DataSource dataSource;

    public ApiWkApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiWkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificando a conexão com o banco
        System.out.println("Conexão estabelecida: " + dataSource.getConnection().getMetaData().getDatabaseProductName());
    }
}