package com.example.compound.use_cases.gateways;

import com.example.compound.entities.Group;

import java.util.List;

public interface GroupRepositoryGateway {
    Group findById(String GUID);

    List<Group> findAll();

    // Returns a new GUID
    String save(Group group);

    void deleteById(String GUID);
}
