/*
 * The hanyang univ.
 * Copyright (c) 2017 Sujong Lee.
 * All rights reserved.
 */

package cse4006;

/**
 * Main class
 * 
 * @version hw0 10 Sept. 2017
 * @author SuJong Lee
 */
public class Main {
    /**
     * main function(program entry point)
     * describe all actions
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // create FriendGraph object
        FriendGraph graph = new FriendGraph();

        // create Person objects
        Person john = new Person("John");
        Person tom = new Person("Tom");
        Person jane = new Person("Jane");
        Person marry = new Person("Marry");

        // add persons
        graph.addPerson(john);
        graph.addPerson(tom);
        graph.addPerson(jane);
        graph.addPerson(marry);

        // add relationship
        graph.addFriendship("John", "Tom");
        graph.addFriendship("Tom", "Jane");

        // print results
        System.out.println(graph.getDistance("John", "Tom"));
        System.out.println(graph.getDistance("John", "Jane"));
        System.out.println(graph.getDistance("John", "John"));
        System.out.println(graph.getDistance("John", "Marry"));
    }
}
