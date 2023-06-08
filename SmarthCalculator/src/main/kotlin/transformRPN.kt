package calculator

fun transformRPN(infix: MutableList<Any>): ArrayDeque<Any> {
    val operators = ArrayDeque<Pair<Char, Int>>()
    val postfix = ArrayDeque<Any>()
    while (infix.isNotEmpty()) {
        val current = infix.removeFirst()
        when {
            current is Number -> postfix.add(current)
            current == ')' -> {
                while (operators.lastOrNull()?.first != '(') {
                    postfix.add(operators.removeLast().first)
                }
                operators.removeLast()
            }
            current == '(' -> operators.add('(' to 0)
            operatorsList.containsKey(current) -> {
                val opt = current as Char
                while ((operators.lastOrNull()?.second ?: -1) >= operatorsList[opt]!!) {
                    postfix.add(operators.removeLast().first)
                }
                operators.addLast(Pair(opt, operatorsList.getValue(opt)))
            }
        }
    }
    while (operators.isNotEmpty()) {
        postfix.add(operators.removeLast().first)
    }
    postfix.addAll(operators)
    return postfix
}