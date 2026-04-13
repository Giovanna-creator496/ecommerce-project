package it.giovanna.Ecommerce;

import it.giovanna.ecommerce.EcommerceApplication;
import org.springframework.boot.SpringApplication;

public class TestEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.from(EcommerceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
