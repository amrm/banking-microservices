package com.bank.transferservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TransferserviceApplication.class)
@ActiveProfiles("test")
class TransferserviceApplicationTests {

	@Test
	void contextLoads() {
		System.err.println("");
	}

}
