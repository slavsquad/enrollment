package org.product.catalog.analyzer.enrollment;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
class EnrollmentApplicationTests {


	@Test
	void contextLoads() {
	}

}
