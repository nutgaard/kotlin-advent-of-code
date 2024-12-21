package utils

class Trie<Value> {
    val root = Node<Value>()

    data class Node<Value>(
        val children: MutableMap<Char, Node<Value>> = mutableMapOf(),
        var value: Value? = null
    )

    fun insert(key: String, value: Value) {
        key.fold(root) { node, char ->
            node.children[char] ?: Node<Value>().also { node.children[char] = it }
        }.value = value
    }

    fun find(key: String): Value? {
        return key.fold(root) { node, char ->
            node.children[char] ?: return null
        }.value
    }

    fun contains(key: String): Boolean {
        key.fold(root) { node, char ->
            node.children[char] ?: return false
        }
        return true
    }

    fun delete(key: String) {
        delete(key, 0, root)
    }

    private fun delete(key: String, index: Int, node: Node<Value>) {
        if (index == key.length) {
            node.value = null
        } else {
            node.children[key[index]]?.run {
                delete(key, index + 1, this)
                if (children.isEmpty() && value == null) node.children.remove(key[index])
            }
        }
    }
}