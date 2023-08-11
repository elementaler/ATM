public class ATMApplication {
    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.loadCardsFromFile("cards.txt");
        UserInterface userInterface = new UserInterface(atm);
        userInterface.start();
    }
}