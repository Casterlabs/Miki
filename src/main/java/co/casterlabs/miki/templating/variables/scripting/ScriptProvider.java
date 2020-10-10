package co.casterlabs.miki.templating.variables.scripting;

import java.util.Map;

import javax.script.ScriptException;

public interface ScriptProvider {

    public void eval(String script) throws ScriptException;

    public String getMime();

    public Map<String, String> getHeaders();

    public String getResult();

    public int getStatus();

}
