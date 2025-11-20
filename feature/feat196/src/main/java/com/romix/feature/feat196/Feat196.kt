package com.romix.feature.feat196

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat196Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat196UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat196FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat196UserSummary
)

data class Feat196UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat196NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat196Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat196Config = Feat196Config()
) {

    fun loadSnapshot(userId: Long): Feat196NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat196NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat196UserSummary {
        return Feat196UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat196FeedItem> {
        val result = java.util.ArrayList<Feat196FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat196FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat196UiMapper {

    fun mapToUi(model: List<Feat196FeedItem>): Feat196UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat196UiModel(
            header = UiText("Feat196 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat196UiModel =
        Feat196UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat196UiModel =
        Feat196UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat196UiModel =
        Feat196UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat196Service(
    private val repository: Feat196Repository,
    private val uiMapper: Feat196UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat196UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat196UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat196UserItem1(val user: CoreUser, val label: String)
data class Feat196UserItem2(val user: CoreUser, val label: String)
data class Feat196UserItem3(val user: CoreUser, val label: String)
data class Feat196UserItem4(val user: CoreUser, val label: String)
data class Feat196UserItem5(val user: CoreUser, val label: String)
data class Feat196UserItem6(val user: CoreUser, val label: String)
data class Feat196UserItem7(val user: CoreUser, val label: String)
data class Feat196UserItem8(val user: CoreUser, val label: String)
data class Feat196UserItem9(val user: CoreUser, val label: String)
data class Feat196UserItem10(val user: CoreUser, val label: String)

data class Feat196StateBlock1(val state: Feat196UiModel, val checksum: Int)
data class Feat196StateBlock2(val state: Feat196UiModel, val checksum: Int)
data class Feat196StateBlock3(val state: Feat196UiModel, val checksum: Int)
data class Feat196StateBlock4(val state: Feat196UiModel, val checksum: Int)
data class Feat196StateBlock5(val state: Feat196UiModel, val checksum: Int)
data class Feat196StateBlock6(val state: Feat196UiModel, val checksum: Int)
data class Feat196StateBlock7(val state: Feat196UiModel, val checksum: Int)
data class Feat196StateBlock8(val state: Feat196UiModel, val checksum: Int)
data class Feat196StateBlock9(val state: Feat196UiModel, val checksum: Int)
data class Feat196StateBlock10(val state: Feat196UiModel, val checksum: Int)

fun buildFeat196UserItem(user: CoreUser, index: Int): Feat196UserItem1 {
    return Feat196UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat196StateBlock(model: Feat196UiModel): Feat196StateBlock1 {
    return Feat196StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat196UserSummary> {
    val list = java.util.ArrayList<Feat196UserSummary>(users.size)
    for (user in users) {
        list += Feat196UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat196UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat196UiModel {
    val summaries = (0 until count).map {
        Feat196UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat196UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat196UiModel> {
    val models = java.util.ArrayList<Feat196UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat196AnalyticsEvent1(val name: String, val value: String)
data class Feat196AnalyticsEvent2(val name: String, val value: String)
data class Feat196AnalyticsEvent3(val name: String, val value: String)
data class Feat196AnalyticsEvent4(val name: String, val value: String)
data class Feat196AnalyticsEvent5(val name: String, val value: String)
data class Feat196AnalyticsEvent6(val name: String, val value: String)
data class Feat196AnalyticsEvent7(val name: String, val value: String)
data class Feat196AnalyticsEvent8(val name: String, val value: String)
data class Feat196AnalyticsEvent9(val name: String, val value: String)
data class Feat196AnalyticsEvent10(val name: String, val value: String)

fun logFeat196Event1(event: Feat196AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat196Event2(event: Feat196AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat196Event3(event: Feat196AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat196Event4(event: Feat196AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat196Event5(event: Feat196AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat196Event6(event: Feat196AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat196Event7(event: Feat196AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat196Event8(event: Feat196AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat196Event9(event: Feat196AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat196Event10(event: Feat196AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat196Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat196Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat196Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat196Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat196Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat196Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat196Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat196Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat196Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat196Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat196(u: CoreUser): Feat196Projection1 =
    Feat196Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat196Projection1> {
    val list = java.util.ArrayList<Feat196Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat196(u)
    }
    return list
}
