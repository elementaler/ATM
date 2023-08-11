import java.math.BigDecimal;

public class DepositTransactionHandler implements ATMTransactionHandler {
    @Override
    public OperationResult handleTransaction(ATM atm, String cardNumber, String pin, BigDecimal amount) {
        Card card = atm.getCardByNumber(cardNumber);
        if (card != null) {
            if (card.isBlocked()) {
                return new OperationResult(false, "Карта заблокирована. Вы можете возобновить обслуживание через 24 часа");
            } else {
                return new OperationResult(true, "Текущий баланс: ₽" + card.getBalance());
            }
        } else {
            return new OperationResult(false, "Неверный номер карты.");
        }


    }
}
