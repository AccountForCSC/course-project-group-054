package com.example.compound.services;

import com.example.compound.entities.*;

import java.util.List;

public interface GroupService {
    int newGroup(String name, String description, List<String> members);
    boolean editGroup();
    List<String> getExpenses(int num);
    List<String> getUsers();
}
