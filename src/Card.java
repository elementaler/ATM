import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Card {
    private String cardNumber;
    private String pin;
    private BigDecimal balance;
    private boolean blocked;
    private int failedPinAttempts;
    private LocalDateTime lastFailedAttemptTime;

    public Card(String cardNumber, String pin, BigDecimal balance) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
        this.blocked = false;
        this.failedPinAttempts = 0;
        this.lastFailedAttemptTime = null;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public boolean validatePIN(String pinToCheck) {
        return pin.equals(pinToCheck);
    }

    public BigDecimal getBalance() {
        return balance.setScale(2, RoundingMode.HALF_UP);
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance.setScale(2, RoundingMode.HALF_UP);
    }

    public String getPin() {
        return pin;
    }

    public boolean isBlocked() {
        if (failedPinAttempts >= 3 && lastFailedAttemptTime != null) {
            LocalDateTime now = LocalDateTime.now();
            long hoursPassed = lastFailedAttemptTime.until(now, ChronoUnit.HOURS);
            if (hoursPassed < 24) {
                return true;
            } else {
                failedPinAttempts = 0;
                lastFailedAttemptTime = null;
            }
        }
        return blocked;
    }

    public void resetFailedPinAttempts() {
        failedPinAttempts = 0;
        lastFailedAttemptTime = null;
    }

    public void recordFailedPinAttempt() {
        failedPinAttempts++;
        lastFailedAttemptTime = LocalDateTime.now();
    }

    public void blockCard() {
        blocked = true;
    }
}
