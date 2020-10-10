package co.casterlabs.miki.templating;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WebResponse {
    private @Setter(AccessLevel.NONE) Map<String, String> headers = new HashMap<>();
    private @NonNull String result = "";
    private @Nullable String mime;
    private int status = 200;

}
