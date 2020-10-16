package co.casterlabs.miki.templating.variables.scripting.nashorn;

import java.io.IOException;

import javax.script.ScriptException;

import co.casterlabs.miki.MikiUtil;
import co.casterlabs.miki.templating.variables.scripting.ScriptProvider;
import co.casterlabs.miki.templating.variables.scripting.ScriptProviderFactory;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

@SuppressWarnings({
        "restriction"
})
public class NashornScriptProviderFactory implements ScriptProviderFactory {
    @SuppressWarnings("removal")
    private static final NashornScriptEngineFactory factory = new jdk.nashorn.api.scripting.NashornScriptEngineFactory();
    private static String nativeHelper;

    static {
        try {
            nativeHelper = MikiUtil.loadInternalFile("miki/nashorn_natives.js");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ScriptProvider newInstance() throws ScriptException {
        return new NashornScriptProvider(factory.getScriptEngine("--language=es6"), nativeHelper);
    }

    @Override
    public String getName() {
        return "OracleNashorn";
    }

    @Override
    public String getCompliance() {
        // Full ES6 support was added in Java9, Java1.8 supports some ES6 features (such as const and let)
        if (System.getProperty("java.version").contains("1.8")) {
            return "es6-partial";
        } else {
            return "es6";
        }
    }

    @Override
    public String[] getLimitations() {
        return new String[] {
                "classes",
                "undefined_variables"
        };
    }

}
