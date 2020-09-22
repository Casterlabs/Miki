package co.casterlabs.miki.templating.variables;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Scanner;

import co.casterlabs.miki.templating.MikiTemplatingException;
import lombok.ToString;

@ToString(callSuper = true)
public class MikiFileVariable extends MikiVariable {

    public MikiFileVariable(String key, String name) {
        super(key, name);
    }

    @SuppressWarnings("unused")
    @Override
    public String evaluate(Map<String, String> variables) throws MikiTemplatingException {
        if (this.getName().contains("://")) {
            try {
                URL url = new URL(this.name);
                Scanner scanner = new Scanner(url.openStream());
                StringBuffer sb = new StringBuffer();
                
                while (scanner.hasNext()) {
                    sb.append(scanner.next());
                }

                scanner.close();
                
                return sb.toString();
            } catch (Exception e) {
                throw new MikiTemplatingException("Unable to read URL", e);
            }
        } else {
            try {
                byte[] bytes = Files.readAllBytes(new File(this.name).toPath());

                return new String(bytes);
            } catch (Exception e) {
                throw new MikiTemplatingException("Unable to read File", e);
            }
        }
    }

}
