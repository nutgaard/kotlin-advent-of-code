package utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class TrieTest {
    data class UniqueData(val id: String = UUID.randomUUID().toString())

    @Test
    fun `it should be possibe to insert, retrieve, and delete values`() {
        val trie = Trie<UniqueData>()

        val value = UniqueData()
        trie.insert(value.id, value)
        val found = trie.find(value.id)

        trie.delete(value.id)
        val notFound = trie.find(value.id)

        assertEquals(value, found)
        assertNull(notFound)
    }

    @Test
    fun `deletion of intermediate node should not affect children`() {
        val trie = Trie<String>()

        trie.insert("abcdef", "abcdef")
        trie.insert("abcd", "abcd")
        trie.insert("ab", "ab")

        trie.delete("abcd")

        assertTrue(trie.contains("abcdef"))
        assertTrue(trie.contains("ab"))
    }

    @Test
    fun `test a grid`() {
        val grid = SingleArrayGrid(
            Dimension(5, 5),
            Array(25) { "." }
        )

        repeat(5) { it ->
            grid.setValue(Coordinate.Companion.of(it, it), "#")
        }

        val str = grid.asString()

        println(str)
    }
}