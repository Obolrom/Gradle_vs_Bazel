package com.romix.feature.feat464

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat464Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat464UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat464FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat464UserSummary
)

data class Feat464UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat464NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat464Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat464Config = Feat464Config()
) {

    fun loadSnapshot(userId: Long): Feat464NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat464NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat464UserSummary {
        return Feat464UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat464FeedItem> {
        val result = java.util.ArrayList<Feat464FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat464FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat464UiMapper {

    fun mapToUi(model: List<Feat464FeedItem>): Feat464UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat464UiModel(
            header = UiText("Feat464 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat464UiModel =
        Feat464UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat464UiModel =
        Feat464UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat464UiModel =
        Feat464UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat464Service(
    private val repository: Feat464Repository,
    private val uiMapper: Feat464UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat464UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat464UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat464UserItem1(val user: CoreUser, val label: String)
data class Feat464UserItem2(val user: CoreUser, val label: String)
data class Feat464UserItem3(val user: CoreUser, val label: String)
data class Feat464UserItem4(val user: CoreUser, val label: String)
data class Feat464UserItem5(val user: CoreUser, val label: String)
data class Feat464UserItem6(val user: CoreUser, val label: String)
data class Feat464UserItem7(val user: CoreUser, val label: String)
data class Feat464UserItem8(val user: CoreUser, val label: String)
data class Feat464UserItem9(val user: CoreUser, val label: String)
data class Feat464UserItem10(val user: CoreUser, val label: String)

data class Feat464StateBlock1(val state: Feat464UiModel, val checksum: Int)
data class Feat464StateBlock2(val state: Feat464UiModel, val checksum: Int)
data class Feat464StateBlock3(val state: Feat464UiModel, val checksum: Int)
data class Feat464StateBlock4(val state: Feat464UiModel, val checksum: Int)
data class Feat464StateBlock5(val state: Feat464UiModel, val checksum: Int)
data class Feat464StateBlock6(val state: Feat464UiModel, val checksum: Int)
data class Feat464StateBlock7(val state: Feat464UiModel, val checksum: Int)
data class Feat464StateBlock8(val state: Feat464UiModel, val checksum: Int)
data class Feat464StateBlock9(val state: Feat464UiModel, val checksum: Int)
data class Feat464StateBlock10(val state: Feat464UiModel, val checksum: Int)

fun buildFeat464UserItem(user: CoreUser, index: Int): Feat464UserItem1 {
    return Feat464UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat464StateBlock(model: Feat464UiModel): Feat464StateBlock1 {
    return Feat464StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat464UserSummary> {
    val list = java.util.ArrayList<Feat464UserSummary>(users.size)
    for (user in users) {
        list += Feat464UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat464UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat464UiModel {
    val summaries = (0 until count).map {
        Feat464UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat464UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat464UiModel> {
    val models = java.util.ArrayList<Feat464UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat464AnalyticsEvent1(val name: String, val value: String)
data class Feat464AnalyticsEvent2(val name: String, val value: String)
data class Feat464AnalyticsEvent3(val name: String, val value: String)
data class Feat464AnalyticsEvent4(val name: String, val value: String)
data class Feat464AnalyticsEvent5(val name: String, val value: String)
data class Feat464AnalyticsEvent6(val name: String, val value: String)
data class Feat464AnalyticsEvent7(val name: String, val value: String)
data class Feat464AnalyticsEvent8(val name: String, val value: String)
data class Feat464AnalyticsEvent9(val name: String, val value: String)
data class Feat464AnalyticsEvent10(val name: String, val value: String)

fun logFeat464Event1(event: Feat464AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat464Event2(event: Feat464AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat464Event3(event: Feat464AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat464Event4(event: Feat464AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat464Event5(event: Feat464AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat464Event6(event: Feat464AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat464Event7(event: Feat464AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat464Event8(event: Feat464AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat464Event9(event: Feat464AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat464Event10(event: Feat464AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat464Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat464Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat464Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat464Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat464Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat464Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat464Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat464Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat464Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat464Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat464(u: CoreUser): Feat464Projection1 =
    Feat464Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat464Projection1> {
    val list = java.util.ArrayList<Feat464Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat464(u)
    }
    return list
}
