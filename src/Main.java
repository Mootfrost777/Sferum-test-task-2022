// It is my first time coding on java. I hope it is not too hard...

import org.apache.commons.codec.digest.DigestUtils;

import java.sql.SQLException;

import java.util.Scanner;
import java.lang.*;

public class Main {
    private static User user;  // User object. Can be instantiated without Register method.
    private final static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        conn.Conn();  // Initialize database.
        conn.CreateDB();

        Auth();  // Authenticate user. Optional.
        MainLoop();  // Main app loop.
    }

    private static void Auth() throws SQLException {
        System.out.println("""
                       Commands:
                       register <username> <password> <balance> - register new user
                       login <username> <password> - login
                        """);

        loop: while (true) {
            String command = sc.nextLine();

            String[] Args = command.split(" ");

            switch (Args[0]) {
                case "register" -> {
                    if (Args.length != 4) {
                        System.out.println("Wrong number of arguments.");
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
                case "exit" -> {
                    System.exit(0);
                }
                default -> System.out.println("Invalid command");
            }
        }
    }

    private static void MainLoop() throws SQLException {
        Help();

        while (true) {
            String command = sc.nextLine();
            String[] Args = command.toLowerCase().trim().split(" ");
            for (int i = 0; i < Args.length; i++) {
                Args[i] = Args[i].replace("\"", "");
            }

            switch (Args[0]) {
                case "print_balance" -> {
                    System.out.println("Your balance: " + user.balance);
                }
                case "show_books_in_stock" -> {
                    for (Book book : conn.GetAllBooks()) {
                        System.out.println("\"" + book.title + "\", " + book.quantity + " pieces, " + book.price + " rub.");
                    }
                }
                case "buy" -> {
                    if (Args.length != 3) {
                        System.out.println("no deal");
                        break;
                    }

                    Book RequestedBook = conn.GetBook(Args[1]);

                    if (RequestedBook == null ||
                            RequestedBook.quantity < Integer.parseInt(Args[2]) ||
                            user.balance < RequestedBook.price * Integer.parseInt(Args[2])) {
                        System.out.println("no deal");
                    }
                    else {
                        user.balance -= RequestedBook.price * Integer.parseInt(Args[2]);
                        RequestedBook.quantity -= Integer.parseInt(Args[2]);
                        user.boughtBooks.add(RequestedBook);

                        conn.UpdateUser(user);
                        conn.UpdateBook(RequestedBook);

                        System.out.println("deal");
                    }
                }
                case "show_bought_books" -> {
                    for (Book book : conn.GetUser(user.id).boughtBooks) {
                        System.out.println("\"" + book.title + "\", " + book.quantity + " pieces.");
                    }
                }
                case "add_books" -> {
                    String book;
                    System.out.println("Enter books \"<name>\", <quantity>, <price>: ");

                    while (true){
                        book = sc.nextLine();

                        if (book.equals("0")) {
                            break;
                        }

                        String[] bookInfo = book.split(", ");

                        bookInfo[0] = bookInfo[0].replace("\"", "");
                        try {
                            conn.AddBook(new Book(bookInfo[0], Integer.parseInt(bookInfo[1]), Integer.parseInt(bookInfo[2])));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid book info.");
                        }
                    }
                }
                case "top_up" -> {
                    try {
                        user.balance += Integer.parseInt(Args[1]);
                        conn.UpdateUser(user);
                    } catch (Exception e) {
                        System.out.println("Invalid amount.");
                    }
                }
                case "help" -> {
                    Help();
                }
                case "exit" -> {
                    conn.Close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid command.");
            }
        }
    }

    private static void Help() {
        System.out.println("""
                Commands:
                print_balance - print your balance
                show_books_in_stock - show books in stock
                buy "<name>" <quantity> - buy book
                show_bought_books - show bought books
                top_up <amount> - top up your balance
                add_books - add books to stock
                help - show commands list
                exit - exit
                """);
    }
}