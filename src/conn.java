// Class to work with Sqlite.

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.io.Serializable;

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
                available integer)
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
        PreparedStatement statement = conn.prepareStatement("insert into books (title, price, available) values (?, ?, ?)");
        statement.setString(1, book.title);
        statement.setDouble(2, book.price);
        statement.setInt(3, book.availableQuantity);
        statement.executeUpdate();
    }

    public static Book GetBook(int id) throws SQLException { // Get book by id.
        stmt = conn.createStatement();
        PreparedStatement statement = conn.prepareStatement("select * from books where id = ?");
        statement.setInt(1, id);
        statement.setMaxRows(1);

        ResultSet rs = statement.executeQuery();

        return new Book(rs.getInt("id"), rs.getString("title"), rs.getDouble("price"), rs.getInt("available"));
    }

    public static List<Book> GetAllBooks() throws SQLException { // Get all books.
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from books");

        List<Book> books = new ArrayList<Book>();
        while (rs.next())
        {
            books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getDouble("price"), rs.getInt("available")));
        }

        return books;
    }

    public static void UpdateBook(Book book) throws SQLException { // Update book.
        stmt = conn.createStatement();
        PreparedStatement statement = conn.prepareStatement("update books set price = ?, available = ? where book.id = ?");
        statement.setDouble(1, book.price);  // Java is SUS, it doesn't consume arguments in execute.,=
        statement.setInt(2, book.availableQuantity);
        statement.setString(3, book.title);
        statement.executeUpdate();
    }

    public static int Register(User user) throws SQLException { // Register user.
        stmt = conn.createStatement();
        PreparedStatement statement = conn.prepareStatement("search users where username = ?");
        statement.setString(1, user.username);
        statement.setMaxRows(1);
        if (!statement.executeQuery().next()) {
            statement = conn.prepareStatement("insert into users (name, balance, password) values (?, ?, ?)");
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
        PreparedStatement statement = conn.prepareStatement("select * from users where name = ? and password = ?");
        statement.setString(1, username);
        statement.setString(2, password);
        statement.setMaxRows(1);

        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return new User(rs.getInt("id"), rs.getString("name"), rs.getDouble("balance"));
        }
        else {
            return null;
        }
    }

    public static void UpdateUser(User user) throws SQLException { // Update user.
        stmt = conn.createStatement();
        PreparedStatement statement = conn.prepareStatement("update users set balance = ?, boughtBooks = ? where users.id = ?");
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

        return new User(rs.getInt("id"), rs.getString("name"), rs.getDouble("balance"), new Gson().fromJson(rs.getString("boughtBooks"), ArrayList.class));
    }
}
