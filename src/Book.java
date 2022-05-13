public class Book
{
    public int id;
    public String title;
    public double price;
    public int availableQuantity;

    public Book(String title, double price, int availableQuantity) {
        this.title = title;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    public Book(int id, String title, double price, int availableQuantity) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }
}
