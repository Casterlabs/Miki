package co.casterlabs.miki.json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import co.casterlabs.miki.Miki;
import co.casterlabs.miki.MikiUtil;
import co.casterlabs.miki.parsing.MikiParsingException;
import co.casterlabs.miki.templating.MikiTemplate;
import co.casterlabs.miki.templating.MikiTemplatingException;
import co.casterlabs.miki.templating.WebRequest;
import co.casterlabs.miki.templating.WebResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class MikiFileAdapter {
    private static final Gson GSON = new Gson();

    private List<ConfigVariable> variables = new ArrayList<>();
    private MikiTemplate miki;

    @SerializedName("template")
    private String templateRaw = "";

    @SerializedName("template_location")
    private String templateLocation;

    @SerializedName("template_file")
    private String templateFile;

    public String format(@NonNull Map<String, String> globals) throws MikiTemplatingException {
        return this.formatAsWeb(globals, new WebRequest()).getResult();
    }

    public WebResponse formatAsWeb(@NonNull Map<String, String> globals, @NonNull WebRequest request) throws MikiTemplatingException {
        Map<String, String> variables = new HashMap<>();

        for (ConfigVariable variable : this.variables) {
            if (variable.value.contains("://")) {
                variables.put(variable.name, MikiUtil.getFromURI(variable.value));
            } else {
                variables.put(variable.name, variable.value);
            }
        }

        return this.miki.formatAsWeb(variables, globals, request);
    }

    public String format() throws MikiTemplatingException {
        return this.format(Collections.emptyMap());
    }

    public static MikiFileAdapter readFile(@NonNull File file) throws IOException, MikiTemplatingException, MikiParsingException {
        String json = new String(Files.readAllBytes(file.toPath()));
        MikiFileAdapter adapter = GSON.fromJson(json, MikiFileAdapter.class);

        if (adapter.templateFile != null) {
            adapter.templateRaw = new String(Files.readAllBytes(new File(file.getParentFile(), adapter.templateFile).toPath()));
        } else if (adapter.templateLocation != null) {
            adapter.templateRaw = MikiUtil.getFromURI(adapter.templateLocation);
        }

        adapter.miki = Miki.parse(adapter.templateRaw);

        return adapter;
    }

    private static class ConfigVariable {
        private String name;
        private String value;

    }

}
