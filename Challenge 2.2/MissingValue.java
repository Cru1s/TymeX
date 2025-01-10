/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.interview.use;

/**
 *
 * @author Cruis
 */
public class MissingValue {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int nums[] = {3,7,1,2,6,4}; //n = 6
        findMissingValue(nums.length, nums);
    }

    private static void findMissingValue(int n, int[] nums) {

        int missing = 0;
        for (int i = 0; i <= n; i++) {
            if (i == n) {
                missing += i + 1;
                break;
            }
            missing = missing + i + 1 - nums[i];
        }

        System.out.println("Missing value: " + missing);
    }
}
