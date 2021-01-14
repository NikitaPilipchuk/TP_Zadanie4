fun isNumber(s:String):Boolean {
    if(s.isEmpty()){
        return false
    }
    for (symbol in s){
        if(!symbol.isDigit()){
            return false
        }
    }
    return true
}

fun prefixToInfix(op: MutableList<String>, final:Boolean = false):String {
    var result = ""
    var flag = false
    val ops = arrayOf("+","-","*","/")
    var temp: MutableList<String> = ArrayList()
    if(op.size == 3) {
        var res = "${op[1]} ${op[0]} ${op[2]}"
        return if (!final && (op[0] == "+" || op[0] == "-")) "($res)" else res
    }
    for (i in op.size-1 downTo 0) {
        //println("${op[i]} $op")
        if(i == 0) flag = true
        if(result == "" && op[i] !in ops && op[i-1] !in ops && op[i-2] !in ops) {
            temp.add(op.removeAt(i))
            continue
        }
        if(op[i] in ops) {
            if(result == "" && op[i+1] !in ops && op[i+2] !in ops) {
                result = op[i+2]
                op.removeAt(i+2)
            }
            if(i != op.lastIndex) {
                result = prefixToInfix(arrayOf(op[i], op[i + 1], result).toMutableList(), flag && final)
                op.removeAt(i+1)
            }
            else {
                result = prefixToInfix(arrayOf(op[i], result, temp.removeAt(temp.lastIndex)).toMutableList(), flag && final)
            }
            op.removeAt(i)
        }
    }
    return result
}

fun transformExpression(answer:String): String {
    val parts = Regex("""(.+?(( [0-9]+){2,}| [0-9]+${'$'}))""").findAll(answer.toString()).map { it.value.trim() }.toList()
    var resOp: MutableList<String> = ArrayList()

    for(part in parts) {
        var count_operators = Regex("""[/*\-+]""").findAll(part).count()
        val count_operands = Regex("""[0-9]+(\.[0-9]+)?""").findAll(part).count()
        var op = Regex("""\s+""").split(part).toMutableList()
        if (count_operands == count_operators) resOp.add(0,op.removeAt(0))
        if (count_operands == 1) {
            resOp.add(op.removeAt(0))
            continue
        }
        resOp.add(prefixToInfix(op, parts.size == 1))
    }
    return "${if(resOp.size == 1) resOp[0] else prefixToInfix(resOp, true)}"
}

fun checkAnswer(answer:String): Boolean {
    if(answer == "") {
        println("Вы ввели пустую строку.")
        return false
    }
    if(Regex("""([^0-9/*+. -])""").containsMatchIn(answer)) {
        println("Ошибка ввода! Выражение содержит недопустимые символы.")
        return false
    }
    var count_operators = Regex("""[/*\-+]""").findAll(answer).count()
    val count_operands = Regex("""[0-9]+(\.[0-9]+)?""").findAll(answer).count()
    if(count_operators+1 != count_operands) {
        println("Ошибка ввода! Выражение содержит неправильное соотношение операторов и операндов.")
        return false
    }
    return true
}

fun main(){
    val tests = arrayOf("+ - 13 4 55", "+ 2 * 2 - 2 1", "+ + 10 20 30", "- - 1 2", "/ + 3 10 * + 2 3 - 3 5", "   ", " + - 5a 5")
    for(test in tests) {
        println("\nИсходное префиксное выражение: $test")
        if(checkAnswer(test)) println("Результат: ${transformExpression(test)}")
    }
}