// Module 1 Day 1 Workshop

package com.tfip2021.workshop1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class ShoppingCart {
    private String user;
    private File userDBFile;
    private final String relativePath = "src\\main\\java\\com\\tfip2021\\workshop1\\";
    private String absolutePath;
    private LinkedHashSet<String> tempCart = new LinkedHashSet<String>();

    // constructor
    public ShoppingCart(String cart) {
        this.absolutePath = relativePath + File.separator + cart;
    }

    // getters
    public String getUser() { return user; }
    public File getUserDBFile() { return userDBFile; }
    public String getRelativePath() { return relativePath; }
    public String getAbsolutePath() { return absolutePath; }
    public LinkedHashSet<String> getTempCart() { return tempCart; }

    //setters
    public void setUser(String user) { this.user = user; }
    public void setUserDBFile(File userDBFile) { this.userDBFile = userDBFile; }

    public void list() {
        if (this.getUser() == null) {
            System.out.println("Login before listing cart");
            return;
        }
        int counter = 1;
        if (this.getTempCart().size() == 0) {
            System.out.println("Your cart is empty");
        } else {
            for (String item : this.getTempCart()) {
                System.out.println(counter + ". " + item);
                counter++;
            }
        }
    }

    public void add(String itemStr) {
        if (this.getUser() == null) {
            System.out.println("Login before adding to cart");
            return;
        }
        String[] items = itemStr.split(",");
        boolean result;
        for (String item : items) {
            item = item.trim();
            result = this.getTempCart().add(item);
            if (result) {
                System.out.println(item + " added to cart");
            } else {
                System.out.println("You have " + item + " in your cart");
            }
        }
    }

    public void delete(String indexStr) {
        int index = Integer.parseInt(indexStr);
        if (this.getUser() == null) {
            System.out.println("There is no user logged in");
            return;
        }
        else if (index > this.getTempCart().size() || index < 1) {
            System.out.println("Incorrect item index");
            return;
        }
        int i = 1;
        String toBeRemovedItem = "";
        for (String item : this.getTempCart()) {
            if (i == index) {
                toBeRemovedItem = item;
            }
            i++;
        }
        System.out.println(toBeRemovedItem + " removed from cart");
        this.getTempCart().remove(toBeRemovedItem);
    }

    public void login(String name) throws IOException, FileNotFoundException {
        if (this.getUser() != null) {
            System.out.println(
                "Another user is currently using the cart. " +
                "Please try again later"
            );
            return;
        }
        // create or retreive existing db cart
        String strPath = this.getAbsolutePath() + File.separator + name + ".db";
        File file = new File(strPath);
        file.getParentFile().mkdirs();
        file.createNewFile();

        // assign session to user
        this.setUser(name);
        this.setUserDBFile(file);
        // read existing items from db cart
        Scanner scan = new Scanner(file);
        String item;
        int counter = 1;
        if (scan.hasNextLine()) {
            System.out.println(name + ", your cart contains the following items");
        } else {
            System.out.println(name + ", your cart is empty");
        }
        while (scan.hasNextLine()) {
            item = scan.nextLine();
            this.getTempCart().add(item);
            System.out.println(counter + ". " + item);
            counter++;
        }
        scan.close();
    }

    public void logout() {
        if (this.getUser() == null) {
            System.out.println("There is no user logged in");
            return;
        }
        this.setUser(null);
        this.setUserDBFile(null);
        this.getTempCart().clear();
        System.out.println("You have been logged out successfully!");
    }

    public void clear() throws IOException {
        if (this.getUser() == null) {
            System.out.println("There is no user logged in");
            return;
        }
        this.getTempCart().clear();
        FileWriter fw = new FileWriter(this.getUserDBFile());
        fw.write("");
        fw.close();
    }

    public void save() throws IOException {
        if (this.getUser() == null) {
            System.out.println("Login before saving your cart");
            return;
        }
        FileWriter fw = new FileWriter(this.getUserDBFile());
        for (String item : this.getTempCart()) {
            fw.write(item);
            fw.write(System.lineSeparator());
        }
        fw.close();
        System.out.println("Your cart has been saved");
    }

    public void users() {
        File dir = new File(this.getAbsolutePath());
        File[] userCarts = dir.listFiles();
        String[] fileName;
        int counter = 1;
        for (File file : userCarts) {
            if (file.isFile()) {
                fileName = file.getName().split("\\.");
                System.out.println(counter + ". " + fileName[0]);
                counter++;
            }
        }
    }

    public static void main(String[] args) throws IOException, FileNotFoundException, NoSuchMethodException, SecurityException {
        String cart;
        if (args.length == 0) {
            cart = "db";
        } else {
            cart = args[0];
        }

        ShoppingCart shoppingCart = new ShoppingCart(cart);
        Scanner scan = new Scanner(System.in);
        String operation = "";
        String params = "";

        System.out.println("Welcome to your shopping cart");
        while (!operation.equals("exit")) {
            System.out.print("> ");
            operation = scan.next().toLowerCase();
            params = scan.nextLine().trim();
            Method method;
            try {
                if (params.equals("")) {
                    method = shoppingCart.getClass().getMethod(operation);
                    method.invoke(shoppingCart);
                } else {
                    method = shoppingCart.getClass().getMethod(operation, params.getClass());
                    method.invoke(shoppingCart, params);
                }
            } catch (NoSuchMethodException e) {
                System.out.println("Invalid operation");
            } catch (SecurityException |
                IllegalAccessException |
                IllegalArgumentException |
                InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        scan.close();
    }
}
