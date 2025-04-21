import java.util.ArrayList;
import java.util.List;

/**
 what does lexer class do?
 Lexer: Converts raw source string into a stream of tokens.

 It recognizes keywords (if, else, while, print), operators (+, -, *, etc.),
 identifiers, numbers, colons, and so forth.
 */
class Lexer {
    private final String source;  // This takes entire code as a single string
    private final int length;     // The total size of our input
    private int current = 0;      // Index tracking our position in source
    private List<Token> tokens = new ArrayList<>(); // Accumulates identified tokens

    /**
     Construct a Lexer for the given source code.
     */
    Lexer(String source) {
        this.source = source;
        this.length = source.length();
    }

    /**
     Main method to scan through the source and build a list of tokens.
     */
    public List<Token> tokenize() {
        // We read character by character until end
        while (!isAtEnd()) {
            char c = advance();
            switch (c) {
                case '+': addToken(TokenType.PLUS, "+"); break;
                case '-': addToken(TokenType.MINUS, "-"); break;
                case '*': addToken(TokenType.STAR, "*"); break;
                case '/': addToken(TokenType.SLASH, "/"); break;
                case '%': addToken(TokenType.MOD, "%"); break;

                // Parentheses for expressions/print calls
                case '(':
                    addToken(TokenType.LPAREN, "(");
                    break;
                case ')':
                    addToken(TokenType.RPAREN, ")");
                    break;

                // '=' can be assignment or '==' compare
                case '=':
                    if (match('=')) {
                        addToken(TokenType.EQEQ, "==");
                    } else {
                        addToken(TokenType.EQ, "=");
                    }
                    break;

                // '!' might be !=
                case '!':
                    if (match('=')) {
                        addToken(TokenType.NEQ, "!=");
                    }
                    // If it's just '!', ignoring for now
                    break;

                // '>' can be > or >=
                case '>':
                    if (match('=')) {
                        addToken(TokenType.GTE, ">=");
                    } else {
                        addToken(TokenType.GT, ">");
                    }
                    break;

                // '<' can be < or <=
                case '<':
                    if (match('=')) {
                        addToken(TokenType.LTE, "<=");
                    } else {
                        addToken(TokenType.LT, "<");
                    }
                    break;

                case ':':
                    addToken(TokenType.COLON, ":");
                    break;

                // # => comment, skip until newline
                case '#':
                    while (!isAtEnd() && peek() != '\n') {
                        advance();
                    }
                    break;

                // End of line => NEWLINE token
                case '\n':
                    addToken(TokenType.NEWLINE, "\\n");
                    break;

                // Whitespace we just skip (except newline)
                case ' ':
                case '\r':
                case '\t':
                    break;

                // If it's a digit, parse a multi-digit number
                default:
                    if (isDigit(c)) {
                        number(c);
                    }
                    // If it's an alpha char, parse an identifier
                    else if (isAlpha(c)) {
                        identifier(c);
                    } else {
                        // If it's something unrecognized throws an exception
                        throw new RuntimeException("Unexpected character: " + c);
                    }
                    break;
            }
        }

        // Add an end-of-file token so the interpreter knows we're done
        addToken(TokenType.EOF, "");
        return tokens;
    }

    // Check if we've reached the end of the source
    private boolean isAtEnd() {
        return current >= length;
    }

    // Reads the current character then advances the index
    private char advance() {
        return source.charAt(current++);
    }

    // If the next character matches expected, consume it + return true
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    // Add a Token of a given type and text to our list
    private void addToken(TokenType type, String text) {
        tokens.add(new Token(type, text));
    }

    // Look at the next character but don't consume it
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    // True if the character is between '0' and '9'
    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    // Keep reading digits for multi-digit numbers
    private void number(char firstChar) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstChar);

        while (!isAtEnd() && isDigit(peek())) {
            sb.append(advance());
        }
        addToken(TokenType.NUMBER, sb.toString());
    }

    // Check if character is a letter or underscore
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c == '_');
    }

    // Check if character is alpha or digit
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    // Parse an identifier (or keyword) from the source
    private void identifier(char firstChar) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstChar);

        while (!isAtEnd() && isAlphaNumeric(peek())) {
            sb.append(advance());
        }
        String text = sb.toString();

        // If it matches a known keyword, update token type
        TokenType type = checkKeyword(text);
        addToken(type, text);
    }

    // Check for 'if', 'else', 'while', 'print' or default to IDENT
    private TokenType checkKeyword(String text) {
        switch (text) {
            case "if":    return TokenType.IF;
            case "else":  return TokenType.ELSE;
            case "while": return TokenType.WHILE;
            case "print": return TokenType.PRINT;
            default:      return TokenType.IDENT;
        }
    }
}