import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * if you are reviewing our project
 * what is an interpreter?
 * Interpreter: takes a list of tokens (from our Lexer) and executes
 * Python-like statements (if, while, print, etc.) in a simplified manner.
 */
public class Interpreter {

    // We'll store lines with indentation info in this list
    private final List<Line> lines;
    private int currentLine = 0; // which line we're on

    // Our "variables" map: var name => integer value
    private final Map<String, Integer> variables = new HashMap<>();

    /**
     Line class: wraps tokens and their indent level
     */
    static class Line {
        final List<Token> tokens;
        final int indent;

        Line(List<Token> tokens, int indent) {
            this.tokens = tokens;
            this.indent = indent;
        }
    }

    /**
     so we Construct an Interpreter with a list of tokens + original source.
     We split tokens into lines by NEWLINE, attach indentation info,
     then interpret line by line.
     */
    public Interpreter(List<Token> tokens, String originalSource) {
        this.lines = splitIntoIndentedLines(tokens, originalSource);
    }

    /**
     this reads lines until done.
     */
    public void interpret() {
        while (!isAtEnd()) {
            interpretLine(getLine());
        }
    }

    /**
     Break tokens into lines, read indentation from the original source lines,
     and build a "Line" object for each row of code.
     */
    private List<Line> splitIntoIndentedLines(List<Token> tokens, String source) {
        // Split source by actual newlines
        String[] sourceLines = source.split("\n", -1);

        // Count leading spaces on each line
        int[] indentLevels = new int[sourceLines.length];
        for (int i = 0; i < sourceLines.length; i++) {
            indentLevels[i] = countLeadingSpaces(sourceLines[i]);
        }

        List<Line> lineList = new ArrayList<>();
        int lineIndex = 0;
        List<Token> currentLineTokens = new ArrayList<>();

        // Walk through all tokens, grouping them until we see NEWLINE
        for (Token tk : tokens) {
            if (tk.type == TokenType.NEWLINE) {
                // That ends one physical line
                lineList.add(new Line(currentLineTokens, indentLevels[lineIndex]));
                currentLineTokens = new ArrayList<>();
                lineIndex++;
            } else if (tk.type == TokenType.EOF) {
                // End of file: add leftover tokens if any
                if (!currentLineTokens.isEmpty()) {
                    lineList.add(new Line(currentLineTokens, indentLevels[lineIndex]));
                }
            } else {
                // Otherwise, keep collecting tokens for this line
                currentLineTokens.add(tk);
            }
        }

        // If we ended without a final NEWLINE, add those tokens anyway
        if (!currentLineTokens.isEmpty()) {
            if (lineIndex < indentLevels.length) {
                lineList.add(new Line(currentLineTokens, indentLevels[lineIndex]));
            } else {
                // fallback if mismatch
                lineList.add(new Line(currentLineTokens, 0));
            }
        }
        return lineList;
    }

    // Count how many spaces at the beginning of a line
    private int countLeadingSpaces(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') count++;
            else break;
        }
        return count;
    }

    /**
     Reads + interprets a single "Line" (which might be an assignment, if, while, etc.)
     */
    private void interpretLine(Line line) {
        if (line.tokens.isEmpty()) {
            // Empty line => skip
            advanceLine();
            return;
        }

        Token first = line.tokens.get(0);

        switch (first.type) {
            case IDENT:
                parseAssignment(line);
                break;
            case PRINT:
                parsePrint(line);
                break;
            case IF:
                parseIf(line);
                break;
            case WHILE:
                parseWhile(line);
                break;
            case ELSE:
                // If we hit else by itself, skip to avoid double prints
                parseElse(line);
                break;
            default:
                // Unrecognized line => skip
                advanceLine();
                break;
        }
    }

    private void parseAssignment(Line line) {
        if (line.tokens.size() < 3) {
            advanceLine();
            return;
        }
        String varName = line.tokens.get(0).text;
        Token eq = line.tokens.get(1);
        if (eq.type != TokenType.EQ) {
            advanceLine();
            return;
        }

        // Grab everything after '=' as the expression
        List<Token> exprTokens = line.tokens.subList(2, line.tokens.size());
        int value = evaluateExpression(exprTokens);
        variables.put(varName, value);
        advanceLine();
    }

    /**
     We skip optional parentheses around the expression if present -> e.g.: print(z) or print z
     */
    private void parsePrint(Line line) {
        if (line.tokens.size() < 2) {
            advanceLine();
            return;
        }

        // We'll slice off the tokens after 'print'
        int startIndex = 1;
        int endIndex = line.tokens.size();

        // If the next token is LPAREN, skip it
        if (line.tokens.get(startIndex).type == TokenType.LPAREN) {
            startIndex++;
        }
        // If the last token is RPAREN, skip it
        if (line.tokens.get(endIndex - 1).type == TokenType.RPAREN) {
            endIndex--;
        }

        // Evaluate whatever remains
        if (startIndex >= endIndex) {
            advanceLine();
            return;
        }
        List<Token> exprTokens = line.tokens.subList(startIndex, endIndex);

        int value = evaluateExpression(exprTokens);
        System.out.println(value);
        advanceLine();
    }

    /**
     so it does something like this
     if expr:
     [block if expr != 0]
     else:
     [block if expr == 0]
     */
    private void parseIf(Line line) {
        int colonIndex = findColon(line.tokens);
        if (colonIndex < 0) {
            advanceLine();
            return;
        }

        // Condition tokens => everything after 'if' up to ':'
        List<Token> conditionTokens = line.tokens.subList(1, colonIndex);
        int conditionValue = evaluateExpression(conditionTokens);

        // Indent level of the if line, for block detection
        int myIndent = line.indent;
        advanceLine();

        // Decide if we run or skip the block
        boolean ifExecuted = false;
        if (conditionValue != 0) {
            interpretBlock(myIndent);
            ifExecuted = true;
        } else {
            skipBlock(myIndent);
        }

        // Then we check if there's an 'else' right after, at the same indent
        if (!isAtEnd()) {
            Line nextLine = getLine();
            if (!nextLine.tokens.isEmpty() &&
                    nextLine.tokens.get(0).type == TokenType.ELSE &&
                    nextLine.indent == myIndent)
            {
                parseElseInternal(nextLine, ifExecuted);
            }
        }
    }

    /**
     If we encounter 'else' unexpectedly, we skip it
     because the real logic is handled in parseElseInternal.
     */
    private void parseElse(Line line) {
        advanceLine();
        skipBlock(line.indent);
    }

    /**
     Called right after an 'if' to interpret or skip the else block.
     */
    private void parseElseInternal(Line line, boolean ifExecuted) {
        // Consume the 'else' line
        advanceLine();

        // If the 'if' block already ran, skip else
        if (ifExecuted) {
            skipBlock(line.indent);
        }
        // Otherwise, run the else block
        else {
            interpretBlock(line.indent);
        }
    }

    /**
     while expr:
     [block repeated if expr != 0]
     */
    private void parseWhile(Line line) {
        int colonIndex = findColon(line.tokens);
        if (colonIndex < 0) {
            advanceLine();
            return;
        }

        List<Token> conditionTokens = line.tokens.subList(1, colonIndex);
        int myIndent = line.indent;
        int startLine = currentLine;

        // Evaluate once, then interpret block if condition != 0
        int conditionValue = evaluateExpression(conditionTokens);
        advanceLine();

        while (conditionValue != 0) {
            interpretBlock(myIndent);

            // Jump back to recheck
            resetTo(startLine);
            Line whileLine = getLine();
            conditionTokens = whileLine.tokens.subList(1, findColon(whileLine.tokens));
            conditionValue = evaluateExpression(conditionTokens);
            advanceLine();
        }

        // If false, skip block once
        skipBlock(myIndent);
    }

    /**
     Finds the position of the first ':' in a list of tokens.
     Returns -1 if not found.
     */
    private int findColon(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).type == TokenType.COLON) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Interpret lines with indentation strictly greater than 'baseIndent'
     * (meaning they belong to the current block).
     */
    private void interpretBlock(int baseIndent) {
        while (!isAtEnd()) {
            Line ln = getLine();
            if (ln.indent <= baseIndent) {
                // The block ends
                return;
            }
            interpretLine(ln);
        }
    }

    /**
     * Skip lines (like a block) until we find a line with indent <= baseIndent.
     */
    private void skipBlock(int baseIndent) {
        while (!isAtEnd()) {
            Line ln = getLine();
            if (ln.indent <= baseIndent) {
                return;
            }
            advanceLine();
        }
    }

    // ---------------------------------
    // Expression Evaluator
    // ---------------------------------

    /**
     Evaluate a simple left-to-right expression with
     +, -, *, /, %, and comparisons (==, !=, <, etc.).
     */
    private int evaluateExpression(List<Token> exprTokens) {
        if (exprTokens.isEmpty()) return 0;

        // Start with the first value
        int value = getSingleValue(exprTokens.get(0));
        int i = 1;

        // Then fold in the rest as "value op nextValue"
        while (i < exprTokens.size()) {
            Token op = exprTokens.get(i);
            if (isOperator(op.type)) {
                if (i + 1 >= exprTokens.size()) {
                    throw new RuntimeException("Operator at end with no operand.");
                }
                int rightVal = getSingleValue(exprTokens.get(i + 1));
                switch (op.type) {
                    case PLUS:  value += rightVal; break;
                    case MINUS: value -= rightVal; break;
                    case STAR:  value *= rightVal; break;
                    case SLASH:
                        if (rightVal == 0) {
                            throw new RuntimeException("Division by zero.");
                        }
                        value /= rightVal;
                        break;
                    case MOD:
                        if (rightVal == 0) {
                            throw new RuntimeException("Modulo by zero.");
                        }
                        value %= rightVal;
                        break;
                    case EQEQ:  value = (value == rightVal) ? 1 : 0; break;
                    case NEQ:   value = (value != rightVal) ? 1 : 0; break;
                    case GT:    value = (value >  rightVal) ? 1 : 0; break;
                    case GTE:   value = (value >= rightVal) ? 1 : 0; break;
                    case LT:    value = (value <  rightVal) ? 1 : 0; break;
                    case LTE:   value = (value <= rightVal) ? 1 : 0; break;
                    default:
                        break;
                }
                i += 2;
            } else {
                // If it's not an operator, just move on
                i++;
            }
        }
        return value;
    }

    /**
     Convert a token to an integer value:
     - NUMBER => parseInt
     - IDENT => fetch from variables map or 0 if undefined
     */
    private int getSingleValue(Token token) {
        switch (token.type) {
            case NUMBER:
                return Integer.parseInt(token.text);
            case IDENT:
                return variables.getOrDefault(token.text, 0);
            default:
                throw new RuntimeException("Unexpected token in expression: " + token);
        }
    }

    // Check if token type is an operator or comparison
    private boolean isOperator(TokenType t) {
        switch (t) {
            case PLUS: case MINUS: case STAR: case SLASH: case MOD:
            case EQEQ: case NEQ: case GT: case GTE: case LT: case LTE:
                return true;
            default:
                return false;
        }
    }

    // ---------------------------------
    // Helpers for line-based parsing
    // ---------------------------------

    // True if we've consumed all lines
    private boolean isAtEnd() {
        return currentLine >= lines.size();
    }

    // Get the current line object
    private Line getLine() {
        return lines.get(currentLine);
    }

    // Move to the next line
    private void advanceLine() {
        currentLine++;
    }

    // Reset the line pointer (used in while loops to re-check conditions)
    private void resetTo(int lineIndex) {
        currentLine = lineIndex;
    }
}