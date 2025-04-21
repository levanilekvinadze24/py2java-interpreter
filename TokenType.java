enum TokenType {
    // Single-character tokens
    PLUS, MINUS, STAR, SLASH, MOD, LPAREN, RPAREN, EQ, COLON,

    // One or two character tokens for comparisons
    GT, GTE, LT, LTE, EQEQ, NEQ,

    // Literals
    IDENT, NUMBER,

    // Keywords
    IF, ELSE, WHILE, PRINT,

    // Utility
    NEWLINE, //this will be needed as we need new logic for loops
    // We'll add a newline token to help detect indentation
    EOF
}
/*
explanation for each one to understand what each is for:
Single-character tokens
        PLUS -> +, MINUS -> -, STAR -> *, SLASH -> /, MOD -> %,
        LPAREN -> (, RPAREN -> ), LBRACE -> {, RBRACE _> }, EQ -> =, COLON -> :

One or two character tokens for comparisons
        GT -> >, GTE -> >=, LT -> <, LTE -> <=, EQEQ -> ==, NEQ -> !=

Literals
        IDENT ->name for variable, NUMBER -> number,

Keywords
        IF, ELSE, WHILE, PRINT, (I think this is understandable)

Utility
        EOF -> end of file
 */