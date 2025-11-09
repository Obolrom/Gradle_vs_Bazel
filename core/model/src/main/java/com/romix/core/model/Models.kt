package com.romix.core.model

data class CoreUser(
    val id: Long,
    val name: String,
    val email: String?,
    val isActive: Boolean
)

data class CoreSession(
    val token: String,
    val userId: Long,
    val expiresAtMillis: Long
)

data class CoreError(
    val code: Int,
    val message: String,
    val recoverable: Boolean
)

enum class CoreStatus {
    IDLE, LOADING, SUCCESS, ERROR
}

data class CoreResult<T>(
    val value: T? = null,
    val error: CoreError? = null
) {
    val isSuccess: Boolean get() = value != null && error == null
    val isError: Boolean get() = error != null
}

data class DummyModel1(val id: Int, val name: String, val flag: Boolean)
data class DummyModel2(val id: Int, val name: String, val flag: Boolean)
data class DummyModel3(val id: Int, val name: String, val flag: Boolean)
data class DummyModel4(val id: Int, val name: String, val flag: Boolean)
data class DummyModel5(val id: Int, val name: String, val flag: Boolean)
data class DummyModel6(val id: Int, val name: String, val flag: Boolean)
data class DummyModel7(val id: Int, val name: String, val flag: Boolean)
data class DummyModel8(val id: Int, val name: String, val flag: Boolean)
data class DummyModel9(val id: Int, val name: String, val flag: Boolean)
data class DummyModel10(val id: Int, val name: String, val flag: Boolean)

data class DummyModel11(val id: Int, val value: String)
data class DummyModel12(val id: Int, val value: String)
data class DummyModel13(val id: Int, val value: String)
data class DummyModel14(val id: Int, val value: String)
data class DummyModel15(val id: Int, val value: String)
data class DummyModel16(val id: Int, val value: String)
data class DummyModel17(val id: Int, val value: String)
data class DummyModel18(val id: Int, val value: String)
data class DummyModel19(val id: Int, val value: String)
data class DummyModel20(val id: Int, val value: String)

data class DummyPair1(val a: Int, val b: Int)
data class DummyPair2(val a: Int, val b: Int)
data class DummyPair3(val a: Int, val b: Int)
data class DummyPair4(val a: Int, val b: Int)
data class DummyPair5(val a: Int, val b: Int)
data class DummyPair6(val a: Int, val b: Int)
data class DummyPair7(val a: Int, val b: Int)
data class DummyPair8(val a: Int, val b: Int)
data class DummyPair9(val a: Int, val b: Int)
data class DummyPair10(val a: Int, val b: Int)

object CoreModelFactory {
    fun createUser(index: Int): CoreUser =
        CoreUser(
            id = index.toLong(),
            name = "User$index",
            email = "user$index@example.com",
            isActive = index % 2 == 0
        )

    fun createError(code: Int): CoreError =
        CoreError(
            code = code,
            message = "Error $code",
            recoverable = code in 400..499
        )
}

fun computeChecksum(input: String): Int {
    var result = 0
    for (c in input) {
        result = (result * 31) + c.code
    }
    return result
}

data class LargeDummy1(val f1: Int, val f2: String, val f3: Boolean)
data class LargeDummy2(val f1: Int, val f2: String, val f3: Boolean)
data class LargeDummy3(val f1: Int, val f2: String, val f3: Boolean)
data class LargeDummy4(val f1: Int, val f2: String, val f3: Boolean)
data class LargeDummy5(val f1: Int, val f2: String, val f3: Boolean)
data class LargeDummy6(val f1: Int, val f2: String, val f3: Boolean)
data class LargeDummy7(val f1: Int, val f2: String, val f3: Boolean)
data class LargeDummy8(val f1: Int, val f2: String, val f3: Boolean)
data class LargeDummy9(val f1: Int, val f2: String, val f3: Boolean)
data class LargeDummy10(val f1: Int, val f2: String, val f3: Boolean)
