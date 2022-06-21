package com.yamangulov.results;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class PropertyTOImpl implements PropertyTO{
    Map<String, String> options;

    @Override
    public void addSingleOption(String key, String value) {
        this.options.put(key, value);
    }

    @Override
    public String getSingleOption(String key) {
        return this.options.get(key);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("New object PropertyTO: ").append(super.toString()).append("\n");
        Set<Map.Entry<String, String>> entries = options.entrySet();
        for (Map.Entry<String, String> entry: entries) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        builder.append("End object PropertyTO: ").append(super.toString()).append("\n");
        return builder.toString();
    }
}
