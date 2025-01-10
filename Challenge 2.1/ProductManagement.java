/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.interview.use;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Cruis
 */
public class ProductManagement {

    public static void main(String[] args) {

        String product = "Heaphones";
        List<Product> list = new ArrayList<>();
        list.add(new Product("Laptop", 999.99, 5));
        list.add(new Product("Smartphone", 499.99, 10));
        list.add(new Product("Tablet", 299.99, 0));
        list.add(new Product("Smartwatch", 199.99, 3));

        totalValue(list);
        findMax(list);
        System.out.println("Does list contain " + product + "? : " + isExist(list, product));
        sortAsc(list, "price");
        sortDesc(list, "quantity");

    }

    private static void totalValue(List<Product> list) {

        Double total = 0.0;

        for (Product product : list) {
            total += product.getPrice() * product.getQuantity();
        }

        System.out.println("Total value: " + total);
    }

    private static void findMax(List<Product> list) {
        
        if(list.isEmpty()){
            throw new IllegalArgumentException("List is empty");
        }
        
        Double max = list.get(1).getPrice();
        Product product = new Product();

        for (Product temp : list) {
            if (temp.getPrice() > max) {
                max = temp.getPrice();
                product = temp;
            }
        }

        System.out.println("Most expensive item: " + product.getName());
    }

    private static boolean isExist(List<Product> list, String name) {
        boolean isExist = false;
        for (var product : list) {
            if (product.getName().equals(name)) {
                isExist = true;
            }
        }
        return isExist;
    }

    private static void sortAsc(List<Product> list, String option) {
        switch (option) {
            case "quantity":
                System.out.println("\nSort asc by quantity");
                list.sort(Comparator.comparingInt(Product::getQuantity));
                list.forEach(System.out::println);
                break;
            case "price":
                System.out.println("\nSort asc by price");
                list.sort(Comparator.comparingDouble(Product::getPrice));
                list.forEach(System.out::println);
                break;
            default:
                throw new AssertionError();
        }
    }

    private static void sortDesc(List<Product> list, String option) {
        switch (option) {
            case "quantity":
                System.out.println("\nSort desc by quantity");
                list.sort(Comparator.comparingInt(Product::getQuantity).reversed());
                list.forEach(System.out::println);
                break;
            case "price":
                System.out.println("\nSort desc by price");
                list.sort(Comparator.comparingDouble(Product::getPrice).reversed());
                list.forEach(System.out::println);
                break;
            default:
                throw new AssertionError();
        }
    }
}
