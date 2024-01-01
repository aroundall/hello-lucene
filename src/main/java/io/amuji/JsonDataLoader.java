package io.amuji;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonDataLoader {
    public List<Form> load(String fileName) {
        String data;
        try {
            data = Files.readString(Path.of(Objects.requireNonNull(this.getClass().getClassLoader().getResource(fileName)).toURI()), StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return new Gson().fromJson(data, new TypeToken<ArrayList<Form>>(){}.getType());
    }
}
