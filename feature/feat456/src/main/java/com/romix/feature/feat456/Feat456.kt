package com.romix.feature.feat456

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat456Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat456UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat456FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat456UserSummary
)

data class Feat456UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat456NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat456Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat456Config = Feat456Config()
) {

    fun loadSnapshot(userId: Long): Feat456NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat456NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat456UserSummary {
        return Feat456UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat456FeedItem> {
        val result = java.util.ArrayList<Feat456FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat456FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat456UiMapper {

    fun mapToUi(model: List<Feat456FeedItem>): Feat456UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat456UiModel(
            header = UiText("Feat456 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat456UiModel =
        Feat456UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat456UiModel =
        Feat456UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat456UiModel =
        Feat456UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat456Service(
    private val repository: Feat456Repository,
    private val uiMapper: Feat456UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat456UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat456UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat456UserItem1(val user: CoreUser, val label: String)
data class Feat456UserItem2(val user: CoreUser, val label: String)
data class Feat456UserItem3(val user: CoreUser, val label: String)
data class Feat456UserItem4(val user: CoreUser, val label: String)
data class Feat456UserItem5(val user: CoreUser, val label: String)
data class Feat456UserItem6(val user: CoreUser, val label: String)
data class Feat456UserItem7(val user: CoreUser, val label: String)
data class Feat456UserItem8(val user: CoreUser, val label: String)
data class Feat456UserItem9(val user: CoreUser, val label: String)
data class Feat456UserItem10(val user: CoreUser, val label: String)

data class Feat456StateBlock1(val state: Feat456UiModel, val checksum: Int)
data class Feat456StateBlock2(val state: Feat456UiModel, val checksum: Int)
data class Feat456StateBlock3(val state: Feat456UiModel, val checksum: Int)
data class Feat456StateBlock4(val state: Feat456UiModel, val checksum: Int)
data class Feat456StateBlock5(val state: Feat456UiModel, val checksum: Int)
data class Feat456StateBlock6(val state: Feat456UiModel, val checksum: Int)
data class Feat456StateBlock7(val state: Feat456UiModel, val checksum: Int)
data class Feat456StateBlock8(val state: Feat456UiModel, val checksum: Int)
data class Feat456StateBlock9(val state: Feat456UiModel, val checksum: Int)
data class Feat456StateBlock10(val state: Feat456UiModel, val checksum: Int)

fun buildFeat456UserItem(user: CoreUser, index: Int): Feat456UserItem1 {
    return Feat456UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat456StateBlock(model: Feat456UiModel): Feat456StateBlock1 {
    return Feat456StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat456UserSummary> {
    val list = java.util.ArrayList<Feat456UserSummary>(users.size)
    for (user in users) {
        list += Feat456UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat456UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat456UiModel {
    val summaries = (0 until count).map {
        Feat456UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat456UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat456UiModel> {
    val models = java.util.ArrayList<Feat456UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat456AnalyticsEvent1(val name: String, val value: String)
data class Feat456AnalyticsEvent2(val name: String, val value: String)
data class Feat456AnalyticsEvent3(val name: String, val value: String)
data class Feat456AnalyticsEvent4(val name: String, val value: String)
data class Feat456AnalyticsEvent5(val name: String, val value: String)
data class Feat456AnalyticsEvent6(val name: String, val value: String)
data class Feat456AnalyticsEvent7(val name: String, val value: String)
data class Feat456AnalyticsEvent8(val name: String, val value: String)
data class Feat456AnalyticsEvent9(val name: String, val value: String)
data class Feat456AnalyticsEvent10(val name: String, val value: String)

fun logFeat456Event1(event: Feat456AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat456Event2(event: Feat456AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat456Event3(event: Feat456AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat456Event4(event: Feat456AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat456Event5(event: Feat456AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat456Event6(event: Feat456AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat456Event7(event: Feat456AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat456Event8(event: Feat456AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat456Event9(event: Feat456AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat456Event10(event: Feat456AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat456Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat456Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat456Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat456Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat456Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat456Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat456Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat456Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat456Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat456Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat456(u: CoreUser): Feat456Projection1 =
    Feat456Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat456Projection1> {
    val list = java.util.ArrayList<Feat456Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat456(u)
    }
    return list
}
