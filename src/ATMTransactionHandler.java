import java.math.BigDecimal;

public interface ATMTransactionHandler {
    OperationResult handleTransaction(ATM atm, String cardNumber, String pin, BigDecimal amount);
}
