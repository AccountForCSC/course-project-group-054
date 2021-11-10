package com.example.compound.debugging;

import com.example.compound.entities.User;
import com.example.compound.use_cases.UserManager;

public class Debugging {
    public static void main(String[] args) {
        UserManager manager = new UserManager();
        User user = manager.getUser("rohan.tinna@mail.utoronto.ca");
        System.out.println(user);
    }
}
