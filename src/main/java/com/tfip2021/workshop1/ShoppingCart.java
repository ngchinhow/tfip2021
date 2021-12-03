// Module 1 Day 1 Workshop

package com.tfip2021.workshop1;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class ShoppingCart {
    LinkedHashSet<String> cart = new LinkedHashSet<String> ();

    public void list() {
        int counter = 1;
        if (this.cart.size() == 0) {
            System.out.println("Your cart is empty");
        } else {
            for (String item : this.cart) {
                System.out.println(counter + ". " + item);
                counter++;
            }
        }
    }

    public void add(String[] items) {
        for (String item : items) {
            item = item.trim();
            boolean result = this.cart.add(item);
            if (result) {
                System.out.println(item + " added to cart");
            }
            else {
                System.out.println("You have " + item + " in your cart");
            }           
        }
    }

    public void delete(int index) {
        if (index > this.cart.size()) {
            System.out.println("Incorrect item index");
        } 
        else {
            int i = 1;
            String toBeRemovedItem = "";
            for (String item : this.cart) {
                if (i == index) {
                    toBeRemovedItem = item;
                }
                i++;
            }
            System.out.println(toBeRemovedItem + " removed from cart");
            this.cart.remove(toBeRemovedItem);            
        }
    }
    public static void main(String[] args) {
        ShoppingCart myShoppingCart = new ShoppingCart();
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
                    myShoppingCart.add(params.split(","));
                    break;
                case "list":
                    myShoppingCart.list();
                    break;
                case "delete":
                    myShoppingCart.delete(Integer.parseInt(params));
                    break;
            }
        }
        
        scan.close();
    }
}
