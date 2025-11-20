package com.romix.feature.feat193

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat193Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat193UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat193FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat193UserSummary
)

data class Feat193UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat193NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat193Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat193Config = Feat193Config()
) {

    fun loadSnapshot(userId: Long): Feat193NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat193NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat193UserSummary {
        return Feat193UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat193FeedItem> {
        val result = java.util.ArrayList<Feat193FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat193FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat193UiMapper {

    fun mapToUi(model: List<Feat193FeedItem>): Feat193UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat193UiModel(
            header = UiText("Feat193 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat193UiModel =
        Feat193UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat193UiModel =
        Feat193UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat193UiModel =
        Feat193UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat193Service(
    private val repository: Feat193Repository,
    private val uiMapper: Feat193UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat193UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat193UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat193UserItem1(val user: CoreUser, val label: String)
data class Feat193UserItem2(val user: CoreUser, val label: String)
data class Feat193UserItem3(val user: CoreUser, val label: String)
data class Feat193UserItem4(val user: CoreUser, val label: String)
data class Feat193UserItem5(val user: CoreUser, val label: String)
data class Feat193UserItem6(val user: CoreUser, val label: String)
data class Feat193UserItem7(val user: CoreUser, val label: String)
data class Feat193UserItem8(val user: CoreUser, val label: String)
data class Feat193UserItem9(val user: CoreUser, val label: String)
data class Feat193UserItem10(val user: CoreUser, val label: String)

data class Feat193StateBlock1(val state: Feat193UiModel, val checksum: Int)
data class Feat193StateBlock2(val state: Feat193UiModel, val checksum: Int)
data class Feat193StateBlock3(val state: Feat193UiModel, val checksum: Int)
data class Feat193StateBlock4(val state: Feat193UiModel, val checksum: Int)
data class Feat193StateBlock5(val state: Feat193UiModel, val checksum: Int)
data class Feat193StateBlock6(val state: Feat193UiModel, val checksum: Int)
data class Feat193StateBlock7(val state: Feat193UiModel, val checksum: Int)
data class Feat193StateBlock8(val state: Feat193UiModel, val checksum: Int)
data class Feat193StateBlock9(val state: Feat193UiModel, val checksum: Int)
data class Feat193StateBlock10(val state: Feat193UiModel, val checksum: Int)

fun buildFeat193UserItem(user: CoreUser, index: Int): Feat193UserItem1 {
    return Feat193UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat193StateBlock(model: Feat193UiModel): Feat193StateBlock1 {
    return Feat193StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat193UserSummary> {
    val list = java.util.ArrayList<Feat193UserSummary>(users.size)
    for (user in users) {
        list += Feat193UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat193UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat193UiModel {
    val summaries = (0 until count).map {
        Feat193UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat193UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat193UiModel> {
    val models = java.util.ArrayList<Feat193UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat193AnalyticsEvent1(val name: String, val value: String)
data class Feat193AnalyticsEvent2(val name: String, val value: String)
data class Feat193AnalyticsEvent3(val name: String, val value: String)
data class Feat193AnalyticsEvent4(val name: String, val value: String)
data class Feat193AnalyticsEvent5(val name: String, val value: String)
data class Feat193AnalyticsEvent6(val name: String, val value: String)
data class Feat193AnalyticsEvent7(val name: String, val value: String)
data class Feat193AnalyticsEvent8(val name: String, val value: String)
data class Feat193AnalyticsEvent9(val name: String, val value: String)
data class Feat193AnalyticsEvent10(val name: String, val value: String)

fun logFeat193Event1(event: Feat193AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat193Event2(event: Feat193AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat193Event3(event: Feat193AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat193Event4(event: Feat193AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat193Event5(event: Feat193AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat193Event6(event: Feat193AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat193Event7(event: Feat193AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat193Event8(event: Feat193AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat193Event9(event: Feat193AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat193Event10(event: Feat193AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat193Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat193Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat193Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat193Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat193Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat193Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat193Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat193Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat193Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat193Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat193(u: CoreUser): Feat193Projection1 =
    Feat193Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat193Projection1> {
    val list = java.util.ArrayList<Feat193Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat193(u)
    }
    return list
}
