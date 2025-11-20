package com.romix.feature.feat409

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat409Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat409UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat409FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat409UserSummary
)

data class Feat409UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat409NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat409Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat409Config = Feat409Config()
) {

    fun loadSnapshot(userId: Long): Feat409NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat409NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat409UserSummary {
        return Feat409UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat409FeedItem> {
        val result = java.util.ArrayList<Feat409FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat409FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat409UiMapper {

    fun mapToUi(model: List<Feat409FeedItem>): Feat409UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat409UiModel(
            header = UiText("Feat409 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat409UiModel =
        Feat409UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat409UiModel =
        Feat409UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat409UiModel =
        Feat409UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat409Service(
    private val repository: Feat409Repository,
    private val uiMapper: Feat409UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat409UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat409UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat409UserItem1(val user: CoreUser, val label: String)
data class Feat409UserItem2(val user: CoreUser, val label: String)
data class Feat409UserItem3(val user: CoreUser, val label: String)
data class Feat409UserItem4(val user: CoreUser, val label: String)
data class Feat409UserItem5(val user: CoreUser, val label: String)
data class Feat409UserItem6(val user: CoreUser, val label: String)
data class Feat409UserItem7(val user: CoreUser, val label: String)
data class Feat409UserItem8(val user: CoreUser, val label: String)
data class Feat409UserItem9(val user: CoreUser, val label: String)
data class Feat409UserItem10(val user: CoreUser, val label: String)

data class Feat409StateBlock1(val state: Feat409UiModel, val checksum: Int)
data class Feat409StateBlock2(val state: Feat409UiModel, val checksum: Int)
data class Feat409StateBlock3(val state: Feat409UiModel, val checksum: Int)
data class Feat409StateBlock4(val state: Feat409UiModel, val checksum: Int)
data class Feat409StateBlock5(val state: Feat409UiModel, val checksum: Int)
data class Feat409StateBlock6(val state: Feat409UiModel, val checksum: Int)
data class Feat409StateBlock7(val state: Feat409UiModel, val checksum: Int)
data class Feat409StateBlock8(val state: Feat409UiModel, val checksum: Int)
data class Feat409StateBlock9(val state: Feat409UiModel, val checksum: Int)
data class Feat409StateBlock10(val state: Feat409UiModel, val checksum: Int)

fun buildFeat409UserItem(user: CoreUser, index: Int): Feat409UserItem1 {
    return Feat409UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat409StateBlock(model: Feat409UiModel): Feat409StateBlock1 {
    return Feat409StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat409UserSummary> {
    val list = java.util.ArrayList<Feat409UserSummary>(users.size)
    for (user in users) {
        list += Feat409UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat409UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat409UiModel {
    val summaries = (0 until count).map {
        Feat409UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat409UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat409UiModel> {
    val models = java.util.ArrayList<Feat409UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat409AnalyticsEvent1(val name: String, val value: String)
data class Feat409AnalyticsEvent2(val name: String, val value: String)
data class Feat409AnalyticsEvent3(val name: String, val value: String)
data class Feat409AnalyticsEvent4(val name: String, val value: String)
data class Feat409AnalyticsEvent5(val name: String, val value: String)
data class Feat409AnalyticsEvent6(val name: String, val value: String)
data class Feat409AnalyticsEvent7(val name: String, val value: String)
data class Feat409AnalyticsEvent8(val name: String, val value: String)
data class Feat409AnalyticsEvent9(val name: String, val value: String)
data class Feat409AnalyticsEvent10(val name: String, val value: String)

fun logFeat409Event1(event: Feat409AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat409Event2(event: Feat409AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat409Event3(event: Feat409AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat409Event4(event: Feat409AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat409Event5(event: Feat409AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat409Event6(event: Feat409AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat409Event7(event: Feat409AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat409Event8(event: Feat409AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat409Event9(event: Feat409AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat409Event10(event: Feat409AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat409Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat409Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat409Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat409Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat409Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat409Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat409Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat409Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat409Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat409Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat409(u: CoreUser): Feat409Projection1 =
    Feat409Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat409Projection1> {
    val list = java.util.ArrayList<Feat409Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat409(u)
    }
    return list
}
