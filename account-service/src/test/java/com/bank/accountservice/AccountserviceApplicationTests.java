package com.bank.accountservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = AccountserviceApplication.class)
@ActiveProfiles("test")
class AccountserviceApplicationTests {

	@Test
	void contextLoads() {
	}

}
