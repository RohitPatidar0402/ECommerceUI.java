import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

class ECommerceUI {
    static Map<Integer, Product> productDB = new HashMap<>();
    static List<Product> cart = new ArrayList<>();
    static List<Order> orders = new ArrayList<>();
    static int productIdCounter = 1;
    static String currentUser = "";
    static String role = ""; // "admin" or "user"

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> showLoginScreen());
    }

    static void showLoginScreen() {
        String[] options = {"Admin", "User"};
        int choice = JOptionPane.showOptionDialog(null, "Login as:", "Login",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            role = "admin";
            currentUser = "";
            showAdminMenu();
        } else if (choice == 1) {
            role = "user";
            currentUser = JOptionPane.showInputDialog("Enter your name:");
            if (currentUser != null && !currentUser.isBlank()) {
                showUserMenu();
            } else {
                showLoginScreen();  // Retry login
            }
        } else {
            System.exit(0); // User closed login
        }
    }

    static void showAdminMenu() {
        String[] options = {"Add Product", "View Products", "View Orders", "Back to Login", "Exit"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(null, "Admin Menu", "Admin",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0 -> addProduct();
                case 1 -> viewProducts();
                case 2 -> viewAllOrders();
                case 3 -> showLoginScreen();
                case 4 -> System.exit(0);
                default -> { return; }
            }
        }
    }

    static void showUserMenu() {
        String[] options = {"View Products", "Add to Cart", "Delete from Cart", "View Cart", "Place Order", "My Orders", "Back to Login", "Exit"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(null, "User Menu", "User",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0 -> viewProducts();
                case 1 -> addToCart();
                case 2 -> deleteFromCart();
                case 3 -> viewCart();
                case 4 -> placeOrder(currentUser);
                case 5 -> viewUserOrders(currentUser);
                case 6 -> showLoginScreen();
                case 7 -> System.exit(0);
                default -> { return; }
            }
        }
    }

    static void addProduct() {
        String title = JOptionPane.showInputDialog("Enter product title:");
        String priceStr = JOptionPane.showInputDialog("Enter product price:");
        String desc = JOptionPane.showInputDialog("Enter product description:");
        String imageUrl = JOptionPane.showInputDialog("Enter product image URL:");

        try {
            double price = Double.parseDouble(priceStr);
            Product p = new Product(productIdCounter, title, price, desc, imageUrl);
            productDB.put(productIdCounter++, p);
            JOptionPane.showMessageDialog(null, "Product added!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid input.");
        }
    }

    static void viewProducts() {
        if (productDB.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No products available.");
            return;
        }
        StringBuilder sb = new StringBuilder("Product List:\n");
        for (Product p : productDB.values()) {
            sb.append(p.toString()).append("\n---------------------\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(null, scroll);
    }

    static void addToCart() {
        String idStr = JOptionPane.showInputDialog("Enter Product ID to add:");
        try {
            int id = Integer.parseInt(idStr);
            Product p = productDB.get(id);
            if (p != null) {
                cart.add(p);
                JOptionPane.showMessageDialog(null, "Added to cart: " + p.title);
            } else {
                JOptionPane.showMessageDialog(null, "Product not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid ID.");
        }
    }

    static void deleteFromCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Cart is empty.");
            return;
        }
        String idStr = JOptionPane.showInputDialog("Enter Product ID to remove from cart:");
        try {
            int id = Integer.parseInt(idStr);
            boolean removed = cart.removeIf(p -> p.id == id);
            if (removed) {
                JOptionPane.showMessageDialog(null, "Removed from cart.");
            } else {
                JOptionPane.showMessageDialog(null, "Product not found in cart.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid ID.");
        }
    }

    static void viewCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Cart is empty.");
            return;
        }
        StringBuilder sb = new StringBuilder("Your Cart:\n");
        double total = 0;
        for (Product p : cart) {
            sb.append(p.toString()).append("\n---------------------\n");
            total += p.price;
        }
        sb.append(String.format("Total: $%.2f", total));
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(null, scroll);
    }

    static void placeOrder(String customerName) {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Cart is empty. Cannot place order.");
            return;
        }
        Order order = new Order(new ArrayList<>(cart), customerName);
        orders.add(order);
        cart.clear();
        JOptionPane.showMessageDialog(null, "Order placed!");
    }

    static void viewAllOrders() {
        if (orders.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No orders placed.");
            return;
        }
        StringBuilder sb = new StringBuilder("All Orders:\n");
        for (Order o : orders) {
            sb.append(o.toString()).append("\n==================\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(null, scroll);
    }

    static void viewUserOrders(String customerName) {
        List<Order> userOrders = orders.stream()
                .filter(o -> o.customerName.equalsIgnoreCase(customerName))
                .toList();

        if (userOrders.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You have no orders.");
            return;
        }

        StringBuilder sb = new StringBuilder("Your Orders:\n");
        for (Order o : userOrders) {
            sb.append(o.toString()).append("\n==================\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(null, scroll);
    }

    // === Inner Classes ===

    static class Product {
        int id;
        String title;
        double price;
        String description;
        String imageUrl;

        Product(int id, String title, double price, String desc, String imageUrl) {
            this.id = id;
            this.title = title;
            this.price = price;
            this.description = desc;
            this.imageUrl = imageUrl;
        }

        public String toString() {
            return String.format("ID: %d\nTitle: %s\nPrice: $%.2f\nDescription: %s\nImage: %s", id, title, price, description, imageUrl);
        }
    }

    static class Order {
        List<Product> products;
        String customerName;

        Order(List<Product> products, String name) {
            this.products = products;
            this.customerName = name;
        }

        public double calculateTotal() {
            return products.stream().mapToDouble(p -> p.price).sum();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("Customer: " + customerName + "\n");
            for (Product p : products) {
                sb.append(p.toString()).append("\n-----------------\n");
            }
            sb.append(String.format("Total: $%.2f", calculateTotal()));
            return sb.toString();
        }
    }
}