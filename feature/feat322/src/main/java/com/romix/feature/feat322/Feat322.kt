package com.romix.feature.feat322

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat322Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat322UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat322FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat322UserSummary
)

data class Feat322UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat322NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat322Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat322Config = Feat322Config()
) {

    fun loadSnapshot(userId: Long): Feat322NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat322NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat322UserSummary {
        return Feat322UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat322FeedItem> {
        val result = java.util.ArrayList<Feat322FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat322FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat322UiMapper {

    fun mapToUi(model: List<Feat322FeedItem>): Feat322UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat322UiModel(
            header = UiText("Feat322 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat322UiModel =
        Feat322UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat322UiModel =
        Feat322UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat322UiModel =
        Feat322UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat322Service(
    private val repository: Feat322Repository,
    private val uiMapper: Feat322UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat322UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat322UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat322UserItem1(val user: CoreUser, val label: String)
data class Feat322UserItem2(val user: CoreUser, val label: String)
data class Feat322UserItem3(val user: CoreUser, val label: String)
data class Feat322UserItem4(val user: CoreUser, val label: String)
data class Feat322UserItem5(val user: CoreUser, val label: String)
data class Feat322UserItem6(val user: CoreUser, val label: String)
data class Feat322UserItem7(val user: CoreUser, val label: String)
data class Feat322UserItem8(val user: CoreUser, val label: String)
data class Feat322UserItem9(val user: CoreUser, val label: String)
data class Feat322UserItem10(val user: CoreUser, val label: String)

data class Feat322StateBlock1(val state: Feat322UiModel, val checksum: Int)
data class Feat322StateBlock2(val state: Feat322UiModel, val checksum: Int)
data class Feat322StateBlock3(val state: Feat322UiModel, val checksum: Int)
data class Feat322StateBlock4(val state: Feat322UiModel, val checksum: Int)
data class Feat322StateBlock5(val state: Feat322UiModel, val checksum: Int)
data class Feat322StateBlock6(val state: Feat322UiModel, val checksum: Int)
data class Feat322StateBlock7(val state: Feat322UiModel, val checksum: Int)
data class Feat322StateBlock8(val state: Feat322UiModel, val checksum: Int)
data class Feat322StateBlock9(val state: Feat322UiModel, val checksum: Int)
data class Feat322StateBlock10(val state: Feat322UiModel, val checksum: Int)

fun buildFeat322UserItem(user: CoreUser, index: Int): Feat322UserItem1 {
    return Feat322UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat322StateBlock(model: Feat322UiModel): Feat322StateBlock1 {
    return Feat322StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat322UserSummary> {
    val list = java.util.ArrayList<Feat322UserSummary>(users.size)
    for (user in users) {
        list += Feat322UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat322UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat322UiModel {
    val summaries = (0 until count).map {
        Feat322UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat322UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat322UiModel> {
    val models = java.util.ArrayList<Feat322UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat322AnalyticsEvent1(val name: String, val value: String)
data class Feat322AnalyticsEvent2(val name: String, val value: String)
data class Feat322AnalyticsEvent3(val name: String, val value: String)
data class Feat322AnalyticsEvent4(val name: String, val value: String)
data class Feat322AnalyticsEvent5(val name: String, val value: String)
data class Feat322AnalyticsEvent6(val name: String, val value: String)
data class Feat322AnalyticsEvent7(val name: String, val value: String)
data class Feat322AnalyticsEvent8(val name: String, val value: String)
data class Feat322AnalyticsEvent9(val name: String, val value: String)
data class Feat322AnalyticsEvent10(val name: String, val value: String)

fun logFeat322Event1(event: Feat322AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat322Event2(event: Feat322AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat322Event3(event: Feat322AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat322Event4(event: Feat322AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat322Event5(event: Feat322AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat322Event6(event: Feat322AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat322Event7(event: Feat322AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat322Event8(event: Feat322AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat322Event9(event: Feat322AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat322Event10(event: Feat322AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat322Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat322Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat322Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat322Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat322Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat322Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat322Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat322Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat322Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat322Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat322(u: CoreUser): Feat322Projection1 =
    Feat322Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat322Projection1> {
    val list = java.util.ArrayList<Feat322Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat322(u)
    }
    return list
}
