package com.romix.feature.feat401

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat401Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat401UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat401FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat401UserSummary
)

data class Feat401UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat401NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat401Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat401Config = Feat401Config()
) {

    fun loadSnapshot(userId: Long): Feat401NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat401NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat401UserSummary {
        return Feat401UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat401FeedItem> {
        val result = java.util.ArrayList<Feat401FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat401FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat401UiMapper {

    fun mapToUi(model: List<Feat401FeedItem>): Feat401UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat401UiModel(
            header = UiText("Feat401 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat401UiModel =
        Feat401UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat401UiModel =
        Feat401UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat401UiModel =
        Feat401UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat401Service(
    private val repository: Feat401Repository,
    private val uiMapper: Feat401UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat401UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat401UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat401UserItem1(val user: CoreUser, val label: String)
data class Feat401UserItem2(val user: CoreUser, val label: String)
data class Feat401UserItem3(val user: CoreUser, val label: String)
data class Feat401UserItem4(val user: CoreUser, val label: String)
data class Feat401UserItem5(val user: CoreUser, val label: String)
data class Feat401UserItem6(val user: CoreUser, val label: String)
data class Feat401UserItem7(val user: CoreUser, val label: String)
data class Feat401UserItem8(val user: CoreUser, val label: String)
data class Feat401UserItem9(val user: CoreUser, val label: String)
data class Feat401UserItem10(val user: CoreUser, val label: String)

data class Feat401StateBlock1(val state: Feat401UiModel, val checksum: Int)
data class Feat401StateBlock2(val state: Feat401UiModel, val checksum: Int)
data class Feat401StateBlock3(val state: Feat401UiModel, val checksum: Int)
data class Feat401StateBlock4(val state: Feat401UiModel, val checksum: Int)
data class Feat401StateBlock5(val state: Feat401UiModel, val checksum: Int)
data class Feat401StateBlock6(val state: Feat401UiModel, val checksum: Int)
data class Feat401StateBlock7(val state: Feat401UiModel, val checksum: Int)
data class Feat401StateBlock8(val state: Feat401UiModel, val checksum: Int)
data class Feat401StateBlock9(val state: Feat401UiModel, val checksum: Int)
data class Feat401StateBlock10(val state: Feat401UiModel, val checksum: Int)

fun buildFeat401UserItem(user: CoreUser, index: Int): Feat401UserItem1 {
    return Feat401UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat401StateBlock(model: Feat401UiModel): Feat401StateBlock1 {
    return Feat401StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat401UserSummary> {
    val list = java.util.ArrayList<Feat401UserSummary>(users.size)
    for (user in users) {
        list += Feat401UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat401UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat401UiModel {
    val summaries = (0 until count).map {
        Feat401UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat401UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat401UiModel> {
    val models = java.util.ArrayList<Feat401UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat401AnalyticsEvent1(val name: String, val value: String)
data class Feat401AnalyticsEvent2(val name: String, val value: String)
data class Feat401AnalyticsEvent3(val name: String, val value: String)
data class Feat401AnalyticsEvent4(val name: String, val value: String)
data class Feat401AnalyticsEvent5(val name: String, val value: String)
data class Feat401AnalyticsEvent6(val name: String, val value: String)
data class Feat401AnalyticsEvent7(val name: String, val value: String)
data class Feat401AnalyticsEvent8(val name: String, val value: String)
data class Feat401AnalyticsEvent9(val name: String, val value: String)
data class Feat401AnalyticsEvent10(val name: String, val value: String)

fun logFeat401Event1(event: Feat401AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat401Event2(event: Feat401AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat401Event3(event: Feat401AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat401Event4(event: Feat401AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat401Event5(event: Feat401AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat401Event6(event: Feat401AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat401Event7(event: Feat401AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat401Event8(event: Feat401AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat401Event9(event: Feat401AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat401Event10(event: Feat401AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat401Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat401Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat401Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat401Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat401Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat401Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat401Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat401Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat401Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat401Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat401(u: CoreUser): Feat401Projection1 =
    Feat401Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat401Projection1> {
    val list = java.util.ArrayList<Feat401Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat401(u)
    }
    return list
}
