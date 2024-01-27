package org.nokisev.PaymentProBot;

import org.nokisev.PaymentProBot.service.GoogleApiService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.nokisev.PaymentProBot.config.BotConfig;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot  {

    private BotConfig botConfig;

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/lead", "add to spreadsheets [name] [sum] [value] [worker] [date]"));
        listOfCommands.add(new BotCommand("/mydata", "get info about your salary"));
        listOfCommands.add(new BotCommand("/help", "info about commands"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {

        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String msg = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (msg) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    help(chatId);
                    break;
                case "/help":
                    help(chatId);
                    break;
                case "/mydata":
                    String userId = update.getMessage().getFrom().getUserName();
                    System.out.println(userId + " use mydata");
                    try {
                        sendMessage(chatId, GoogleApiService.SpreadSheetsRead(userId));
                    } catch (GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "/lead":
                    break;
                default:
                    break;
            }
        }
    }

    private void help(long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText("Ниже представлены все мои команды:\n" +
                "/lead - добавление информации в Google Sheets\n" +
                "/mydata - получить информацию о зарплате");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var leadButton = new InlineKeyboardButton();
        leadButton.setText("/lead");
        leadButton.setCallbackData("LEAD_BUTTON");

        var dataButton = new InlineKeyboardButton();
        dataButton.setText("/mydata");
        dataButton.setCallbackData("DATA_BUTTON");

        rowInLine.add(leadButton);
        rowInLine.add(dataButton);

        rowsInline.add(rowInLine);

        markupInline.setKeyboard(rowsInline);
        msg.setReplyMarkup(markupInline);

        try {
            execute(msg);
        } catch (TelegramApiException e) {

        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет, " + name + ", это бот для контроля зарплаты! \n "
                + "                 понял, да?";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }
}