package co.casterlabs.miki.templating.variables;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import co.casterlabs.miki.Miki;
import co.casterlabs.miki.templating.MikiTemplatingException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import lombok.ToString;
import xyz.e3ndr.fastloggingframework.logging.FastLogger;

@ToString(callSuper = true)
@SuppressWarnings({
        "restriction",
        "deprecation"
})
public class MikiScriptVariable extends MikiVariable {
    private static final NashornScriptEngineFactory factory = new jdk.nashorn.api.scripting.NashornScriptEngineFactory();
    private static final List<String> mikiInject = new ArrayList<>();
    private static final FastLogger logger = new FastLogger("MikiScripting");
    private static final String[] mikiPoly = new String[] {
            "document.js",
            "console.js"
    };

    static {
        try {
            for (String polyName : mikiPoly) {
                if (Miki.ideEnviroment) {
                    File file = new File("src/main/resources/scripting", polyName);
                    String poly = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

                    mikiInject.add(poly);
                } else {
                    InputStream in = Miki.class.getClassLoader().getResourceAsStream("scripting/" + polyName);
                    String poly = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

                    mikiInject.add(poly);
                }
            }

            Map<String, String> options = new HashMap<>();

            options.put("version", Miki.VERSION);
            options.put("parent", "java");
            options.put("scriptProvider", "OracleNashorn");
            options.put("parentVersion", System.getProperty("java.version"));

            if (System.getProperty("java.version").contains("1.8")) {
                options.put("scriptCompliance", "es6-partial");
            } else {
                options.put("scriptCompliance", "es6");
            }

            mikiInject.add(String.format("const Miki = %s;", getVariableObject(options)));

            mikiInject.add("print = undefined;");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    // A reeeeeeeeeeeeeeeaaaaaaaaaaaallllllllllllllyyyyyy bad messaging system, but it works!
                    StringBuilder line = new StringBuilder();

                    for (int i = off; i != len + off; i++) {
                        line.append(cbuf[i]);
                    }

                    if (line.charAt(0) == '!') {
                        String message = line.substring(2);

                        switch (line.charAt(1)) {
                            case 'I':
                                logger.info(message);
                                return;

                            case 'W':
                                logger.warn(message);
                                return;

                            case 'S':
                                logger.severe(message);
                                return;

                            case 'D':
                                logger.debug(message);
                                return;
                        }
                        result.append(line);
                    } else if (line.charAt(0) == '#') {
                        result.append(line.substring(1));
                    } else {
                        result.append(line);
                    }
                }

            });

            mikiInject.forEach((poly) -> {
                try {
                    engine.eval(poly);
                } catch (ScriptException ignored) {
                    ignored.printStackTrace();
                }
            });

            engine.eval("const variables = " + getVariableObject(variables) + ";");
            engine.eval("const globals = " + getVariableObject(globals) + ";");

            engine.eval(this.name);

            return result.substring(0, result.length() - 2); // There's a random CRLF... Thanks Nashorn!
        } catch (Exception e) {
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
