package io.github.oldborn.fundamakelaar2026;

import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.FundaAanbodApiClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FundaAanbodApiClientConfig.class)
public class FundaMakelaar2026Application {

    public static void main(String[] args) {
        SpringApplication.run(FundaMakelaar2026Application.class, args);
    }

}
