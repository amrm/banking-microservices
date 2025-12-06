package com.bank.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = UserserviceApplication.class)
@ActiveProfiles("test")
class UserserviceApplicationTests {

	@Test
	void contextLoads() {
	}

}
