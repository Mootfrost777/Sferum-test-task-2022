// It is my first time coding on java. I hope it is not too hard...

import org.apache.commons.codec.digest.DigestUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.lang.System;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        conn.Conn();  // Initialize database.
        conn.CreateDB();

        Scanner sc = new Scanner(System.in);

        User user;

        System.out.println("""
                       Commands:
                       register - register new user
                       login - login
                        """);

        loop: while (true) {
            String command = sc.nextLine();

            switch (command) {
                case "register" -> {
                    System.out.println("Enter your name: ");
                    String name = sc.nextLine();

                    System.out.println("Enter your password: ");
                    String password = DigestUtils.sha256Hex(sc.nextLine()); // Hash password.

                    System.out.println("Enter your balance: ");
                    double balance = sc.nextDouble();

                    user = new User(name, password, balance);
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
                    System.out.println("Enter your name: ");
                    String name = sc.nextLine();

                    System.out.println("Enter your password: ");
                    String password = DigestUtils.sha256Hex(sc.nextLine()); // Hash password.

                    user = conn.Login(name, password);
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

        while (true) {
            System.out.println("""
                Commands:
                print balance - print your balance
                show books in stock - show books in stock
                buy - buy book
                show bought books - show bought books
                add books - add books to stock
                exit - exit
                """);

            String command = sc.nextLine();

            switch (command) {
                case "print balance" -> {
                    System.out.println("Your balance: " + user.balance);
                }
                case "show books in stock" -> {
                    for (Book book : conn.GetAllBooks()) {
                        System.out.println(book.toString());
                    }
                }
                case "buy" -> {

                }
                case "show bought books" -> {
                    for (Book book : conn.GetUser(user.id).boughtBooks) {
                        System.out.println(book.toString());
                    }
                }
                case "add books" -> {
                    String book;
                    do {
                        book = sc.nextLine();
                        String[] bookInfo = book.split(", ");

                        bookInfo[0] = bookInfo[0].replace("\"", "");

                        conn.AddBook(new Book(bookInfo[0], Integer.parseInt(bookInfo[1]), Integer.parseInt(bookInfo[2])))    ;

                    } while (!Objects.equals(book, "0"));
                }
                case "exit" -> {
                    System.exit(0);
                }
                default -> System.out.println("Invalid command!");
            }
        }
    }
}