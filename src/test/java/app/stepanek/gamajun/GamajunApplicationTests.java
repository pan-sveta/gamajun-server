package app.stepanek.gamajun;

import app.stepanek.gamajun.services.ValidatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GamajunApplicationTests {
	private final ValidatorService validatorService;

	@Autowired
	GamajunApplicationTests(ValidatorService validatorService) {
		this.validatorService = validatorService;
	}

	@Test
	void contextLoads() {

	}

}
