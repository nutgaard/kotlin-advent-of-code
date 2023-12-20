package day19

import println
import readInput
import timed
import verifySolution

const val dir = "day19"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test").parse()
    val testInputPart2 = readInput("${dir}/Part02_test").parse()

    verifySolution(testInputPart1, 19114, ::part1)
    verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input").parse()

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: Input): Int {
    var sum = 0
    for (part in input.parts) {
        var current: Destination = Destination.Named("in")
        do {
            val rules = requireNotNull(input.rules[current]) {
                "Could not find rule for $current"
            }
            for (rule in rules) {
                if (rule is Rule.DestinationRule) {
                    current = rule.destination
                    break
                } else if (rule is Rule.ConditionRule) {
                    val actualValue = rule.element(part)
                    if (rule.operator == Operator.LT && actualValue < rule.value) {
                        current = rule.destination
                        break
                    } else if (rule.operator == Operator.GT && actualValue > rule.value) {
                        current = rule.destination
                        break
                    }
                } else {
                    error("Unknown rule: $rule")
                }
            }
        } while (current != Destination.Accepted && current != Destination.Rejected)
        if (current == Destination.Accepted) {
            sum += part.x + part.m + part.a + part.s
        }
    }
    return sum
}

fun part2(input: Input): Int {
    val t = 0..10
    return -1
}

data class Input(
    val rules: Map<Destination, List<Rule>>,
    val parts: List<Part>
) {
    companion object {
        fun parse(lines: List<String>): Input {
            val rules = mutableMapOf<Destination, List<Rule>>()
            val parts = mutableListOf<Part>()

            var parseRules = true
            for (line in lines) {
                if (line == "") {
                    parseRules = false
                    continue
                }
                if (parseRules) {
                    val rule = RuleLine.parse(line)
                    rules[rule.name] = rule.rules
                } else {
                    parts.add(Part.parse(line))
                }
            }


            return Input(rules, parts)
        }
    }
}

fun List<String>.parse(): Input = Input.parse(this)

data class RuleLine(
    val name: Destination,
    val rules: List<Rule>
) {
    companion object {
        fun parse(line: String): RuleLine {
            val ruleStart = line.indexOf('{')
            val name = line.substring(0, ruleStart)
            val rules = line
                .substring(ruleStart)
                .removePrefix("{")
                .removeSuffix("}")
                .split(",")
                .map { rule ->
                    if (rule.contains(':')) {
                        val (elementStr, valueStr, destinationStr) = rule.split('<', '>', ':')

                        Rule.ConditionRule(
                            element = Part.elementFn(elementStr),
                            operator = if (rule.contains('<')) Operator.LT else Operator.GT,
                            value = valueStr.toInt(),
                            destination = Destination.parse(destinationStr)
                        )
                    } else {
                        Rule.DestinationRule(Destination.parse(rule))
                    }
                }

            return RuleLine(Destination.parse(name), rules)
        }
    }
}

sealed class Destination {
    object Rejected : Destination()
    object Accepted : Destination()
    data class Named(val name: String): Destination()

    companion object {
        fun parse(name: String): Destination {
            return when (name) {
                "A" -> Accepted
                "R" -> Rejected
                else -> Named(name)
            }
        }
    }
}

sealed class Rule {
    data class ConditionRule(
        val element: (Part) -> Int,
        val operator: Operator,
        val value: Int,
        val destination: Destination
    ): Rule()

    data class DestinationRule(
        val destination: Destination
    ): Rule()
}

enum class Operator {
    LT, GT;

    companion object {
        fun fromString(str: String): Operator {
            return when (str) {
                "<" -> LT
                ">" -> GT
                else -> error("Unknown operator: $str")
            }
        }
    }
}

data class Part(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int,
) {
    companion object {
        fun parse(line: String): Part {
            val map = line
                .removePrefix("{")
                .removeSuffix("}")
                .split(",")
                .fold(mutableMapOf<String, Int>()) { acc, element ->
                    val (name, valueStr) = element.split("=")
                    acc[name] = valueStr.toInt()
                    acc
                }

            return Part(
                x = map["x"] ?: 0,
                m = map["m"] ?: 0,
                a = map["a"] ?: 0,
                s = map["s"] ?: 0,
            )
        }

        fun elementFn(name: String): (Part) -> Int {
            return when (name) {
                "x" -> Part::x
                "m" -> Part::m
                "a" -> Part::a
                "s" -> Part::s
                else -> error("Unknown element name: $name")
            }
        }
    }
}