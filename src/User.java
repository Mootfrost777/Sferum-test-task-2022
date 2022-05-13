import java.util.List;

public class User {
    public int id;
    public String username;
    public String password;
    public double balance;

    public List<Book> boughtBooks;

    public User(int id, String name, double balance) {
        this.id = id;
        this.username = name;
        this.balance = balance;
    }

    public User(String name, String password, double balance) {
        this.username = name;
        this.balance = balance;
        this.password = password;
    }

    public User(int id, String name, double balance, List<Book> boughtBooks) {
        this.id = id;
        this.username = name;
        this.balance = balance;
        this.boughtBooks = boughtBooks;
    }
}
