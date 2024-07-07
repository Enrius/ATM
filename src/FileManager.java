import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FileManager {
  private static final String CARDS_FILE_NAME = "cards.txt";
  private static final String ATM_BALANCE_FILE_NAME = "atm_balance.txt";
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

  public static Card loadCard(String cardNumber) throws IOException {
    Map<String, Card> cards = loadCards();
    return cards.get(cardNumber);
  }

  public static void saveCard(Card card) throws IOException {
    Map<String, Card> cards = loadCards();
    cards.put(card.getNumber(), card);
    saveCards(cards);
  }

  public static Map<String, Card> loadCards() throws IOException {
    Map<String, Card> cards = new HashMap<>();
    BufferedReader reader = new BufferedReader(new FileReader(CARDS_FILE_NAME));
    String line;
    while ((line = reader.readLine()) != null) {
      String[] parts = line.split(" ");
      if (parts.length >= 4) {
        String number = parts[0];
        String pin = parts[1];
        int balance = Integer.parseInt(parts[2]);
        boolean blocked = Boolean.parseBoolean(parts[3]);
        LocalDateTime blockTime = (parts.length == 5) ? LocalDateTime.parse(parts[4], FORMATTER) : null;
        Card card = new Card(number, pin, balance);
        if (blocked) {
          card.blockCard();
          card.setBlockTime(blockTime);
        }
        cards.put(number, card);
      }
    }
    reader.close();
    return cards;
  }

  public static void saveCards(Map<String, Card> cards) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(CARDS_FILE_NAME));
    for (Card c : cards.values()) {
      writer.write(c.getNumber() + " " + c.getPin() + " " + c.getBalance() + " " + c.isBlocked());
      if (c.isBlocked()) {
        writer.write(" " + c.getBlockTime().format(FORMATTER));
      }
      writer.newLine();
    }
    writer.close();
  }

  public static int loadATMBalance() throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(ATM_BALANCE_FILE_NAME));
    int balance = Integer.parseInt(reader.readLine());
    reader.close();
    return balance;
  }

  public static void saveATMBalance(int balance) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(ATM_BALANCE_FILE_NAME));
    writer.write(Integer.toString(balance));
    writer.close();
  }
}
