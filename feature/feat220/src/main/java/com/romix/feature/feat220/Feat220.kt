package com.romix.feature.feat220

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat220Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat220UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat220FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat220UserSummary
)

data class Feat220UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat220NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat220Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat220Config = Feat220Config()
) {

    fun loadSnapshot(userId: Long): Feat220NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat220NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat220UserSummary {
        return Feat220UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat220FeedItem> {
        val result = java.util.ArrayList<Feat220FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat220FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat220UiMapper {

    fun mapToUi(model: List<Feat220FeedItem>): Feat220UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat220UiModel(
            header = UiText("Feat220 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat220UiModel =
        Feat220UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat220UiModel =
        Feat220UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat220UiModel =
        Feat220UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat220Service(
    private val repository: Feat220Repository,
    private val uiMapper: Feat220UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat220UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat220UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat220UserItem1(val user: CoreUser, val label: String)
data class Feat220UserItem2(val user: CoreUser, val label: String)
data class Feat220UserItem3(val user: CoreUser, val label: String)
data class Feat220UserItem4(val user: CoreUser, val label: String)
data class Feat220UserItem5(val user: CoreUser, val label: String)
data class Feat220UserItem6(val user: CoreUser, val label: String)
data class Feat220UserItem7(val user: CoreUser, val label: String)
data class Feat220UserItem8(val user: CoreUser, val label: String)
data class Feat220UserItem9(val user: CoreUser, val label: String)
data class Feat220UserItem10(val user: CoreUser, val label: String)

data class Feat220StateBlock1(val state: Feat220UiModel, val checksum: Int)
data class Feat220StateBlock2(val state: Feat220UiModel, val checksum: Int)
data class Feat220StateBlock3(val state: Feat220UiModel, val checksum: Int)
data class Feat220StateBlock4(val state: Feat220UiModel, val checksum: Int)
data class Feat220StateBlock5(val state: Feat220UiModel, val checksum: Int)
data class Feat220StateBlock6(val state: Feat220UiModel, val checksum: Int)
data class Feat220StateBlock7(val state: Feat220UiModel, val checksum: Int)
data class Feat220StateBlock8(val state: Feat220UiModel, val checksum: Int)
data class Feat220StateBlock9(val state: Feat220UiModel, val checksum: Int)
data class Feat220StateBlock10(val state: Feat220UiModel, val checksum: Int)

fun buildFeat220UserItem(user: CoreUser, index: Int): Feat220UserItem1 {
    return Feat220UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat220StateBlock(model: Feat220UiModel): Feat220StateBlock1 {
    return Feat220StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat220UserSummary> {
    val list = java.util.ArrayList<Feat220UserSummary>(users.size)
    for (user in users) {
        list += Feat220UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat220UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat220UiModel {
    val summaries = (0 until count).map {
        Feat220UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat220UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat220UiModel> {
    val models = java.util.ArrayList<Feat220UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat220AnalyticsEvent1(val name: String, val value: String)
data class Feat220AnalyticsEvent2(val name: String, val value: String)
data class Feat220AnalyticsEvent3(val name: String, val value: String)
data class Feat220AnalyticsEvent4(val name: String, val value: String)
data class Feat220AnalyticsEvent5(val name: String, val value: String)
data class Feat220AnalyticsEvent6(val name: String, val value: String)
data class Feat220AnalyticsEvent7(val name: String, val value: String)
data class Feat220AnalyticsEvent8(val name: String, val value: String)
data class Feat220AnalyticsEvent9(val name: String, val value: String)
data class Feat220AnalyticsEvent10(val name: String, val value: String)

fun logFeat220Event1(event: Feat220AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat220Event2(event: Feat220AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat220Event3(event: Feat220AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat220Event4(event: Feat220AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat220Event5(event: Feat220AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat220Event6(event: Feat220AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat220Event7(event: Feat220AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat220Event8(event: Feat220AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat220Event9(event: Feat220AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat220Event10(event: Feat220AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat220Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat220Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat220Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat220Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat220Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat220Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat220Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat220Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat220Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat220Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat220(u: CoreUser): Feat220Projection1 =
    Feat220Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat220Projection1> {
    val list = java.util.ArrayList<Feat220Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat220(u)
    }
    return list
}
