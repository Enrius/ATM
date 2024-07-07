import java.time.LocalDateTime;

public class Card {
  private String number;
  private String pin;
  private int balance;
  private boolean blocked;
  private LocalDateTime blockTime;

  public Card(String number, String pin, int balance) {
    this.number = number;
    this.pin = pin;
    this.balance = balance;
    this.blocked = false;
    this.blockTime = null;
  }

  public String getNumber() {
    return number;
  }

  public String getPin() {
    return pin;
  }

  public int getBalance() {
    return balance;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }

  public boolean isBlocked() {
    return blocked;
  }

  public void blockCard() {
    this.blocked = true;
    this.blockTime = LocalDateTime.now();
  }

  public void unblockCard() {
    this.blocked = false;
    this.blockTime = null;
  }

  public LocalDateTime getBlockTime() {
    return blockTime;
  }

  public void setBlockTime(LocalDateTime blockTime) {
    this.blockTime = blockTime;
  }
}
