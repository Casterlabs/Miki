package co.casterlabs.miki.templating.variables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import co.casterlabs.miki.Miki;
import co.casterlabs.miki.MikiUtil;
import co.casterlabs.miki.templating.MikiTemplatingException;
import co.casterlabs.miki.templating.WebRequest;
import co.casterlabs.miki.templating.variables.scripting.ScriptProvider;
import co.casterlabs.miki.templating.variables.scripting.ScriptProviderFactory;
import co.casterlabs.miki.templating.variables.scripting.nashorn.NashornScriptProviderFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import okhttp3.Response;

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
        return this.evaluateAsWeb(variables, globals, new WebRequest()).getResult();
    }

    public ScriptProvider evaluateAsWeb(Map<String, String> variables, Map<String, String> globals, WebRequest request) throws MikiTemplatingException {
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

            // Hacky way to set variables, I know.
            provider.eval("const variables = " + getVariableObject(variables) + ";");
            provider.eval("const globals = " + getVariableObject(globals) + ";");
            provider.eval(request.getScriptLine());

            provider.eval(this.name);

            return provider;
        } catch (Exception e) {
            throw new MikiTemplatingException("Cannot evaluate script", e);
        }
    }

    public static JsonObject getVariableObject(Map<String, String> variables) {
        JsonObject json = new JsonObject();

        for (Map.Entry<String, String> variable : variables.entrySet()) {
            json.addProperty(variable.getKey(), variable.getValue());
        }

        return json;
    }

    public static String getEvaluatedHttpRequest(String method, String body, String url, Map<String, String> headers) throws IOException {
        Response response = MikiUtil.sendHttp(method, body, url, headers);
        JsonObject json = new JsonObject();
        JsonObject responseHeaders = new JsonObject();

        response.headers().forEach((pair) -> {
            responseHeaders.addProperty(pair.component1(), pair.component2());
        });

        json.addProperty("code", response.code());
        json.addProperty("body", response.body().string());
        json.add("headers", responseHeaders);

        return json.toString();
    }

    public static String getEvaluatedFormHttpRequest(String method, Map<String, String> body, String url, Map<String, String> headers) throws IOException {
        Response response = MikiUtil.sendHttpForm(method, body, url, headers);
        JsonObject json = new JsonObject();
        JsonObject responseHeaders = new JsonObject();

        response.headers().forEach((pair) -> {
            responseHeaders.addProperty(pair.component1(), pair.component2());
        });

        json.addProperty("code", response.code());
        json.addProperty("body", response.body().string());
        json.add("headers", responseHeaders);

        return json.toString();
    }

}
