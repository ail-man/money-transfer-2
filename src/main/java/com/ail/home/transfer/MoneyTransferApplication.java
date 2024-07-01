package com.ail.home.transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MoneyTransferApplication {

	public static void main(final String[] args) {
		SpringApplication.run(MoneyTransferApplication.class, args);
	}

}
