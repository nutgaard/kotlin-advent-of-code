package utils

object Memoizer {
    private sealed class Key {
        data class One<A>(val one: A) : Key()
        data class Two<A, B>(val one: A, val two: B) : Key()
        data class Three<A, B, C>(val one: A, val two: B, val three: C) : Key()
        data class Four<A, B, C, D>(val one: A, val two: B, val three: C, val four: D) : Key()
        data class Five<A, B, C, D, E>(val one: A, val two: B, val three: C, val four: D, val five: E) : Key()
    }

    fun <A, O> of(fn: (A) -> O): (A) -> O {
        val cache = mutableMapOf<Key, O>()
        return { one -> cache.getOrPut(Key.One(one)) {
            fn(one) }
        }
    }

    fun <A, B, O> of(fn: (A, B) -> O): (A, B) -> O {
        val cache = mutableMapOf<Key, O>()
        return { one, two -> cache.getOrPut(Key.Two(one, two)) {
            fn(one, two) }
        }
    }

    fun <A, B, C, O> of(fn: (A, B, C) -> O): (A, B, C) -> O {
        val cache = mutableMapOf<Key, O>()
        return { one, two, three -> cache.getOrPut(Key.Three(one, two, three)) {
            fn(one, two, three) }
        }
    }

    fun <A, B, C, D, O> of(fn: (A, B, C, D) -> O): (A, B, C, D) -> O {
        val cache = mutableMapOf<Key, O>()
        return { one, two, three, four -> cache.getOrPut(Key.Four(one, two, three, four)) {
            fn(one, two, three, four) }
        }
    }

    fun <A, B, C, D, E, O> of(fn: (A, B, C, D, E) -> O): (A, B, C, D, E) -> O {
        val cache = mutableMapOf<Key, O>()
        return { one, two, three, four, five -> cache.getOrPut(Key.Five(one, two, three, four, five)) {
            fn(one, two, three, four, five) }
        }
    }
}