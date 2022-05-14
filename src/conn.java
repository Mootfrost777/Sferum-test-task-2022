// Class to work with Sqlite.

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class conn {
    public static Connection conn;
    public static Statement stmt;

    public static void Conn() throws ClassNotFoundException, SQLException { // Connect to the database.
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:db.sqlite");
    }

    public static void CreateDB() throws SQLException { // Create the database.
        stmt = conn.createStatement();
        stmt.execute("""
                create table if not exists books (
                id integer primary key,
                title varchar,
                price real,
                quantity integer)
                """);

        stmt.execute("""
                create table if not exists users (
                id integer primary key,
                username varchar,
                password varchar,
                balance real,
                boughtBooks varchar)
                """);
    }

    public static void AddBook(Book book) throws SQLException { // Add book to the database.
        stmt = conn.createStatement();
        Book b = GetBook(book.title);
        if (b != null) {
            UpdateBook(new Book(book.id, book.title, book.price, book.quantity + b.quantity));
        }
        else {
            PreparedStatement statement = conn.prepareStatement("insert into books (title, price, quantity) values (?, ?, ?)");
            statement.setString(1, book.title);
            statement.setDouble(2, book.price);
            statement.setInt(3, book.quantity);
            statement.executeUpdate();
        }
    }

    public static Book GetBook(String title) throws SQLException { // Get book by id.
        stmt = conn.createStatement();
        PreparedStatement statement = conn.prepareStatement("select * from books where title = ?");
        statement.setString(1, title);
        statement.setMaxRows(1);

        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return new Book(rs.getInt("id"), rs.getString("title"), rs.getDouble("price"), rs.getInt("quantity"));
        }
        return null;
    }

    public static List<Book> GetAllBooks() throws SQLException { // Get all books.
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from books");

        List<Book> books = new ArrayList<Book>();
        while (rs.next())
        {
            books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getDouble("price"), rs.getInt("quantity")));
        }

        return books;
    }

    public static void UpdateBook(Book book) throws SQLException { // Update book.
        stmt = conn.createStatement();
        PreparedStatement statement = conn.prepareStatement("update books set price = ?, quantity = ? where title = ?");
        statement.setDouble(1, book.price);  // Java is SUS, it doesn't consume arguments in execute.,=
        statement.setInt(2, book.quantity);
        statement.setString(3, book.title);
        statement.executeUpdate();
    }

    public static int Register(User user) throws SQLException { // Register user.
        stmt = conn.createStatement();
        PreparedStatement statement = conn.prepareStatement("select * from users where username = ?");
        statement.setString(1, user.username);
        statement.setMaxRows(1);
        if (!statement.executeQuery().next()) {
            statement = conn.prepareStatement("insert into users (username, balance, password) values (?, ?, ?)");
            statement.setString(1, user.username);
            statement.setDouble(2, user.balance);
            statement.setString(3, user.password);
            statement.executeUpdate();

            statement = conn.prepareStatement("select id from users where username = ?");
            statement.setString(1, user.username);
            return statement.executeQuery().getInt("id");
        }
        return -1;
    }

    public static User Login(String username, String password) throws SQLException { // Login user.
        stmt = conn.createStatement();
        PreparedStatement statement = conn.prepareStatement("select * from users where username = ? and password = ?");
        statement.setString(1, username);
        statement.setString(2, password);
        statement.setMaxRows(1);

        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            Type listType = new TypeToken<List<Book>>() {}.getType();
            return new User(rs.getInt("id"), rs.getString("username"), rs.getDouble("balance"), new Gson().fromJson(rs.getString("boughtBooks"), listType));
        }
        else {
            return null;
        }
    }

    public static void UpdateUser(User user) throws SQLException { // Update user.
        stmt = conn.createStatement();
        PreparedStatement statement = conn.prepareStatement("update users set balance = ?, boughtBooks = ? where username = ?");
        statement.setDouble(1, user.balance);
        statement.setString(2, new Gson().toJson(user.boughtBooks));
        statement.setString(3, user.username);
        statement.executeUpdate();
    }

    public static User GetUser(int id) throws SQLException { // Get user.
        stmt = conn.createStatement();
        PreparedStatement statement = conn.prepareStatement("select * from users where id = ?");
        statement.setInt(1, id);
        statement.setMaxRows(1);

        ResultSet rs = statement.executeQuery();
        Type listType = new TypeToken<List<Book>>() {}.getType();
        return new User(rs.getInt("id"), rs.getString("username"), rs.getDouble("balance"), new Gson().fromJson(rs.getString("boughtBooks"), listType));
    }

    public static void Close() throws SQLException { // Close connection.
        conn.close();
    }
}
