package com.romix.feature.feat75

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat75Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat75UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat75FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat75UserSummary
)

data class Feat75UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat75NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat75Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat75Config = Feat75Config()
) {

    fun loadSnapshot(userId: Long): Feat75NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat75NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat75UserSummary {
        return Feat75UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat75FeedItem> {
        val result = java.util.ArrayList<Feat75FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat75FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat75UiMapper {

    fun mapToUi(model: List<Feat75FeedItem>): Feat75UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat75UiModel(
            header = UiText("Feat75 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat75UiModel =
        Feat75UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat75UiModel =
        Feat75UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat75UiModel =
        Feat75UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat75Service(
    private val repository: Feat75Repository,
    private val uiMapper: Feat75UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat75UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat75UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat75UserItem1(val user: CoreUser, val label: String)
data class Feat75UserItem2(val user: CoreUser, val label: String)
data class Feat75UserItem3(val user: CoreUser, val label: String)
data class Feat75UserItem4(val user: CoreUser, val label: String)
data class Feat75UserItem5(val user: CoreUser, val label: String)
data class Feat75UserItem6(val user: CoreUser, val label: String)
data class Feat75UserItem7(val user: CoreUser, val label: String)
data class Feat75UserItem8(val user: CoreUser, val label: String)
data class Feat75UserItem9(val user: CoreUser, val label: String)
data class Feat75UserItem10(val user: CoreUser, val label: String)

data class Feat75StateBlock1(val state: Feat75UiModel, val checksum: Int)
data class Feat75StateBlock2(val state: Feat75UiModel, val checksum: Int)
data class Feat75StateBlock3(val state: Feat75UiModel, val checksum: Int)
data class Feat75StateBlock4(val state: Feat75UiModel, val checksum: Int)
data class Feat75StateBlock5(val state: Feat75UiModel, val checksum: Int)
data class Feat75StateBlock6(val state: Feat75UiModel, val checksum: Int)
data class Feat75StateBlock7(val state: Feat75UiModel, val checksum: Int)
data class Feat75StateBlock8(val state: Feat75UiModel, val checksum: Int)
data class Feat75StateBlock9(val state: Feat75UiModel, val checksum: Int)
data class Feat75StateBlock10(val state: Feat75UiModel, val checksum: Int)

fun buildFeat75UserItem(user: CoreUser, index: Int): Feat75UserItem1 {
    return Feat75UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat75StateBlock(model: Feat75UiModel): Feat75StateBlock1 {
    return Feat75StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat75UserSummary> {
    val list = java.util.ArrayList<Feat75UserSummary>(users.size)
    for (user in users) {
        list += Feat75UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat75UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat75UiModel {
    val summaries = (0 until count).map {
        Feat75UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat75UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat75UiModel> {
    val models = java.util.ArrayList<Feat75UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat75AnalyticsEvent1(val name: String, val value: String)
data class Feat75AnalyticsEvent2(val name: String, val value: String)
data class Feat75AnalyticsEvent3(val name: String, val value: String)
data class Feat75AnalyticsEvent4(val name: String, val value: String)
data class Feat75AnalyticsEvent5(val name: String, val value: String)
data class Feat75AnalyticsEvent6(val name: String, val value: String)
data class Feat75AnalyticsEvent7(val name: String, val value: String)
data class Feat75AnalyticsEvent8(val name: String, val value: String)
data class Feat75AnalyticsEvent9(val name: String, val value: String)
data class Feat75AnalyticsEvent10(val name: String, val value: String)

fun logFeat75Event1(event: Feat75AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat75Event2(event: Feat75AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat75Event3(event: Feat75AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat75Event4(event: Feat75AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat75Event5(event: Feat75AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat75Event6(event: Feat75AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat75Event7(event: Feat75AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat75Event8(event: Feat75AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat75Event9(event: Feat75AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat75Event10(event: Feat75AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat75Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat75Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat75Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat75Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat75Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat75Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat75Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat75Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat75Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat75Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat75(u: CoreUser): Feat75Projection1 =
    Feat75Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat75Projection1> {
    val list = java.util.ArrayList<Feat75Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat75(u)
    }
    return list
}
