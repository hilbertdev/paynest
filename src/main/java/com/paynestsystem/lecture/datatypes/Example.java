package com.paynestsystem.lecture.datatypes;

public class Example {

    public static void main(String[] args) {

        ExperimentWithInts();
       /*
        * String name = "John";
        * String referenceToJohn = name;
        * name+= " Doe";
        * System.out.println("Hello, " + name + "!");
        */
    }

    public static void ExperimentWithInts() {
        int a = 10;
        int b = a;
        a++;
        b = 20;
        System.out.println("a: " + a + ", b: " + b);
    }
}
