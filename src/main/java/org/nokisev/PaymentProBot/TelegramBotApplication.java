package org.nokisev.PaymentProBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootApplication
public class TelegramBotApplication {

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		SpringApplication.run(TelegramBotApplication.class, args);
	}
}
