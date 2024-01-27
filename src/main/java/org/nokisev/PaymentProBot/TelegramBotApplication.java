package org.nokisev.PaymentProBot;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.nokisev.PaymentProBot.service.GoogleApiService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@SpringBootApplication
public class TelegramBotApplication {

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		SpringApplication.run(TelegramBotApplication.class, args);
	}
}
