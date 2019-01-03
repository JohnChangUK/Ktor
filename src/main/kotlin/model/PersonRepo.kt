package model

import java.lang.IllegalArgumentException
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

object PersonRepo {

    private val idCounter = AtomicInteger()
    private val personDatabase = CopyOnWriteArrayList<Person>()

    fun add(p: Person): Person {
        if (personDatabase.contains(p)) {
            return personDatabase.find { it == p }!!
        }
        p.id = idCounter.incrementAndGet()
        personDatabase.add(p)
        return p
    }

    fun get(id: String): Person =
        personDatabase.find { it.id.toString() == id } ?: throw IllegalArgumentException("No entity found for $id")

    fun get(id: Int): Person = get(id.toString())

    fun getAll() = personDatabase.toList()

    fun remove(p: Person) {
        if (!personDatabase.contains(p)) {
            throw IllegalArgumentException("Person not stored in database")
        }
        personDatabase.remove(p)
    }

    fun remove(id: String) = personDatabase.remove(get(id))

    fun remove(id: Int) = personDatabase.remove(get(id))

    fun clear() = personDatabase.clear()

}