package com.m.hyderabadmarketingapp.user_module.models;

public class Person {
    private String name;
    private String email;

    public Person(String n, String e) { name = n; email = e; }

    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public String toString() { return name; }
}