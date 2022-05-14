// Encapsulates user data.

import java.util.ArrayList;

public class User {
    public int id;
    public String username;
    public String password;
    public double balance;

    public ArrayList<Book> boughtBooks;

    public User(int id, String name, double balance) {
        this.id = id;
        this.username = name;
        this.balance = balance;
        boughtBooks = new ArrayList<>();
    }

    public User(String name, String password, double balance) {
        this.username = name;
        this.balance = balance;
        this.password = password;
        boughtBooks = new ArrayList<>();
    }

    public User(int id, String name, double balance, ArrayList<Book> boughtBooks) {
        this.id = id;
        this.username = name;
        this.balance = balance;
        this.boughtBooks = boughtBooks;
    }
}
