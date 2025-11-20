package com.romix.feature.feat378

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat378Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat378UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat378FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat378UserSummary
)

data class Feat378UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat378NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat378Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat378Config = Feat378Config()
) {

    fun loadSnapshot(userId: Long): Feat378NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat378NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat378UserSummary {
        return Feat378UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat378FeedItem> {
        val result = java.util.ArrayList<Feat378FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat378FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat378UiMapper {

    fun mapToUi(model: List<Feat378FeedItem>): Feat378UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat378UiModel(
            header = UiText("Feat378 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat378UiModel =
        Feat378UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat378UiModel =
        Feat378UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat378UiModel =
        Feat378UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat378Service(
    private val repository: Feat378Repository,
    private val uiMapper: Feat378UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat378UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat378UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat378UserItem1(val user: CoreUser, val label: String)
data class Feat378UserItem2(val user: CoreUser, val label: String)
data class Feat378UserItem3(val user: CoreUser, val label: String)
data class Feat378UserItem4(val user: CoreUser, val label: String)
data class Feat378UserItem5(val user: CoreUser, val label: String)
data class Feat378UserItem6(val user: CoreUser, val label: String)
data class Feat378UserItem7(val user: CoreUser, val label: String)
data class Feat378UserItem8(val user: CoreUser, val label: String)
data class Feat378UserItem9(val user: CoreUser, val label: String)
data class Feat378UserItem10(val user: CoreUser, val label: String)

data class Feat378StateBlock1(val state: Feat378UiModel, val checksum: Int)
data class Feat378StateBlock2(val state: Feat378UiModel, val checksum: Int)
data class Feat378StateBlock3(val state: Feat378UiModel, val checksum: Int)
data class Feat378StateBlock4(val state: Feat378UiModel, val checksum: Int)
data class Feat378StateBlock5(val state: Feat378UiModel, val checksum: Int)
data class Feat378StateBlock6(val state: Feat378UiModel, val checksum: Int)
data class Feat378StateBlock7(val state: Feat378UiModel, val checksum: Int)
data class Feat378StateBlock8(val state: Feat378UiModel, val checksum: Int)
data class Feat378StateBlock9(val state: Feat378UiModel, val checksum: Int)
data class Feat378StateBlock10(val state: Feat378UiModel, val checksum: Int)

fun buildFeat378UserItem(user: CoreUser, index: Int): Feat378UserItem1 {
    return Feat378UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat378StateBlock(model: Feat378UiModel): Feat378StateBlock1 {
    return Feat378StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat378UserSummary> {
    val list = java.util.ArrayList<Feat378UserSummary>(users.size)
    for (user in users) {
        list += Feat378UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat378UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat378UiModel {
    val summaries = (0 until count).map {
        Feat378UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat378UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat378UiModel> {
    val models = java.util.ArrayList<Feat378UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat378AnalyticsEvent1(val name: String, val value: String)
data class Feat378AnalyticsEvent2(val name: String, val value: String)
data class Feat378AnalyticsEvent3(val name: String, val value: String)
data class Feat378AnalyticsEvent4(val name: String, val value: String)
data class Feat378AnalyticsEvent5(val name: String, val value: String)
data class Feat378AnalyticsEvent6(val name: String, val value: String)
data class Feat378AnalyticsEvent7(val name: String, val value: String)
data class Feat378AnalyticsEvent8(val name: String, val value: String)
data class Feat378AnalyticsEvent9(val name: String, val value: String)
data class Feat378AnalyticsEvent10(val name: String, val value: String)

fun logFeat378Event1(event: Feat378AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat378Event2(event: Feat378AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat378Event3(event: Feat378AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat378Event4(event: Feat378AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat378Event5(event: Feat378AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat378Event6(event: Feat378AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat378Event7(event: Feat378AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat378Event8(event: Feat378AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat378Event9(event: Feat378AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat378Event10(event: Feat378AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat378Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat378Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat378Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat378Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat378Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat378Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat378Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat378Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat378Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat378Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat378(u: CoreUser): Feat378Projection1 =
    Feat378Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat378Projection1> {
    val list = java.util.ArrayList<Feat378Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat378(u)
    }
    return list
}
