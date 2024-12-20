package year2023.day20

import utils.println
import utils.readInput
import utils.timed
import utils.verifySolution

const val dir = "day20"
fun main() {
    val testInputPart1 = readInput("${dir}/Part01_test")
    val testInputPart2 = readInput("${dir}/Part02_test")

    verifySolution(testInputPart1, 32000000, ::part1)
    verifySolution(testInputPart2, 11687500, ::part1)
//    utils.verifySolution(testInputPart2, -1, ::part2)

    val input = readInput("$dir/Input")

    timed("part1") { part1(input).println() }
    timed("part2") { part2(input).println() }
}

fun part1(input: List<String>): Long {
    processor.reset()
    val graph = buildGraph(input)

    repeat(1000) {
        runButtonPress(graph)
    }

    return processor.state()
}

var endCondition: MutableMap<String, Long> = mutableMapOf()
var btnPress = 0
var targetNode: String? = null
fun part2(input: List<String>): Long {
    processor.reset()
    val graph = buildGraph(input)

    targetNode = graph.filterValues { it.children.contains("rx") }.toList().first().first
    endCondition = graph
        .filterValues { it.children.contains(targetNode) }
        .toList()
        .associate { it.first to -1L }
        .toMutableMap()

    while (true) {
        runButtonPress(graph)
        btnPress++
        if (endCondition.all { it.value >= 0 }) break
    }

    return endCondition.values.reduce { acc, l -> acc * l }
}

val processor = Processor()
fun runButtonPress(graph: Graph) {
    processor.add(Pulse(Untyped("button", emptyList()), PULSE_LOW), "broadcaster")
    while (processor.isNotEmpty()) {
        val (pulse, destination) = processor.pop()
        val module = graph[destination] ?: continue
//        utils.println("${pulse.source.name} -${pulse.value}-> $destination")

        module.process(pulse)
    }
}
class Processor {
    private var queue = ArrayDeque<Pair<Pulse, String>>()
    private var lowCounter = 0
    private var highCounter = 0

    fun add(pulse: Pulse, destination: String) {
        if (pulse.value == PULSE_HIGH) {
            highCounter++
        } else {
            lowCounter++
        }
        queue.addLast(pulse to destination)
    }

    fun pop(): Pair<Pulse, String> {
        return queue.removeFirst()
    }

    fun isNotEmpty(): Boolean = queue.isNotEmpty()

    fun reset() {
        queue = ArrayDeque()
        lowCounter = 0
        highCounter = 0
    }

    fun state(): Long {
        return lowCounter.toLong() * highCounter.toLong()
    }
}

data class Pulse(val source: Module, val value: PulseValue)
typealias PulseValue = String
const val PULSE_LOW: PulseValue = "low"
const val PULSE_HIGH: PulseValue = "high"

sealed class Module(val name: String, val children: List<String>) {
    abstract fun process(pulse: Pulse)
    fun send(pulseValue: PulseValue) {
        val pulse = Pulse(this, pulseValue)
        for (child in children) {
            if (pulse.value == PULSE_HIGH && child == targetNode && endCondition[name] == -1L) {
                endCondition[name] = btnPress.toLong() + 1
            }
            processor.add(pulse, child)
        }
    }
}

typealias Graph = Map<String, Module>
fun buildGraph(input: List<String>): Graph {
    val graph = mutableMapOf<String, Module>()
    for (line in input) {
        val (name, childrenList) = line.split(" -> ")
        val children = childrenList.split(",").map { it.trim() }

        if (name.startsWith("%")) {
            graph[name.removePrefix("%")] = FlipFlop(
                name = name.removePrefix("%"),
                children = children
            )
        } else if (name.startsWith("&")) {
            graph[name.removePrefix("&")] = Conjunction(
                name = name.removePrefix("&"),
                children = children
            )
        } else {
            graph[name] = Untyped(
                name = name,
                children = children
            )
        }
    }
    // Add all source-connections to conjunction parts
    val allConjunctionNames = graph
        .filterValues { it is Conjunction }
        .map { it.value.name }

    graph.forEach { (_, module) ->
        val conjunctions = module.children.filter { it in allConjunctionNames }
        conjunctions.forEach { conjunctionName ->
            val cj = graph[conjunctionName] as Conjunction
            cj.sources.add(module)
        }
    }

    return graph
}

class FlipFlop(name: String, children: List<String>) : Module(name, children) {
    var isOn: Boolean = false

    override fun process(pulse: Pulse) {
        if (pulse.value == PULSE_HIGH)return

        if (!isOn) send(PULSE_HIGH) else send(PULSE_LOW)
        isOn = !isOn
    }
}

class Conjunction(name: String, children: List<String>) : Module(name, children) {
    val sources: MutableSet<Module> = mutableSetOf()
    // Defer calculating state until first evaluation. This ensures all sources has been added
    val state: MutableMap<String, PulseValue> by lazy {
        sources
            .associate { it.name to PULSE_LOW }
            .toMutableMap()
    }

    fun addSource(module: Module) {
        sources.add(module)
    }

    override fun process(pulse: Pulse) {
        state[pulse.source.name] = pulse.value
        if (state.values.all { it == PULSE_HIGH }) {
            send(PULSE_LOW)
        } else {
            send(PULSE_HIGH)
        }
    }
}

class Untyped(name: String, children: List<String>) : Module(name, children) {
    override fun process(pulse: Pulse) {
        send(pulse.value)
    }
}