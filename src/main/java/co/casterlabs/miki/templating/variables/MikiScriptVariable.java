package co.casterlabs.miki.templating.variables;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import co.casterlabs.miki.Miki;
import co.casterlabs.miki.templating.MikiTemplatingException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import lombok.ToString;

@ToString(callSuper = true)
@SuppressWarnings("restriction")
public class MikiScriptVariable extends MikiVariable {
    private static final NashornScriptEngineFactory factory = new jdk.nashorn.api.scripting.NashornScriptEngineFactory();
    private static final String miki;

    static {
        Map<String, String> options = new HashMap<>();

        options.put("version", Miki.VERSION);
        options.put("scriptProvider", "Nashorn");
        options.put("javaVersion", System.getProperty("java.version"));

        if (System.getProperty("java.version").contains("1.8")) {
            options.put("scriptVersion", "es6-partial");
        } else {
            options.put("scriptVersion", "es6");
        }

        miki = getVariableObject(options);
    }

    @Override
    public String evaluate(Map<String, String> variables, Map<String, String> globals) throws MikiTemplatingException {
        try {
            ScriptEngine engine = factory.getScriptEngine("--language=es6");
            StringBuilder result = new StringBuilder();

            engine.getContext().setWriter(new Writer() {

                @Override
                public void close() throws IOException {}

                @Override
                public void flush() throws IOException {}

                @Override
                public void write(char[] cbuf, int off, int len) throws IOException {
                    for (int i = off; i != len + off; i++) {
                        result.append(cbuf[i]);
                    }
                }

            });

            engine.eval("const Miki = " + miki + ";");
            engine.eval("const variables = " + getVariableObject(variables) + ";");
            engine.eval("const globals = " + getVariableObject(globals) + ";");

            engine.eval(this.name);

            return result.toString();
        } catch (ScriptException e) {
            throw new MikiTemplatingException("Cannot evaluate script", e);
        }
    }

    // Hacky way to set variables, I know.
    private static String getVariableObject(Map<String, String> variables) {
        StringBuilder scriptVariables = new StringBuilder();

        scriptVariables.append(' ');

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            scriptVariables.append('"').append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }

        return String.format("{%s\n}", scriptVariables.substring(0, scriptVariables.length() - 1));
    }

}
