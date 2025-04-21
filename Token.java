public class Token {
    public final TokenType type;//The type of this token
    public final String text;//text of the token as it appears in the code

    // Constructor: Creates a new token with the given type and text.
    public Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }
    /*
     toString Returns a string version of the token, so we can easily see
     what it is while debugging.
     for example:"Token(IDENT, x)" or "Token(NUMBER, 42)".
     */
    @Override
    public String toString() {
        return "Token(" + type + ", " + text + ")";
    }
}
