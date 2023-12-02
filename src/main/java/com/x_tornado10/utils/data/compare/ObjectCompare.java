package com.x_tornado10.utils.data.compare;

import java.util.*;

public class ObjectCompare {

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

    public HashMap<String, List<UUID>> compare(List<UUID> one, List<UUID> two) {

        List<UUID> added = new ArrayList<>(two);
        List<UUID> removed = new ArrayList<>(one);
        HashMap<String, List<UUID>> result = new HashMap<>();

        for (UUID uuid : one) {
            added.remove(uuid);
        }

        for (UUID uuid : two) {
            removed.remove(uuid);
        }

        result.put("added", added);
        result.put("removed", removed);
        return result;
    }

}
