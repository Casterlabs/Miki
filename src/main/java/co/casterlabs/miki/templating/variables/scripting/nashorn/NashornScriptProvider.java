package co.casterlabs.miki.templating.variables.scripting.nashorn;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import co.casterlabs.miki.templating.variables.scripting.ScriptProvider;
import lombok.Getter;
import xyz.e3ndr.fastloggingframework.logging.FastLogger;
import xyz.e3ndr.fastloggingframework.logging.LogLevel;

public class NashornScriptProvider implements ScriptProvider {
    private static final FastLogger logger = new FastLogger();
    private static final Gson gson = new Gson();

    private @Getter Map<String, String> headers = new HashMap<>();
    private StringBuilder result = new StringBuilder();
    private @Getter String mime = "text/plain";
    private @Getter int status = 200;
    private ScriptEngine engine;

    public NashornScriptProvider(ScriptEngine engine, String nativeHelper) throws ScriptException {
        this.engine = engine;

        this.engine.getContext().setWriter(new Writer() {

            @Override
            public void close() throws IOException {}

            @Override
            public void flush() throws IOException {}

            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                StringBuilder line = new StringBuilder();

                for (int i = off; i != len + off; i++) {
                    line.append(cbuf[i]);
                }

                try {
                    JsonObject json = gson.fromJson(line.toString(), JsonObject.class);
                    String type = json.get("type").getAsString().toLowerCase();

                    switch (type) {
                        case "log": {
                            LogLevel level = LogLevel.valueOf(json.get("level").getAsString().toUpperCase());

                            logger.log(level, getElementString(json.get("message")));
                            return;
                        }

                        case "status": {
                            status = json.get("status").getAsInt();
                            return;
                        }

                        case "mime": {
                            mime = json.get("mime").getAsString();
                            return;
                        }

                        case "header": {
                            headers.put(json.get("key").getAsString(), json.get("value").getAsString());
                            return;
                        }

                        case "print": {
                            result.append(getElementString(json.get("message")));

                            if (json.get("new_line").getAsBoolean()) {
                                result.append("\r\n"); // CRLF as this is meant for the web.
                            }
                            return;
                        }

                        default:
                            return;
                    }

                } catch (NullPointerException ignored) {} // Nashorn will randomly generate blank lines
            }

        });

        this.engine.getContext().setAttribute("__engine", this.engine, ScriptContext.ENGINE_SCOPE);

        this.engine.eval(nativeHelper);
    }

    @Override
    public void eval(String script) throws ScriptException {
        this.engine.eval(script);
    }

    @Override
    public String getResult() {
        return this.result.toString();
    }

    private static String getElementString(JsonElement element) {
        try {
            return element.getAsString();
        } catch (Exception ignored) {
            return element.toString();
        }
    }

}
