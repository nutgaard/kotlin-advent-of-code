package utils

import java.util.Deque
import java.util.LinkedList

open class ConfigurableDeque<T>(
    val type: Type,
    val list: Deque<T> = LinkedList()
) {
    val size: Int
        get() {
            return list.size
        }

    enum class Type {
        STACK, QUEUE
    }

    class Stack<T>(list: Deque<T> = LinkedList()) : ConfigurableDeque<T>(Type.STACK, list)
    class Queue<T>(list: Deque<T> = LinkedList()) : ConfigurableDeque<T>(Type.QUEUE, list)

    fun add(value: T) {
        when (type) {
            Type.STACK -> list.push(value)
            Type.QUEUE -> list.add(value)
        }
    }
    fun offer(value: T) = add(value)
    fun push(value: T) = add(value)

    fun remove(): T {
        // Order is determined when adding the elements
        return list.pollFirst()
    }
    fun poll(): T = remove()
    fun pop(): T = remove()

    fun peek(): T? {
        // Order is determined when adding the elements
        return list.peekFirst()
    }
    fun element(): T? = peek()

    fun isEmpty() = list.isEmpty()
    fun isNotEmpty() = list.isNotEmpty()
    fun contains(value: T) = list.contains(value)
    fun iterator() = list.iterator()
    fun <R> map(transform: (t: T) -> R) = list.map(transform)
    fun filter(predicate: (t: T) -> Boolean) = list.filter(predicate)
    fun reduce(operation: (acc: T, T) -> T) = list.reduce(operation)
    fun <Acc> fold(acc: Acc, operation: (acc: Acc, T) -> Acc) = list.fold(acc, operation)
}