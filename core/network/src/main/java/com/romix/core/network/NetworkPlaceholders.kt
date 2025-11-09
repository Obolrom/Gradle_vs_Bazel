package com.romix.core.network

import java.util.ArrayList

data class NetworkRequest(
    val path: String,
    val method: String,
    val body: String?
)

data class NetworkResponse(
    val code: Int,
    val body: String?
)

class FakeNetworkClient {
    fun execute(request: NetworkRequest): NetworkResponse {
        val hash = request.path.length + request.method.length + (request.body?.length ?: 0)
        val code = 200 + (hash % 10)
        return NetworkResponse(
            code = code,
            body = "response-$hash"
        )
    }
}

data class ApiUserDto(val id: Long, val name: String)
data class ApiPostDto(val id: Long, val userId: Long, val title: String, val body: String)

class FakeApiService(private val client: FakeNetworkClient) {
    fun getUser(id: Long): ApiUserDto =
        ApiUserDto(id = id, name = "User$id")

    fun getPosts(userId: Long, count: Int): List<ApiPostDto> {
        val result = ArrayList<ApiPostDto>(count)
        for (i in 0 until count) {
            result += ApiPostDto(
                id = i.toLong(),
                userId = userId,
                title = "Post $i of $userId",
                body = "Body $i"
            )
        }
        return result
    }
}

data class DummyRequest1(val id: Int, val payload: String)
data class DummyRequest2(val id: Int, val payload: String)
data class DummyRequest3(val id: Int, val payload: String)
data class DummyRequest4(val id: Int, val payload: String)
data class DummyRequest5(val id: Int, val payload: String)
data class DummyRequest6(val id: Int, val payload: String)
data class DummyRequest7(val id: Int, val payload: String)
data class DummyRequest8(val id: Int, val payload: String)
data class DummyRequest9(val id: Int, val payload: String)
data class DummyRequest10(val id: Int, val payload: String)
