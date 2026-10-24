package io.github.oldborn.fundamakelaar2026;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DisableShellTestConfig {
    @Bean("springShellApplicationRunner")
    ApplicationRunner shellDisabledRunner(){
        return args -> {};
    }
}
