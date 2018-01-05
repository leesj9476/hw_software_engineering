/*
 * The hanyang univ.
 * Copyright (c) 2017 Sujong Lee.
 * All rights reserved.
 */

package cse4006;

/**
 * Person class
 * 
 * @version hw0 10 Sept. 2017
 * @author SuJong Lee
 */
public class Person {
    /** Person's name(String) */
    private String name;

    /**
     * Person class constructor
     * 
     * @param name Person's name(String)
     */
    Person(String name) {
        // call setter function
        setName(name);
    }

    /**
     * setter for Person's name
     * 
     * @param name Person's name(String)
     */
    private void setName(String name) {
        this.name = new String(name);
    }
    
    /**
     * getter for Person's name
     * 
     * @return Person's name(String)
     */
    public String getName() {
        return name;
    }
}
