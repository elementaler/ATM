import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.math.BigDecimal;
import java.math.RoundingMode;
/*
    Класс "Симулятор банкомата".
*/
public class ATM {
    private Map<String, Card> cards;
    private BigDecimal atmBalance;

    public ATM() {
        this.cards = new HashMap<>();
        this.atmBalance = new BigDecimal("1000000");
    }
    /*
     Проверяет валидность номера карты по шаблону.
     */

    public boolean validateCardNumber(String cardNumber) {
        if (!cardNumber.matches("[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}")) {
            showError("Неверный формат номера карты.");
            return false;
        }
        if (!cards.containsKey(cardNumber)) {
            showError("Неверный номер карты.");
            return false;
        }
        return true;
    }
    /*
     Проверяет корректность ПИН-кода для карты.
     */
    public boolean validatePIN(String cardNumber, String pin) {
        if (!pin.matches("[0-9]{4}")) {
            showError("Неверный формат PIN-кода.");
            return false;
        }
        Card card = cards.get(cardNumber);
        if (card != null && card.validatePIN(pin)) {
            return true;
        }
        showError("Неверный номер карты или PIN-код.");
        return false;
    }
    public Card getCardByNumber(String cardNumber) {
        return cards.get(cardNumber);
    }


    /*
     Выполняет операцию снятия денег со счета.
     */
    public OperationResult withdraw(String cardNumber, BigDecimal amount) {
        Card card = cards.get(cardNumber);
        if (card == null) {
            return new OperationResult(false, "Неверный номер карты.");
        }

        if (amount.compareTo(atmBalance) > 0) {
            return new OperationResult(false, "Баланс в банкомате недостаточен.");
        }

        if (amount.compareTo(card.getBalance()) > 0) {
            return new OperationResult(false, "Недостаточно средств.");
        }

        BigDecimal newBalance = card.getBalance().subtract(amount).setScale(2, RoundingMode.HALF_UP);
        card.setBalance(newBalance);
        atmBalance = atmBalance.subtract(amount).setScale(2, RoundingMode.HALF_UP);

        return new OperationResult(true, "Вывод средств прошел успешно.\nНовый баланс: ₽" + newBalance);
    }
    /*
     Выполняет операцию пополнения баланса карты.
     */
    public OperationResult deposit(String cardNumber, BigDecimal amount) {
        Card card = cards.get(cardNumber);
        if (card == null) {
            return new OperationResult(false, "Неверный номер карты.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new OperationResult(false, "Недействительная сумма депозита.");
        }

        BigDecimal newBalance = card.getBalance().add(amount).setScale(2, RoundingMode.HALF_UP);
        card.setBalance(newBalance);

        return new OperationResult(true, "Внесение депозита прошло успешно. Новый баланс: ₽" + newBalance);
    }
    /*
     Загружает данные карт из файла.
     */
    public void loadCardsFromFile(String filename) {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    String cardNumber = parts[0];
                    String pin = parts[1];
                    double balance = Double.parseDouble(parts[2]);
                    cards.put(cardNumber, new Card(cardNumber, pin, new BigDecimal(balance)));
                }
            }
        } catch (FileNotFoundException e) {
            showError("Не удалось загрузить карты из файла.");
        }
    }
    /*
     Сохраняет данные карт в файл.
     */
    public void saveCardsToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Card card : cards.values()) {
                writer.println(card.getCardNumber() + " " + card.getPin() + " " + card.getBalance());
            }
        } catch (IOException e) {
            showError("Не удалось сохранить карты в файл.");
        }
    }
    /*
     Выводит сообщение об ошибке на консоль.
     */
    private void showError(String message) {
        System.out.println("Ошибка: " + message);
    }
    /*
     Выводит сообщение об успешной операции на консоль.
     */
    private void showSuccess(String message) {
        System.out.println("Успех: " + message);
    }

    public static void main(String[] args) {
        ATM atm = new ATM();
        UserInterface userInterface = new UserInterface(atm);
        userInterface.start();
    }
}
