package com.x_tornado10.craftiservi.utils.custom_data.reload;

import java.util.HashMap;
import java.util.List;

public class CustomData {
    private List<String> s;
    private List<Boolean> b;
    private List<Integer> i;
    private List<Double> d;
    private List<List<String>> lS;
    private HashMap<String, Integer> hashMap;



    public CustomData(List<String> s, List<Boolean> b, List<Integer> i, List<Double> d, List<List<String>> lS) {
        this.s = s;
        this.b = b;
        this.i = i;
        this.d = d;
        this.lS = lS;
    }
    public CustomData(List<String> s, List<Boolean> b, List<Integer> i, List<Double> d, List<List<String>> lS, HashMap<String, Integer> hashMap) {
        this.s = s;
        this.b = b;
        this.i = i;
        this.d = d;
        this.lS = lS;
        this.hashMap = hashMap;
    }

    public List<String> getS() {
        return s;
    }

    public List<Boolean> getB() {
        return b;
    }

    public List<Integer> getI() {
        return i;
    }

    public List<Double> getD() {
        return d;
    }

    public List<List<String>> getlS() {
        return lS;
    }

    public String getS(int index) {
        return s.get(index);
    }

    public boolean getB(int index) {
        return b.get(index);
    }

    public int getI(int index) {
        return i.get(index);
    }

    public double getD(int index) {
        return d.get(index);
    }

    public List<String> getLS(int index) {
        return lS.get(index);
    }

    public HashMap<String, Integer> getHashMap() {
        return hashMap;
    }
}
