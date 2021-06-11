package cn.vove7.android.scaffold.demo

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        B()
    }
}

open class A {
    init {
        printv()
    }

    open fun printv() {}
}

class B : A() {
    private var v = 1

    init {
        println(v)
    }

    override fun printv() {
        // 0
        v = 2
        print(v)
    }
}