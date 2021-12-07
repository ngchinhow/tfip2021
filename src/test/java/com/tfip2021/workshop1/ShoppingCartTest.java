package com.tfip2021.workshop1;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class ShoppingCartTest {
    private String cart = "db";
    private String userName = "testUser";
    private ShoppingCart sc = new ShoppingCart(cart);
    private String[] input = {"apple", "mango", "pear", "banana"};
    // Create a stream to hold the output
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private PrintStream ps = new PrintStream(baos);
    // IMPORTANT: Save the old System.out!
    private PrintStream old = System.out;

    @Test
    public void testAdd() throws FileNotFoundException, IOException {
        String[] output = new String[4];
        System.setOut(ps);

        // Test adding before logging in
        baos.reset();
        sc.add(input);
        assertEquals(
            "Login before adding to cart" + System.lineSeparator(),
            baos.toString()
        );

        // Test adding
        sc.login(userName);
        sc.clear();
        sc.add(input);
        assertArrayEquals(input, sc.getTempCart().toArray(output));

        // Clean up
        System.out.flush();
        System.setOut(old);
    }

    @Test
    public void testDelete() throws FileNotFoundException, IOException {
        String[] inputWithDeletion = {"apple", "mango", "banana"};
        String[] output = new String[3];
        System.setOut(ps);

        // Test deleting before logging in
        sc.delete(1);
        assertEquals(
            "There is no user logged in" + System.lineSeparator(),
            baos.toString()
        );

        // Test deleting from empty cart
        sc.login(userName);
        sc.clear();
        baos.reset();
        sc.delete(1);
        assertEquals(
            "Incorrect item index" + System.lineSeparator(),
            baos.toString()
        );

        // Test deleting with negative index
        baos.reset();
        sc.delete(-1);
        assertEquals(
            "Incorrect item index" + System.lineSeparator(),
            baos.toString()
        );

        // Test deleting
        sc.add(input);
        sc.delete(3);
        assertArrayEquals(inputWithDeletion, sc.getTempCart().toArray(output));

        // Clean up
        System.out.flush();
        System.setOut(old);
    }

    @Test
    public void testList() throws FileNotFoundException, IOException {
        System.setOut(ps);
        
        // Test listing before logging in
        baos.reset();
        sc.list();
        assertEquals(
            "Login before listing cart" + System.lineSeparator(),
            baos.toString()
        );

        // Test listing from TempCart (empty)
        sc.login(userName);
        sc.clear();
        baos.reset();
        sc.list();
        assertEquals(
            "Your cart is empty" + System.lineSeparator(),
            baos.toString()
        );

        // Test listing from TempCart (filled)
        sc.add(input);
        baos.reset();
        sc.list();
        assertEquals(
            "1. apple" + System.lineSeparator() +
            "2. mango" + System.lineSeparator() +
            "3. pear" + System.lineSeparator() +
            "4. banana" + System.lineSeparator(),
            baos.toString()
        );

        // Test listing from file
        sc.save();
        sc.logout();
        sc.login("testUser");
        baos.reset();
        sc.list();
        assertEquals(
            "1. apple" + System.lineSeparator() +
            "2. mango" + System.lineSeparator() +
            "3. pear" + System.lineSeparator() +
            "4. banana" + System.lineSeparator(),
            baos.toString()
        );

        // Clean up
        System.out.flush();
        System.setOut(old);
    }

    @Test
    public void testLogin() throws FileNotFoundException, IOException {
        System.setOut(ps);

        // Test logging in with empty cart
        File file = new File(sc.getAbsolutePath() + File.separator + userName + ".db");
        file.delete();
        sc.login(userName);
        assertEquals(
            userName + ", your cart is empty" + System.lineSeparator(),
            baos.toString()
        );

        // Test logging in with another user
        baos.reset();
        sc.login("testUser2");
        assertEquals(
            "Another user is currently using the cart. " +
            "Please try again later" + System.lineSeparator(),
            baos.toString()
        );

        // Test logging in with filled cart
        sc.add(input);
        sc.save();
        sc.logout();
        baos.reset();
        sc.login(userName);
        assertEquals(
            userName + ", your cart contains the following items" +
            System.lineSeparator() + 
            "1. apple" + System.lineSeparator() +
            "2. mango" + System.lineSeparator() +
            "3. pear" + System.lineSeparator() +
            "4. banana" + System.lineSeparator(),
            baos.toString()
        );

        // Clean up
        System.out.flush();
        System.setOut(old);
    }

    @Test
    public void testLogout() throws FileNotFoundException, IOException {
        System.setOut(ps);

        // Test logging out when there's no user
        sc.logout();
        assertEquals(
            "There is no user logged in" + System.lineSeparator(),
            baos.toString()
        );

        // Test logging out
        sc.login(userName);
        baos.reset();
        sc.logout();
        assertEquals(null, sc.getUser());
        assertEquals(null, sc.getUserDBFile());
        assertTrue(sc.getTempCart().isEmpty());
    }

    @Test
    public void testClear() throws IOException {
        System.setOut(ps);

        // Test clearing before logging in
        sc.clear();
        assertEquals(
            "There is no user logged in" + System.lineSeparator(),
            baos.toString()
        );

        // Test clearing after adding items and saving
        sc.login(userName);
        sc.add(input);
        sc.save();
        sc.clear();
        File userFile = sc.getUserDBFile();
        assertTrue(sc.getTempCart().isEmpty());
        assertTrue(userFile.exists() && userFile.length() == 0);

        // Clean up
        System.out.flush();
        System.setOut(old);
    }

    @Test
    public void testSave() throws IOException {
        System.setOut(ps);

        // Test saving before logging in
        sc.save();
        assertEquals(
            "Login before saving your cart" + System.lineSeparator(),
            baos.toString()
        );

        // Test saving
        sc.login(userName);
        sc.add(input);
        sc.save();
        Path filePath = Paths.get(
            sc.getAbsolutePath() + File.separator + userName + ".db"
        );
        String content = Files.readString(filePath, StandardCharsets.UTF_8);
        assertEquals(
            "apple" + System.lineSeparator() +
            "mango" + System.lineSeparator() +
            "pear" + System.lineSeparator() +
            "banana" + System.lineSeparator(),
            content
        );

        // Clean up
        System.out.flush();
        System.setOut(old);
    }

    @Test
    public void testUsers() {
        System.setOut(ps);

        // Test listing users
        sc.users();
        assertEquals(
            "1. george" + System.lineSeparator() + 
            "2. jeff" + System.lineSeparator() +
            "3. testUser" + System.lineSeparator(),
            baos.toString()
        );

        // Clean up
        System.out.flush();
        System.setOut(old);
    }
}
