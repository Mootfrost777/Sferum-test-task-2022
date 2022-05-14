// It is my first time coding on java. I hope it is not too hard...

import org.apache.commons.codec.digest.DigestUtils;

import java.security.spec.ECField;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Scanner;
import java.lang.*;

public class Main {
    private static User user;  // User object. Can be instantiated without Register method.
    private final static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        conn.Conn();  // Initialize database.
        conn.CreateDB();

        Auth();  // Authenticate user.
        MainLoop(); // Main loop.
    }

    private static void Auth() throws SQLException {
        System.out.println("""
                       Commands:
                       register <username> <password> <repeat password> <balance> - register new user
                       login <username> <password> - login
                       add_books - add books to stock
                       exit - exit
                        """);

        loop: while (true) {
            String command = sc.nextLine();

            String[] Args = command.split(" ");

            switch (Args[0]) {
                case "register" -> {
                    if (Args.length != 5) {
                        System.out.println("Wrong number of arguments.");
                        break;
                    }
                    if (!Args[2].equals(Args[3])) {
                        System.out.println("Passwords do not match.");
                        break;
                    }

                    user = new User(Args[1], DigestUtils.sha256Hex(Args[2]), Integer.parseInt(Args[3]));
                    int id = conn.Register(user);
                    if (id != -1) {
                        System.out.println("User registered successfully.");
                        user.id = id;

                        break loop;
                    }
                    else {
                        System.out.println("User already exists.");
                    }
                }
                case "login" -> {
                    user = conn.Login(Args[1], DigestUtils.sha256Hex(Args[2]));
                    if (user != null) {
                        System.out.println("User logged in successfully.");

                        break loop;
                    }
                    else {
                        System.out.println("User not found.");
                    }
                }
                case "add_books" -> {
                    AddBooks();
                }
                case "exit" -> {
                    System.exit(0);
                }
                default -> System.out.println("I don't understand.");
            }
        }
    }
    private static void MainLoop() throws SQLException {
        System.out.println(Help());

        while (true) {
            String command = sc.nextLine();
            String[] Args = command.toLowerCase().trim().split(" ");
            for (int i = 0; i < Args.length; i++) {
                Args[i] = Args[i].replace("\"", "");
            }

            switch (Args[0]) {
                case "print_balance" -> {
                    System.out.println("balance: " + user.balance + " rub.");
                }
                case "show_books_in_stock" -> {
                    for (String book : GetInStock()) {
                        System.out.println(book);
                    }
                }
                case "buy" -> {
                    Args = command.split("\"");
                    if (Args.length != 3) {
                        System.out.println("no deal");
                    }
                    try {
                        System.out.println(BuyBook(Args[1], Integer.parseInt(Args[2].trim())));
                    }
                    catch (Exception e) {
                        System.out.println("no deal");
                    }
                }
                case "show_bought_books" -> {
                    for (String book : GetBoughtBooks()) {
                        System.out.println(book);
                    }
                }
                case "top_up" -> {
                    System.out.println(TopUp(Integer.parseInt(Args[1])));
                }
                case "help" -> {
                    System.out.println(Help());
                }
                case "logout" -> {
                    Auth();
                    System.out.println(Help());
                }
                case "exit" -> {
                    conn.Close();
                    System.exit(0);
                }
                default -> System.out.println("I don't understand.");
            }
        }
    }
    private static void AddBooks() throws SQLException {
        String book;
        System.out.println("Enter books (\"<name>\", <price>, <quantity>), press Enter if want to end: ");

        while (true){
            book = sc.nextLine();

            if (book.equals("")) {
                System.out.println("All books added.");
                break;
            }

            String[] bookInfo = book.split(", ");

            bookInfo[0] = bookInfo[0].replace("\"", "");
            try {
                conn.AddBook(new Book(bookInfo[0], Integer.parseInt(bookInfo[1]), Integer.parseInt(bookInfo[2])));
            } catch (Exception e) {
                System.out.println("Invalid book info.");
            }
        }
    }
    private static String BuyBook(String title, int quantity) throws SQLException {
        Book RequestedBook = conn.GetBook(title);

        if (RequestedBook == null ||
                RequestedBook.quantity < quantity ||
                user.balance < RequestedBook.price * quantity) {
            return "no deal";
        }
        else {
            user.balance -= RequestedBook.price * quantity;
            RequestedBook.quantity -= quantity;

            boolean IsAlreadyBought = false;
            for (Book book : user.boughtBooks) {
                if (book.title.equals(RequestedBook.title)) {
                    book.quantity += quantity;
                    IsAlreadyBought = true;
                }
            }
            if (!IsAlreadyBought) {
                user.boughtBooks.add(new Book(RequestedBook.title, RequestedBook.price, quantity));
            }

            conn.UpdateUser(user);
            conn.UpdateBook(RequestedBook);

            return "deal";
        }
    }
    private static ArrayList<String> GetInStock() throws SQLException {
        ArrayList<String> Books = new ArrayList<>();
        for (Book book : conn.GetAllBooks()) {
            Books.add("\"" + book.title + "\", " + book.quantity + " pieces, " + book.price + " rub.");
        }
        return Books;
    }
    private static ArrayList<String> GetBoughtBooks() throws SQLException {
        ArrayList<String> Books = new ArrayList<>();
        for (Book book : conn.GetUser(user.id).boughtBooks) {
            Books.add("\"" + book.title + "\", " + book.quantity + " pieces.");
        }
        return Books;
    }
    private static String TopUp(int sum) throws SQLException {
        try {
            user.balance += sum;
            conn.UpdateUser(user);
            return "Your balance is " + user.balance + " rub.";
        } catch (Exception e) {
            return "Invalid amount.";
        }
    }
    private static String Help() {
        return """
                Commands:
                print_balance - print your balance
                show_books_in_stock - show books in stock
                buy "<name>" <quantity> - buy book
                show_bought_books - show bought books
                top_up <amount> - top up your balance
                help - show commands list
                logout - logout
                exit - exit
                """;
    }
}