package io.github.oldborn.fundamakelaar2026;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(DisableShellTestConfig.class)
class FundaMakelaar2026ApplicationTests {

    @Test
    void contextLoads() {
    }

}
