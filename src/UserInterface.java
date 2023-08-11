import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;
/*
    Класс отвечает за взаимодействие с пользователем через консольное меню.
 */
public class UserInterface {
    private ATM atm;
    private Scanner scanner;

    public UserInterface(ATM atm) {
        this.atm = atm;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Добро пожаловать в банкомат!");
        int option;
        do {
            showMenu();
            option = readOption();
            handleOption(option);
        } while (option != 4);
    }

    private void showMenu() {
        System.out.println("Выберите опцию:");
        System.out.println("1. Проверьте баланс.");
        System.out.println("2. снять средства со счета.");
        System.out.println("3. пополнить баланс.");
        System.out.println("4. выйти.");
    }

    private int readOption() {
        int option = -1; // Значение для обработки ошибок
        while (option < 0) {
            try {
                System.out.print("Введите номер опции: ");
                option = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Неверный ввод.\nПожалуйста, введите действительный номер опции.");
                scanner.nextLine(); // Очистка буфера ввода
            }
        }
        return option;
    }

    private void handleOption(int option) {
        switch (option) {
            case 1:
                handleCheckBalanceOption();
                break;
            case 2:
                handleWithdrawOption();
                break;
            case 3:
                handleDepositOption();
                break;
            case 4:
                atm.saveCardsToFile("cards.txt");
                System.exit(0);
                break;
            default:
                System.out.println("Недопустимая опция");
        }
    }

    private void handleCheckBalanceOption() {
        handleTransactionOption(new CheckBalanceTransactionHandler(), "check balance");
    }

    private void handleWithdrawOption() {
        handleTransactionOption(new WithdrawTransactionHandler(), "withdraw");
    }

    private void handleDepositOption() {
        handleTransactionOption(new DepositTransactionHandler(), "deposit");
    }

    private void handleTransactionOption(ATMTransactionHandler transactionHandler, String action) {
        String cardNumber = promptInput("Введите номер карты: ");
        if (atm.validateCardNumber(cardNumber)) {
            int pinAttempts = 0;
            while (pinAttempts < 3) {
                String pin = promptInput("Введите PIN-код: ");
                if (atm.validatePIN(cardNumber, pin)) {
                    BigDecimal amount = BigDecimal.ZERO; // Для операции проверки баланса
                    if (!action.equals("check balance")) {
                        amount = new BigDecimal(promptInput("Введите сумму " + action + ": "));
                    }
                    OperationResult result = transactionHandler.handleTransaction(atm, cardNumber, pin, amount);
                    if (result.isSuccess()) {
                        showSuccess(result.getMessage());
                    } else {
                        showError(result.getMessage());
                    }
                    break;
                } else {
                    pinAttempts++;
                }
            }
            if (pinAttempts >= 3) {
                Card card = atm.getCardByNumber(cardNumber);
                if (card != null) {
                    card.blockCard();
                    showError("Карта заблокирована из-за 3 неудачных попыток ввода PIN-кода.");
                    System.exit(0);
                }
            }
        }
    }

    private String promptInput(String prompt) {
        System.out.print(prompt + " ");
        return scanner.next();
    }

    private void showError(String message) {
        System.out.println("Ошибка: " + message);
    }

    private void showSuccess(String message) {
        System.out.println("Успех: " + message);
    }

    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.loadCardsFromFile("cards.txt");
        UserInterface userInterface = new UserInterface(atm);
        userInterface.start();
    }
}
