package com.innedhub.keystore.memento.classes;

import java.util.HashMap;
import java.util.Map;

public class Originator {
    private Map<String, String> currentStoreState = new HashMap<>();

    public Map<String, String> getCurrentStoreState() {
        return currentStoreState;
    }

    public Memento saveState() {
        return new Memento(currentStoreState);
    }

    public Memento saveModifiedState(Memento memento) {
        memento.setStoreState(currentStoreState);
        return memento;
    }

    public void restoreState(Memento memento) {
        this.currentStoreState = memento.getStoreState();
    }

    public void rememberClient(String name, String apiKey) {
        currentStoreState.put(name, apiKey);
    }

    public void removeClientByName(String name) {
        currentStoreState.remove(name);
    }

    public void removeClientByApiKey(String apiKey) {
        String name = currentStoreState
                .entrySet()
                .stream()
                .filter(entry -> apiKey.equals(entry.getValue())).toString();
        currentStoreState.remove(name);
    }
}
