package org.example;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class User {

    private UUID id;
    private String name;
    private String password;
    private String email;
    private String keyWord;
    private Integer age;
    private Address address;

    private List<StatisticLogins> statisticLoginsList;

}
