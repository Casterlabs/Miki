package co.casterlabs.miki.templating;

import java.util.Map;

import com.google.gson.JsonObject;

import co.casterlabs.miki.templating.variables.MikiScriptVariable;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@NonNull
@ToString
@AllArgsConstructor
public class WebRequest {
    private Map<String, String> queryParameters;
    private Map<String, String> headers;
    private String hostname;
    private String method;
    private String path;
    private String body;
    private int port;

    public WebRequest() {}

    public String getScriptLine() {
        JsonObject json = new JsonObject();
        boolean isWebEnviroment = this.path != null;

        json.addProperty("isWebEnviroment", isWebEnviroment);

        if (isWebEnviroment) {
            json.addProperty("hostname", this.hostname);
            json.addProperty("method", this.method);
            json.addProperty("port", this.port);
            json.addProperty("body", this.body);
            json.add("headers", MikiScriptVariable.getVariableObject(this.headers));
            json.add("queryParameters", MikiScriptVariable.getVariableObject(this.queryParameters));
        }

        return String.format("const request = %s;", json);
    }

}
