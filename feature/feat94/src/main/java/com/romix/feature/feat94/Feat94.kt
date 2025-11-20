package com.romix.feature.feat94

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat94Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat94UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat94FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat94UserSummary
)

data class Feat94UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat94NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat94Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat94Config = Feat94Config()
) {

    fun loadSnapshot(userId: Long): Feat94NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat94NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat94UserSummary {
        return Feat94UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat94FeedItem> {
        val result = java.util.ArrayList<Feat94FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat94FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat94UiMapper {

    fun mapToUi(model: List<Feat94FeedItem>): Feat94UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat94UiModel(
            header = UiText("Feat94 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat94UiModel =
        Feat94UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat94UiModel =
        Feat94UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat94UiModel =
        Feat94UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat94Service(
    private val repository: Feat94Repository,
    private val uiMapper: Feat94UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat94UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat94UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat94UserItem1(val user: CoreUser, val label: String)
data class Feat94UserItem2(val user: CoreUser, val label: String)
data class Feat94UserItem3(val user: CoreUser, val label: String)
data class Feat94UserItem4(val user: CoreUser, val label: String)
data class Feat94UserItem5(val user: CoreUser, val label: String)
data class Feat94UserItem6(val user: CoreUser, val label: String)
data class Feat94UserItem7(val user: CoreUser, val label: String)
data class Feat94UserItem8(val user: CoreUser, val label: String)
data class Feat94UserItem9(val user: CoreUser, val label: String)
data class Feat94UserItem10(val user: CoreUser, val label: String)

data class Feat94StateBlock1(val state: Feat94UiModel, val checksum: Int)
data class Feat94StateBlock2(val state: Feat94UiModel, val checksum: Int)
data class Feat94StateBlock3(val state: Feat94UiModel, val checksum: Int)
data class Feat94StateBlock4(val state: Feat94UiModel, val checksum: Int)
data class Feat94StateBlock5(val state: Feat94UiModel, val checksum: Int)
data class Feat94StateBlock6(val state: Feat94UiModel, val checksum: Int)
data class Feat94StateBlock7(val state: Feat94UiModel, val checksum: Int)
data class Feat94StateBlock8(val state: Feat94UiModel, val checksum: Int)
data class Feat94StateBlock9(val state: Feat94UiModel, val checksum: Int)
data class Feat94StateBlock10(val state: Feat94UiModel, val checksum: Int)

fun buildFeat94UserItem(user: CoreUser, index: Int): Feat94UserItem1 {
    return Feat94UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat94StateBlock(model: Feat94UiModel): Feat94StateBlock1 {
    return Feat94StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat94UserSummary> {
    val list = java.util.ArrayList<Feat94UserSummary>(users.size)
    for (user in users) {
        list += Feat94UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat94UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat94UiModel {
    val summaries = (0 until count).map {
        Feat94UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat94UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat94UiModel> {
    val models = java.util.ArrayList<Feat94UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat94AnalyticsEvent1(val name: String, val value: String)
data class Feat94AnalyticsEvent2(val name: String, val value: String)
data class Feat94AnalyticsEvent3(val name: String, val value: String)
data class Feat94AnalyticsEvent4(val name: String, val value: String)
data class Feat94AnalyticsEvent5(val name: String, val value: String)
data class Feat94AnalyticsEvent6(val name: String, val value: String)
data class Feat94AnalyticsEvent7(val name: String, val value: String)
data class Feat94AnalyticsEvent8(val name: String, val value: String)
data class Feat94AnalyticsEvent9(val name: String, val value: String)
data class Feat94AnalyticsEvent10(val name: String, val value: String)

fun logFeat94Event1(event: Feat94AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat94Event2(event: Feat94AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat94Event3(event: Feat94AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat94Event4(event: Feat94AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat94Event5(event: Feat94AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat94Event6(event: Feat94AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat94Event7(event: Feat94AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat94Event8(event: Feat94AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat94Event9(event: Feat94AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat94Event10(event: Feat94AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat94Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat94Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat94Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat94Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat94Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat94Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat94Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat94Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat94Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat94Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat94(u: CoreUser): Feat94Projection1 =
    Feat94Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat94Projection1> {
    val list = java.util.ArrayList<Feat94Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat94(u)
    }
    return list
}
