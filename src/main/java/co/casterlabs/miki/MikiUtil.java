package co.casterlabs.miki;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.casterlabs.miki.parsing.MikiParsingException;
import co.casterlabs.miki.templating.MikiTemplatingException;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MikiUtil {
    private static final OkHttpClient client = new OkHttpClient();

    public static Map<String, String> parseKeys(@NonNull String input, char sign, char opening, char closing, char escape) throws MikiParsingException {
        Map<String, String> variables = new HashMap<>();
        List<Integer> signPositions = getSignPositions(input, sign, escape);

        for (int position : signPositions) {
            try {
                String variable = readFrom(position, opening, closing, escape, sign, input);

                if (variable != null) {
                    variables.put(String.format("%c%c%s%c", sign, opening, variable, closing), variable);
                }
            } catch (MikiParsingException ignored) {}
        }

        return variables;
    }

    public static String getFile(String location) throws IOException {
        byte[] bytes = Files.readAllBytes(new File(location).toPath());

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String getFromURI(String location) throws MikiTemplatingException, MalformedURLException {
        try {
            URL url = new URL(location);

            if (url.getProtocol().startsWith("file")) {
                return getFile(location.split("://")[1]);
            } else if (url.getProtocol().startsWith("http")) {
                String body;

                // Auto close.
                try (Response response = MikiUtil.sendHttpGet(url.toString())) {
                    body = response.body().string();
                }

                return body;
            } else {
                throw new UnsupportedOperationException("Unsupported scheme: " + url.getProtocol());
            }
        } catch (IOException e) {
            throw new MikiTemplatingException("Unable to read URL", e);
        }
    }

    public static Map<String, String> lowercaseMap(@NonNull Map<String, String> input) {
        Map<String, String> result = new HashMap<String, String>() {
            private static final long serialVersionUID = 9190028015798081580L;

            @Override
            public String get(Object key) {
                return super.get(((String) key).toLowerCase());
            }

            @Override
            public String getOrDefault(Object key, String def) {
                return super.getOrDefault(((String) key).toLowerCase(), def);
            }

            @Override
            public String put(String key, String value) {
                return super.put(key.toLowerCase(), value);
            }
        };

        result.putAll(input);

        return result;
    }

    public static Response sendHttpGet(String url) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        Request request = builder.build();

        Response response = client.newCall(request).execute();

        return response;
    }

    public static List<Integer> getSignPositions(@NonNull String str, char sign, char escape) {
        List<Integer> positions = new ArrayList<Integer>();
        char[] chars = str.toCharArray();

        for (int i = 0; i != chars.length; i++) {
            if ((chars[i] == sign) && (chars[i + 1] != escape)) {
                positions.add(i);
            }
        }

        return positions;
    }

    public static String escapeString(@NonNull String str) {
        for (MikiSymbol symbol : MikiSymbol.values()) {
            str = str.replace(String.valueOf(symbol.getSign()), symbol.getSign() + String.valueOf(Miki.ESCAPE));
        }

        return str;
    }

    public static String unescapeString(@NonNull String str) {
        for (MikiSymbol symbol : MikiSymbol.values()) {
            str = str.replace(symbol.getSign() + String.valueOf(Miki.ESCAPE), String.valueOf(symbol.getSign()));
        }

        return str;
    }

    public static String readFrom(int start, char opening, char closing, char escape, char sign, @NonNull String str) throws MikiParsingException {
        StringBuilder sb = new StringBuilder();
        char[] chars = str.concat(" ").toCharArray();
        boolean escaped = false;

        if (chars[start + 1] == escape) {
            return null;
        } else if (chars[start + 1] != opening) {
            throw new MikiParsingException("Invalid sign at position: " + (start + 1));
        }

        for (int i = start + 1; i != (chars.length - 1); i++) {
            char current = chars[i];

            escaped = chars[i + 1] == escape;

            if (!escaped) {
                if (current == closing) {
                    return sb.toString();
                } else if (current == opening) {
                    continue;
                }
            }

            if (!escaped && (current == sign)) {
                throw new MikiParsingException("Came across another sign that was unescaped at: " + i);
            }

            if ((current != escape) || escaped) {
                sb.append(current);
            }
        }

        throw new MikiParsingException("Reached the end of the template with an unterminated variable.");
    }

}
