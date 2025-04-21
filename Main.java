import java.util.List;

public class Main {
    /**
     * Main class: Entry point for testing our Python-like interpreter.
     *
     * We feed various code snippets to our lexer + interpreter and
     * observe the results in the console.
     */
    public static void main(String[] args) {
        // 1) Basic arithmetic snippet
        String snippet1 = ""
                + "x = 5\n"
                + "y = 10\n"
                + "z = x + y\n"
                + "print(z)\n"
                + "z = z * 2\n"
                + "print(z)\n";
        runSnippet("Basic arithmetic", snippet1);

        // 2) Sum of First N Numbers (N=10)
        String sumOfN = ""
                + "N = 10\n"
                + "sum = 0\n"
                + "i = 1\n"
                + "while i <= N:\n"
                + "    sum = sum + i\n"
                + "    i = i + 1\n"
                + "print(sum)\n";
        runSnippet("Sum of N = 10", sumOfN);

        // 3) Factorial of N (N=5)
        String factorial = ""
                + "N = 5\n"
                + "fact = 1\n"
                + "i = 1\n"
                + "while i <= N:\n"
                + "    fact = fact * i\n"
                + "    i = i + 1\n"
                + "print(fact)\n";
        runSnippet("Factorial of N=5", factorial);

        // 4) GCD of two numbers (48, 18)
        String gcd = ""
                + "a = 48\n"
                + "b = 18\n"
                + "while b != 0:\n"
                + "    temp = b\n"
                + "    b = a % b\n"
                + "    a = temp\n"
                + "print(a)\n";
        runSnippet("GCD(48,18)", gcd);

        // 5) Reverse a number (1234 => 4321)
        String reverseNumber = ""
                + "n = 1234\n"
                + "rev = 0\n"
                + "while n > 0:\n"
                + "    digit = n % 10\n"
                + "    rev = rev * 10 + digit\n"
                + "    n = n / 10\n"
                + "print(rev)\n";
        runSnippet("Reverse Number 1234", reverseNumber);


        // ---------- NEW TESTS ----------

        // 5. Check if a Number is Prime (N=13 => let's print 1 for True, 0 for False)
        String primeTest = ""
                + "N = 13\n"
                + "i = 2\n"
                + "flag = 1\n"
                + "while i < N:\n"
                + "    if N % i == 0:\n"
                + "        flag = 0\n"
                + "    i = i + 1\n"
                + "if flag == 1:\n"
                + "    print(1)\n"
                + "else:\n"
                + "    print(0)\n";
        runSnippet("Check if 13 is Prime", primeTest);

        // 6. Check if a Number is Palindrome (121 => 1 for True, else 0)
        String palindromeTest = ""
                + "n = 121\n"
                + "temp = n\n"
                + "rev = 0\n"
                + "while temp > 0:\n"
                + "    digit = temp % 10\n"
                + "    rev = rev * 10 + digit\n"
                + "    temp = temp / 10\n"
                + "if rev == n:\n"
                + "    print(1)\n"
                + "else:\n"
                + "    print(0)\n";
        runSnippet("Palindrome check of 121", palindromeTest);

        // 7. Find the Largest Digit in a Number (3947 => 9)
        String largestDigit = ""
                + "n = 3947\n"
                + "largest = 0\n"
                + "while n > 0:\n"
                + "    digit = n % 10\n"
                + "    if digit > largest:\n"
                + "        largest = digit\n"
                + "    n = n / 10\n"
                + "print(largest)\n";
        runSnippet("Largest digit in 3947", largestDigit);

        // 8. Sum of Digits (1234 => 10)
        String sumOfDigits = ""
                + "n = 1234\n"
                + "sum = 0\n"
                + "while n > 0:\n"
                + "    digit = n % 10\n"
                + "    sum = sum + digit\n"
                + "    n = n / 10\n"
                + "print(sum)\n";
        runSnippet("Sum of digits in 1234", sumOfDigits);

        // 9. Multiplication Table (5 => 5,10,15,20,...,50)
        String multiplicationTable = ""
                + "N = 5\n"
                + "i = 1\n"
                + "while i <= 10:\n"
                + "    print(N * i)\n"
                + "    i = i + 1\n";
        runSnippet("Multiplication table of 5", multiplicationTable);

        // 10. Nth Fibonacci Number (N=10 => 34)
        // We'll do an iterative approach because recursive is not implemented in interpreter and is not in our task
        String nthFibonacci = ""
                + "N = 10\n"
                + "if N == 0:\n"
                + "    print(0)\n"
                + "else:\n"
                + "    a = 0\n"
                + "    b = 1\n"
                + "    i = 2\n"
                + "    while i <= N:\n"
                + "        c = a + b\n"
                + "        a = b\n"
                + "        b = c\n"
                + "        i = i + 1\n"
                + "    print(b)\n";
        runSnippet("10th Fibonacci number", nthFibonacci);
    }

    private static void runSnippet(String title, String sourceCode) {
        System.out.println("----- " + title + " -----");
        System.out.println("Source Code:\n" + sourceCode);

        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.tokenize();

        // Optional debug:
        // for (Token t : tokens) {
        //    System.out.println(t);
        // }

        Interpreter interpreter = new Interpreter(tokens, sourceCode);
        interpreter.interpret();
        System.out.println();
    }
}