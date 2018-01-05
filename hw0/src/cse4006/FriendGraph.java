/*
 * The hanyang univ.
 * Copyright (c) 2017 Sujong Lee.
 * All rights reserved.
 */

package cse4006;

/**
 * FriendGraph class
 * 
 * @version hw0 10 Sept. 2017
 * @author SuJong Lee
 */
public class FriendGraph {
    private int personNum = 0;
    private final int MAX_VERTEX = 100;
    private Person[] personArray;
    private int[][] friendshipGraph;

    /**
     * FriendGraph class constructor
     * 
     * allocate and initiate array variables
     */
    FriendGraph() {
        // allocate
        friendshipGraph = new int[MAX_VERTEX][MAX_VERTEX];
        personArray = new Person[MAX_VERTEX];

        // initiate
        for (int i = 0; i < MAX_VERTEX; i++) {
            for (int j = 0; j < MAX_VERTEX; j++) {
                friendshipGraph[i][j] = 0;
            }
        }
    }

    /**
     * @param p Person object you want to add
     */
    public void addPerson(Person p) {
        // check if Person array is full
        if (personNum >= MAX_VERTEX) {
            System.out.println("[Error] Person is full");
            return;
        }
        
        // check if Person name is unique
        if (findPerson(p.getName()) != -1) {
            System.out.println("[Error] Person is exist");
            return;
        }

        // add person
        personArray[personNum] = p;
        personNum++;
    }

    /**
     * @param p name you want to find
     * @return index of name p from Person[]
     *         if no exist, return -1
     */
    public int findPerson(String p) {
        // find name p
        for (int i = 0; i < personNum; i++) {
            if (personArray[i].getName().equals(p))
                return i;
        }

        //no exist
        return -1;
    }
    
    /**
     * add friendship to two-dimensional array(graph)
     * 
     * @param p1 person's name you want to add friendship
     * @param p2 person's name you want to add friendship
     */
    public void addFriendship(String p1, String p2) {
        // index of person p1 and p2
        int p1Idx = findPerson(p1);
        int p2Idx = findPerson(p2);

        // no Person p1 or p2
        if (p1Idx == -1 || p2Idx == -1) {
            System.out.println("[Error] Wrong input name");
            return;
        }

        // p1 == p2
        if (p1Idx == p2Idx) {
            System.out.println("[Warning] No self loop");
            return;
        }

        // add friendship
        friendshipGraph[p1Idx][p2Idx] = 1;
        friendshipGraph[p2Idx][p1Idx] = 1;
    }
    
    /**
     * get shortest friendship distance
     * 
     * @param p1 person's name you want to calculate friendship distance - from
     * @param p2 person's name you want to calculate friendship distance - to
     * @return friendship distance
     */
    public int getDistance(String p1, String p2) {
        // index of person p1 and p2
        int p1Idx = findPerson(p1);
        int p2Idx = findPerson(p2);

        // store the copy of graph
        int[][] matrix = new int[personNum][personNum];

        // queue using array
        int[] queue = new int[personNum];
        int front = 0, rear = 0;
        
		// the index of current person connected from p1
		int cur_p = 0;

        // shortest friendship distance calculated from this function
        int distance = 0;

        // no person p1 or p2
        if (p1Idx == -1 || p2Idx == -1) {
            System.out.println("[Error] Wrong input name");
            return -1;
        }

        // if p1 == p2, distance is 0
        if (p1Idx == p2Idx)
            return distance;

        // copy graph
        for (int i = 0; i < personNum; i++) {
            for (int j = 0; j < personNum; j++)
                matrix[i][j] = friendshipGraph[i][j];
        }

        // calculate distance from p1 to p2 using queue and BFS
        queue[rear] = p1Idx;
        rear++;
        while (front < rear) {
            cur_p = queue[front];
            front++;
            distance++;

            for (int i = 0; i < personNum; i++) {
                if (matrix[cur_p][i] == 1) {
                    // success to find p2
                    if (p2Idx == i)
                        return distance;

                    // add connected person
                    queue[rear] = i;
                    rear++;
                    matrix[cur_p][i] = matrix[i][cur_p] = 0;
                }
            }
        }

        // no relationship between p1 and p2
        return -1;
    }
}
