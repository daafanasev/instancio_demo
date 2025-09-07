package org.example;

import lombok.Data;

import java.util.List;

@Data
public class Node {

    private Node node;
    private List<String> values;

}
