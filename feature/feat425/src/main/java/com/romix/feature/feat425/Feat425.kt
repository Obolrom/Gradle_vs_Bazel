package com.romix.feature.feat425

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat425Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat425UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat425FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat425UserSummary
)

data class Feat425UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat425NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat425Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat425Config = Feat425Config()
) {

    fun loadSnapshot(userId: Long): Feat425NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat425NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat425UserSummary {
        return Feat425UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat425FeedItem> {
        val result = java.util.ArrayList<Feat425FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat425FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat425UiMapper {

    fun mapToUi(model: List<Feat425FeedItem>): Feat425UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat425UiModel(
            header = UiText("Feat425 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat425UiModel =
        Feat425UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat425UiModel =
        Feat425UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat425UiModel =
        Feat425UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat425Service(
    private val repository: Feat425Repository,
    private val uiMapper: Feat425UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat425UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat425UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat425UserItem1(val user: CoreUser, val label: String)
data class Feat425UserItem2(val user: CoreUser, val label: String)
data class Feat425UserItem3(val user: CoreUser, val label: String)
data class Feat425UserItem4(val user: CoreUser, val label: String)
data class Feat425UserItem5(val user: CoreUser, val label: String)
data class Feat425UserItem6(val user: CoreUser, val label: String)
data class Feat425UserItem7(val user: CoreUser, val label: String)
data class Feat425UserItem8(val user: CoreUser, val label: String)
data class Feat425UserItem9(val user: CoreUser, val label: String)
data class Feat425UserItem10(val user: CoreUser, val label: String)

data class Feat425StateBlock1(val state: Feat425UiModel, val checksum: Int)
data class Feat425StateBlock2(val state: Feat425UiModel, val checksum: Int)
data class Feat425StateBlock3(val state: Feat425UiModel, val checksum: Int)
data class Feat425StateBlock4(val state: Feat425UiModel, val checksum: Int)
data class Feat425StateBlock5(val state: Feat425UiModel, val checksum: Int)
data class Feat425StateBlock6(val state: Feat425UiModel, val checksum: Int)
data class Feat425StateBlock7(val state: Feat425UiModel, val checksum: Int)
data class Feat425StateBlock8(val state: Feat425UiModel, val checksum: Int)
data class Feat425StateBlock9(val state: Feat425UiModel, val checksum: Int)
data class Feat425StateBlock10(val state: Feat425UiModel, val checksum: Int)

fun buildFeat425UserItem(user: CoreUser, index: Int): Feat425UserItem1 {
    return Feat425UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat425StateBlock(model: Feat425UiModel): Feat425StateBlock1 {
    return Feat425StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat425UserSummary> {
    val list = java.util.ArrayList<Feat425UserSummary>(users.size)
    for (user in users) {
        list += Feat425UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat425UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat425UiModel {
    val summaries = (0 until count).map {
        Feat425UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat425UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat425UiModel> {
    val models = java.util.ArrayList<Feat425UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat425AnalyticsEvent1(val name: String, val value: String)
data class Feat425AnalyticsEvent2(val name: String, val value: String)
data class Feat425AnalyticsEvent3(val name: String, val value: String)
data class Feat425AnalyticsEvent4(val name: String, val value: String)
data class Feat425AnalyticsEvent5(val name: String, val value: String)
data class Feat425AnalyticsEvent6(val name: String, val value: String)
data class Feat425AnalyticsEvent7(val name: String, val value: String)
data class Feat425AnalyticsEvent8(val name: String, val value: String)
data class Feat425AnalyticsEvent9(val name: String, val value: String)
data class Feat425AnalyticsEvent10(val name: String, val value: String)

fun logFeat425Event1(event: Feat425AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat425Event2(event: Feat425AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat425Event3(event: Feat425AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat425Event4(event: Feat425AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat425Event5(event: Feat425AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat425Event6(event: Feat425AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat425Event7(event: Feat425AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat425Event8(event: Feat425AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat425Event9(event: Feat425AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat425Event10(event: Feat425AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat425Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat425Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat425Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat425Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat425Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat425Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat425Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat425Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat425Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat425Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat425(u: CoreUser): Feat425Projection1 =
    Feat425Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat425Projection1> {
    val list = java.util.ArrayList<Feat425Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat425(u)
    }
    return list
}
