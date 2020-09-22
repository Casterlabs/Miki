package co.casterlabs.miki.templating;

public class MikiTemplatingException extends Exception {
    private static final long serialVersionUID = 1803816489061783719L;

    public MikiTemplatingException(String message) {
        super(message);
    }

    public MikiTemplatingException(String message, Exception e) {
        super(message, e);
    }

}
