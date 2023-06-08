package calculator
import java.math.BigInteger
val operatorsList = mapOf(
    '(' to 0,
    '+' to 1, '-' to 1,
    '*' to 2, '/' to 2,
    '^' to 3,
)
val ExpressionPattern =
    Regex("^(([+-])*\\d+((([+\\-])*|([\\^\\/\\*]))\\d+)*)\$")
val variables = mutableMapOf<String, BigInteger>()
val infix = mutableListOf<Any>()
fun start() {
    val input = readln().split(' ').joinToString("")
    if (input.matches(Regex("/[A-Za-z]+"))) {
        doCommand(input)
    } else if (input.contains('=')) {
        makeVariables(input)
    } else if (input.contains("\\w".toRegex())) {
        replaceVariables(input)
    } else {
        start()
    }
    start()
}

fun makeVariables(input: String) {
    val declaration = input.split("=")[0]
    val assignment = input.split("=")[1]
    val wordPattern = Regex("[a-zA-Z]+")
    if (!declaration.matches(wordPattern)) {
        println("Invalid identifier")
    } else if ("(=.*=)".toRegex().containsMatchIn(input)) {
        println("Invalid assignment")
    } else if (assignment.toBigIntegerOrNull() != null) {
        variables[declaration] = assignment.toBigInteger()
    } else if (wordPattern.matches(assignment)) {
        if (!variables.containsKey(assignment)) {
            println("Unknown variable")
        } else {
            variables[declaration] = variables[assignment]!!
        }
    } else {
        println("Invalid assignment")
    }
}

fun doCommand(input: String) {
    when (input) {
        "/exit" -> {
            throw Exception()
        }

        "/help" -> {
            println(
                """The program does the following operations:
    add variables -> n = 1
    add variables to another variables -> num = n
    reassign value of variable -> num = 2
    sum(+) -> 1 + n = 2
    subtract(-) -> 3 - num = 2
    multiplication(*) -> value = 5 * num * 9
    Integer division(/) -> value / 4 =  22
    exponentiation(^) -> 5 ^ num = 25
    group terms or specify the order of operations in an equation with (parentheses)
    mix operations -> -3 -- num +++ 3 -15 = -15
    example -> 5 * (2 + 4) - 12 / 4 = 37
        """.trimIndent()
            )
        }
        else -> {
            println("Unknown command")
            start()
        }
    }
}

fun computePlusMinus(operator1: Char, operator2: Char): Char {
    return if (operator1 == operator2) {
        '+'
    } else {
        '-'
    }
}


fun Char.isPlusOrMinus(): Boolean {
    return this == '+' || this == '-'
}

fun Char.isOperator(): Boolean {
    return this == '+' || this == '-' || this == '*' || this == '/' || this == '^' || this == '(' || this == ')'
}

fun fixLine(line: String) {
    var awryLine = line
    do {

        do {
            var first = awryLine[0]
            val second: Char
            if (1 <= awryLine.lastIndex && awryLine[1].isPlusOrMinus() && first.isPlusOrMinus()) {
                second = awryLine[1]
                awryLine = awryLine.drop(2)
                awryLine = computePlusMinus(first, second) + awryLine
                first = awryLine[0]
            } else if (first.isPlusOrMinus()) {
                infix.add(first)
                awryLine = awryLine.removePrefix(first.toString())
                first = awryLine[0]
            } else if (first.isOperator()) {
                infix.add(first)
                awryLine = awryLine.removePrefix(first.toString())
                if (awryLine.length != 0) {
                    first = awryLine[0]
                }
            }
        } while (awryLine.length != 0 && first.isOperator())

        val numberPattern = Regex("((-?)|(\\+?))?\\d+")
        val positionNumber = numberPattern.find(awryLine)
        if (positionNumber != null) {
            val number = awryLine.substring(positionNumber.range)
            infix.add(number.toBigInteger())
            awryLine = awryLine.removePrefix(number)
        }
    } while (awryLine.length != 0)
    println(evaluateExpression(transformRPN(infix)))
}

fun evaluateExpression(expression: ArrayDeque<Any>): BigInteger {
    val stack = ArrayDeque<BigInteger>()
    while (expression.isNotEmpty()) {
        val current = expression.removeFirst()

        if (current is BigInteger) {
            stack.addLast(current)
        } else {
            val top = stack.removeLast()
            val nextTop = stack.removeLast()
            when (current) {
                '+' -> stack.addLast(nextTop + top)
                '-' -> stack.addLast(nextTop - top)
                '*' -> stack.addLast(nextTop * top)
                '/' -> stack.addLast(nextTop / top)
                '^' -> stack.addLast(nextTop.pow(top.toInt()))
            }
        }
    }
    return stack.removeLast()
}

fun replaceVariables(line: String) {
    var expression = line

    for ((k, v) in variables) {
        if (expression.contains(k)) {
            expression = expression.replace(k, v.toString())
        }
    }

    if (expression.matches(Regex("[+-]?\\d+"))) {
        println(expression)
    } else if ((expression.count { it == '(' } == expression.count { it == ')'}) &&
        expression.replace("(", "").replace(")", "").matches(ExpressionPattern)) {

        fixLine(expression)
    }  else if (Regex("([a-z]|[A-Z])+").containsMatchIn(expression)){
        println("Unknown variable")
    } else {
        println("Invalid expression")
    }
}


fun main() {
    try {
        start()
    } catch (e: Exception) {
        println("Bye")
    }
}