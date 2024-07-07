import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ATM {
  private Card card;
  private static final int ATM_BALANCE_LIMIT = 5000;
  private int atmBalance;
  private Scanner scanner = new Scanner(System.in);
  private int pinAttempts = 0;

  public void start() {
    try {
      atmBalance = FileManager.loadATMBalance();
    } catch (IOException e) {
      System.out.println("Ошибка при загрузке баланса банкомата: " + e.getMessage());
      atmBalance = ATM_BALANCE_LIMIT;
    }

    System.out.println("Введите номер карты: ");
    String cardNumber = scanner.nextLine();
    if (!validateCardNumber(cardNumber)) {
      System.out.println("Номер карты не соответствует формату!");
      return;
    }

    try {
      card = FileManager.loadCard(cardNumber);
      if (card == null) {
        System.out.println("Карта не найдена.");
        return;
      }

      if (card.isBlocked()) {
        if (card.getBlockTime() != null) {
          Duration duration = Duration.between(card.getBlockTime(), LocalDateTime.now());
          if (duration.toHours() >= 24) {
            card.unblockCard();
            FileManager.saveCard(card);
            System.out.println("Блокировка карты снята. Попробуйте снова.");
          } else {
            System.out.println("Карта заблокирована. Попробуйте позже.");
            return;
          }
        } else {
          System.out.println("Карта заблокирована. Попробуйте позже.");
          return;
        }
      }
    } catch (IOException e) {
      System.out.println("Ошибка при загрузке данных карты: " + e.getMessage());
      return;
    }

    while (pinAttempts < 3) {
      System.out.println("Введите пин-код: ");
      String pinCode = scanner.nextLine();
      if (validatePinCode(pinCode)) {
        if (pinCode.equals(card.getPin())) {
          System.out.println("Доступ предоставлен!");
          showMenu();
          return;
        } else {
          pinAttempts++;
          System.out.println("Неправильный пин-код! Повторите попытку.");
        }
      } else {
        System.out.println("Пин-код должен содержать 4 цифры.");
      }
    }

    card.blockCard();
    try {
      FileManager.saveCard(card);
    } catch (IOException e) {
      System.out.println("Ошибка при сохранении данных карты: " + e.getMessage());
    }
    System.out.println("Карта заблокирована из-за 3 неправильных попыток ввода ПИН-кода.");
  }

  private boolean validateCardNumber(String cardNumber) {
    Pattern patternNumber = Pattern.compile("^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}$");
    Matcher matcherNumber = patternNumber.matcher(cardNumber);
    return matcherNumber.find();
  }

  private boolean validatePinCode(String pinCode) {
    Pattern patternCode = Pattern.compile("^[0-9]{4}$");
    Matcher matcherCode = patternCode.matcher(pinCode);
    return matcherCode.find();
  }

  private void showMenu() {
    while (true) {
      System.out.println("\nВыберите операцию:");
      System.out.println("1. Проверить баланс");
      System.out.println("2. Снять средства");
      System.out.println("3. Пополнить баланс");
      System.out.println("4. Выход");

      int choice = Integer.parseInt(scanner.nextLine());

      switch (choice) {
        case 1:
          checkBalance();
          break;
        case 2:
          withdraw();
          break;
        case 3:
          deposit();
          break;
        case 4:
          exit();
          return;
        default:
          System.out.println("Неверный выбор. Повторите попытку.");
      }
    }
  }

  private void checkBalance() {
    System.out.println("Текущий баланс: " + card.getBalance() + " рублей.");
  }

  private void withdraw() {
    System.out.println("Введите сумму для снятия: ");
    int amount = Integer.parseInt(scanner.nextLine());

    if (amount > card.getBalance()) {
      System.out.println("Недостаточно средств на счете.");
    } else if (amount > atmBalance) {
      System.out.println("Недостаточно средств в банкомате. Остаток в банкомате: " + atmBalance + " рублей.");
    } else {
      card.setBalance(card.getBalance() - amount);
      atmBalance -= amount;
      System.out.println("Операция успешна. Новый баланс: " + card.getBalance() + " рублей.");
      try {
        FileManager.saveCard(card);
        FileManager.saveATMBalance(atmBalance);
      } catch (IOException e) {
        System.out.println("Ошибка при сохранении данных карты: " + e.getMessage());
      }
    }
  }

  private void deposit() {
    System.out.println("Введите сумму для пополнения: ");
    int amount = Integer.parseInt(scanner.nextLine());

    if (amount > 1000000) {
      System.out.println("Сумма пополнения не должна превышать 1 000 000 рублей.");
    } else {
      card.setBalance(card.getBalance() + amount);
      atmBalance += amount; // Увеличиваем остаток в банкомате
      System.out.println("Операция успешна. Новый баланс: " + card.getBalance() + " рублей.");
      try {
        FileManager.saveCard(card);
        FileManager.saveATMBalance(atmBalance); // Сохраняем новый баланс банкомата
      } catch (IOException e) {
        System.out.println("Ошибка при сохранении данных карты: " + e.getMessage());
      }
    }
  }

  private void exit() {
    try {
      FileManager.saveATMBalance(atmBalance);
    } catch (IOException e) {
      System.out.println("Ошибка при сохранении баланса банкомата: " + e.getMessage());
    }
    System.out.println("Завершение работы.");
    scanner.close();
  }
}
