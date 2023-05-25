package benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

data class Vector2(
    var x: Int = 0,
    var y: Int = 0,
)

fun multiplyVector2(input: Vector2, multiplier: Int): Vector2 {
    return Vector2(input.x * multiplier, input.y * multiplier)
}

fun multiplyVector2WithOut(input: Vector2, multiplier: Int, out: Vector2 = Vector2()): Vector2 {
    out.x = input.x * multiplier
    out.y = input.y * multiplier
    return out
}

@State(Scope.Benchmark)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class AllocationBenchmark {

    companion object {
        val TEST_VECTOR = Vector2(2, 3)
    }

    val preAllocated = Vector2()

    @Benchmark
    fun multiplyVector2(): Vector2 {
        return multiplyVector2(TEST_VECTOR, 3)
    }

    @Benchmark
    fun multiplyVector2WithOut(): Vector2 {
        return multiplyVector2WithOut(TEST_VECTOR, 3)
    }

    @Benchmark
    fun multiplyVector2WithOutWithPreallocated(): Vector2 {
        return multiplyVector2WithOut(TEST_VECTOR, 3, preAllocated)
    }

}