package org.nokisev.PaymentProBot.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleApiService {


    public static final String APPLICATION_NAME = "PaymentProRecord";
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static final String TOKENS_DIRECTORY_PATH = "tokens";
    public static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
    public static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleApiService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(in)
        );

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8888)
                .build();
        return new AuthorizationCodeInstalledApp(flow,receiver).authorize("user");
    }

    public static String SpreadSheetsRead(String userId) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadSheetsId = "15WBRurm9KULlsk-x3ULDQ5llf146D8XZ0HqzGRHfk1k";
        final String allRange = "Salary!A2:E";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, GoogleApiService.JSON_FACTORY, GoogleApiService.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(GoogleApiService.APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadSheetsId, allRange)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            return "No data found";
        } else {
            String msg = "Name,             Tg,             Input,             Procent,             Final\n";
            for (List row : values) {
                if (String.valueOf(row.get(1)).contains(userId)) {
                    msg += row.get(0) + ", " + row.get(1) + ", " + row.get(2) + ", " + row.get(3) + "%, " + row.get(4) +"\n";
                }
            }
            return msg;
        }
    }

    public static String SpreadSheetsWrite() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadSheetsId = "15WBRurm9KULlsk-x3ULDQ5llf146D8XZ0HqzGRHfk1k"; // TODO: Add your Sheet id here
        final String allRange = "Salary!A2:F10";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, GoogleApiService.JSON_FACTORY, GoogleApiService.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(GoogleApiService.APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadSheetsId, allRange)
                .execute();
        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            return "No data found";
        } else {
            ValueRange appendBody = new ValueRange()
                    .setValues(Arrays.asList(
                            Arrays.asList("This", "was", "added", "from", "code!")
                    ));

            AppendValuesResponse result =
                    service.spreadsheets().values().append(spreadSheetsId, "Salary", appendBody)
                            .setValueInputOption("USER_ENTERED")
                            .setInsertDataOption("INSERT_ROWS")
                            .setIncludeValuesInResponse(true)
                            .execute();
            for (List row : values) {
                if (String.valueOf(row.get(0)).contains("This")) {
                    System.out.println(row);
                }
            }
        }
        return " p ";
    }
}
