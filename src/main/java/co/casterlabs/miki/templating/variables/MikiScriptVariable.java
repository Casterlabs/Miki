package co.casterlabs.miki.templating.variables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import co.casterlabs.miki.Miki;
import co.casterlabs.miki.MikiUtil;
import co.casterlabs.miki.templating.MikiTemplatingException;
import co.casterlabs.miki.templating.variables.scripting.ScriptProvider;
import co.casterlabs.miki.templating.variables.scripting.ScriptProviderFactory;
import co.casterlabs.miki.templating.variables.scripting.nashorn.NashornScriptProviderFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString(callSuper = true)
public class MikiScriptVariable extends MikiVariable {
    private static @NonNull @Getter ScriptProviderFactory scriptProviderFactory = new NashornScriptProviderFactory();

    private static final List<String> mikiInject = new ArrayList<>();
    private static String mikiOptions;
    private static final String[] mikiPoly = new String[] {
            "miki/scriptpoly/document.js",
            "miki/scriptpoly/console.js",
            "miki/scriptpoly/io.js",
            "miki/scriptpoly/store.js"
    };

    static {
        try {
            for (String polyName : mikiPoly) {
                mikiInject.add(MikiUtil.loadInternalFile(polyName));
            }

            setScriptProviderFactory(new NashornScriptProviderFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setScriptProviderFactory(ScriptProviderFactory factory) {
        JsonObject options = new JsonObject();
        JsonArray limitations = new JsonArray();

        for (String limitation : factory.getLimitations()) {
            limitations.add(limitation);
        }

        options.addProperty("version", Miki.VERSION);
        options.addProperty("parent", "java");
        options.addProperty("scriptProvider", factory.getName());
        options.addProperty("parentVersion", System.getProperty("java.version"));
        options.addProperty("scriptCompliance", factory.getCompliance());
        options.add("limitations", limitations);

        mikiOptions = String.format("const Miki = %s;", options.toString());
    }

    @Override
    public MikiVariable init(String key, String name) throws MikiTemplatingException {
        this.key = key;
        this.name = MikiUtil.unescapeString(name);

        if (this.name.startsWith("file://")) {
            this.name = MikiUtil.getFromURI(this.name);
        }

        return this;
    }

    @Override
    public String evaluate(Map<String, String> variables, Map<String, String> globals) throws MikiTemplatingException {
        try {
            ScriptProvider provider = scriptProviderFactory.newInstance();

            provider.eval(mikiOptions);

            mikiInject.forEach((poly) -> {
                try {
                    provider.eval(poly);
                } catch (ScriptException ignored) {
                    ignored.printStackTrace();
                }
            });

            provider.eval("const variables = " + getVariableObject(variables) + ";");
            provider.eval("const globals = " + getVariableObject(globals) + ";");

            provider.eval(this.name);

            return provider.getResult();
        } catch (Exception e) {
            throw new MikiTemplatingException("Cannot evaluate script", e);
        }
    }

    // Hacky way to set variables, I know.
    public static String getVariableObject(Map<String, String> variables) {
        StringBuilder scriptVariables = new StringBuilder();

        scriptVariables.append(' ');

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            scriptVariables.append('"').append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }

        return String.format("{%s\n}", scriptVariables.substring(0, scriptVariables.length() - 1));
    }

}
