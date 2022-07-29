package edu.cmu.demo.utils;

import java.util.ArrayList;

public class Plan {
    public String name;
    public ArrayList<Double> parameters;

    public Plan(String name, ArrayList<Double> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String name() {
        return this.name;
    }

    public ArrayList<Double> parameters() {
        return this.parameters;
    }

    // todo: compare to function to compare plans
}
