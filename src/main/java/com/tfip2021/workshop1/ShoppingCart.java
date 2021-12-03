// Module 1 Day 1 Workshop

package com.tfip2021.workshop1;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class ShoppingCart {
    private String user;
    private File userDBFile;
    private String relativePath = "src\\main\\java\\com\\tfip2021\\workshop1\\";
    private LinkedHashSet<String> tempCart = new LinkedHashSet<String> ();

    public void list() {
        int counter = 1;
        if (this.tempCart.size() == 0) {
            System.out.println("Your cart is empty");
        } else {
            for (String item : this.tempCart) {
                System.out.println(counter + ". " + item);
                counter++;
            }
        }
    }

    public void add(String[] items) {
        for (String item : items) {
            item = item.trim();
            boolean result = this.tempCart.add(item);
            if (result) {
                System.out.println(item + " added to cart");
            }
            else {
                System.out.println("You have " + item + " in your cart");
            }           
        }
    }

    public void delete(int index) {
        if (index > this.tempCart.size()) {
            System.out.println("Incorrect item index");
        } 
        else {
            int i = 1;
            String toBeRemovedItem = "";
            for (String item : this.tempCart) {
                if (i == index) {
                    toBeRemovedItem = item;
                }
                i++;
            }
            System.out.println(toBeRemovedItem + " removed from cart");
            this.tempCart.remove(toBeRemovedItem);            
        }
    }

    public void login(String cart, String name) throws IOException, FileNotFoundException {
        if (this.user != null) {
            System.out.println("Another user is currently using the cart. " + 
                "Please try again later");
            return;
        }
        // create or retreive existing db cart
        String strPath = relativePath + File.separator + 
            cart + File.separator + name + ".db";
        File file = new File(strPath);
        file.getParentFile().mkdirs();
        file.createNewFile();

        // assign session to user
        this.user = name;
        this.userDBFile = file;
        // read existing items from db cart
        this.tempCart.clear();
        Scanner scan = new Scanner(file);
        int counter = 1;
        if (scan.hasNextLine()) {
            System.out.println(name + ", your cart contains the following items");
        }
        else {
            System.out.println(name + ", your cart is empty");
        }
        while (scan.hasNextLine()) {
            String item = scan.nextLine();
            this.tempCart.add(item);
            System.out.println(counter + ". " + item);
            counter++;
        }
        scan.close();
    }

    public void save() throws IOException {
        if (this.user == null) {
            System.out.println("Login before saving your cart");
            return;
        }
        FileWriter fw = new FileWriter(this.userDBFile);
        for (String item : this.tempCart) {
            fw.write(item);
            fw.write(System.lineSeparator());
        }
        fw.close();
        System.out.println("Your cart has been saved");
        this.user = null;
        this.userDBFile = null;
    }

    public void users(String cart) {
        String strPath = relativePath + File.separator + cart;
        File dir = new File(strPath);
        File[] userCarts = dir.listFiles();
        int counter = 1;
        for (File file : userCarts) {
            if (file.isFile()) {
                System.out.println(counter + ". " + file.getName().split(".")[0]);
                counter++;
            }
        }
    }
    public static void main(String[] args) throws IOException, FileNotFoundException {
        String cart;
        if (args.length == 0) {
            cart = "db";
        }
        else {
            cart = args[0];
        }        
        
        ShoppingCart shoppingCart = new ShoppingCart();
        Scanner scan = new Scanner(System.in);
        String operation = "";
        String params = "";

        System.out.println("Welcome to your shopping cart");
        while (!operation.equals("exit")) {
            System.out.print("> ");
            operation = scan.next().toLowerCase();
            params = scan.nextLine().trim();
            switch (operation) {
                case "add":
                    shoppingCart.add(params.split(","));
                    break;
                case "list":
                    shoppingCart.list();
                    break;
                case "delete":
                    shoppingCart.delete(Integer.parseInt(params));
                    break;
                case "login":
                    shoppingCart.login(cart, params);
                    break;
                case "save":
                    shoppingCart.save();
            }
        }
        
        scan.close();
    }
}
