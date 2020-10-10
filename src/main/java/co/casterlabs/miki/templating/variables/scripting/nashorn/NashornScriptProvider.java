package co.casterlabs.miki.templating.variables.scripting.nashorn;

import java.io.IOException;
import java.io.Writer;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import co.casterlabs.miki.templating.variables.scripting.ScriptProvider;
import xyz.e3ndr.fastloggingframework.logging.FastLogger;
import xyz.e3ndr.fastloggingframework.logging.LogLevel;

public class NashornScriptProvider implements ScriptProvider {
    private static final FastLogger logger = new FastLogger();
    private static final Gson gson = new Gson();

    private StringBuilder result = new StringBuilder();
    private ScriptEngine engine;

    public NashornScriptProvider(ScriptEngine engine, String nativeHelper) throws ScriptException {
        this.engine = engine;

        engine.getContext().setWriter(new Writer() {

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

                    if (json.get("type").getAsString().equalsIgnoreCase("log")) {
                        LogLevel level = LogLevel.valueOf(json.get("level").getAsString().toUpperCase());

                        logger.log(level, getElementString(json.get("message")));
                    } else {
                        result.append(getElementString(json.get("message")));

                        if (json.get("new_line").getAsBoolean()) {
                            result.append('\n');
                        }
                    }
                } catch (NullPointerException ignored) {} // Nashorn will randomly generate blank lines
            }

        });

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
