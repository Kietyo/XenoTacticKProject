package benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlin.reflect.KClass

data class TestComponent1(val data: String)
data class TestComponent2(val data: String)

@State(Scope.Benchmark)
class TestBenchmark {

    val classToInstanceMap = mutableMapOf<KClass<*>, Any>().apply {
        this[TestComponent2::class] = TestComponent2("test2")
    }
    val stringToInstanceMap = mutableMapOf<String, Any>().apply {
        this[TestComponent2::class.toString()] = TestComponent2("test2")
    }
    val data = TestComponent1("test")

    @Benchmark
    fun setClassToInstance() {
        classToInstanceMap[data::class] = data
    }

    fun setStringToInstance() {
        stringToInstanceMap[data::class.toString()] = data
    }

    @Benchmark
    fun setClassToInstance2() {
        classToInstanceMap[TestComponent1::class] = data
    }

    fun setStringToInstance2() {
        stringToInstanceMap[TestComponent1::class.toString()] = data
    }

    fun getClassToInstance() {
        val data = classToInstanceMap[TestComponent2::class]
    }

    fun getStringToInstance() {
        val data = stringToInstanceMap[TestComponent2::class.toString()]
    }
}