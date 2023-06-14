package com.x_tornado10.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HashMapCompare {

    public HashMap<String, HashMap<UUID, Long>> compare(HashMap<UUID, Long> one, HashMap<UUID, Long> two) {

        HashMap<UUID, Long> added = new HashMap<>(two);
        HashMap<UUID, Long> removed = new HashMap<>(one);
        HashMap<String, HashMap<UUID, Long>> result = new HashMap<>();

        for (Map.Entry<UUID, Long> entry : one.entrySet()) {
            added.remove(entry.getKey());
        }

        for (Map.Entry<UUID, Long> entry : two.entrySet()) {
            removed.remove(entry.getKey());
        }

        result.put("added", added);
        result.put("removed", removed);
        return result;
    }

}
