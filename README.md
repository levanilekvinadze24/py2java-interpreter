# Python-Like Interpreter in Java

This project implements a **Python-like interpreter in Java**, supporting key features such as variable assignment, arithmetic operations, conditionals, loops, and basic input/output. It serves as a learning tool and a foundation for further expanding language features.

---

## 🛠️ **Features**
- **Variable Assignment** – Assign values to variables (`x = 5`).
- **Arithmetic Operations** – Supports `+`, `-`, `*`, `/`, `%`.
- **Conditionals** – Handles `if`, `else` blocks.
- **Loops** – Supports `while` loops for iteration.
- **Basic Input/Output** – Prints results with `print()`.
- **Error Handling** – Detects syntax errors like division by zero.

---

## 🚚 **Deliverables**
1. **Language Subset Specification** – A concise document defining the subset syntax and features.
2. **Interpreter Implementation** – A working interpreter capable of parsing and executing the subset.
3. **Test Algorithms** – Example algorithms executed by the interpreter (e.g., factorial, prime check).
4. **Documentation** – A user guide for using the interpreter with sample programs.

---

## 🧩 **Key Components**
### TokenType.java
Defines token categories for the interpreter (e.g., operators, keywords, literals).

### Token.java
Represents a single token, including its type and text.

### Lexer.java
Converts the source code into a list of tokens, parsing identifiers, numbers, and operators.

### Interpreter.java
Executes the tokenized source code, supporting variables, arithmetic, conditionals, and loops.

### Main.java
Tests the interpreter with various code snippets, covering arithmetic, control flow, and algorithms.

---

## 🚦 **Execution Flow**
1. **Tokenization** – Lexer breaks the code into tokens.
2. **Parsing** – Interpreter processes tokens and executes the program.
3. **Error Handling** – Detects syntax errors like division by zero.

---

## 📊 **Efficiency & Extensibility**
- Modular design for easier updates.
- Supports adding new statements and expressions.

---

## 📸 **Screenshots**

![frommain](https://github.com/user-attachments/assets/4ad29023-0776-4e31-b619-187b2dcbb267)
![fromconsole](https://github.com/user-attachments/assets/9fbddbe0-ce2e-42a3-8da6-16217c953f66)

---

## 🎯 **Usage**
To run the interpreter:
1. Compile all Java files.
2. Run `Main.java` with your code snippet for testing.
