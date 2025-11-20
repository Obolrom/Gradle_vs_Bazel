package com.romix.feature.feat370

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat370Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat370UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat370FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat370UserSummary
)

data class Feat370UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat370NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat370Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat370Config = Feat370Config()
) {

    fun loadSnapshot(userId: Long): Feat370NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat370NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat370UserSummary {
        return Feat370UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat370FeedItem> {
        val result = java.util.ArrayList<Feat370FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat370FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat370UiMapper {

    fun mapToUi(model: List<Feat370FeedItem>): Feat370UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat370UiModel(
            header = UiText("Feat370 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat370UiModel =
        Feat370UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat370UiModel =
        Feat370UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat370UiModel =
        Feat370UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat370Service(
    private val repository: Feat370Repository,
    private val uiMapper: Feat370UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat370UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat370UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat370UserItem1(val user: CoreUser, val label: String)
data class Feat370UserItem2(val user: CoreUser, val label: String)
data class Feat370UserItem3(val user: CoreUser, val label: String)
data class Feat370UserItem4(val user: CoreUser, val label: String)
data class Feat370UserItem5(val user: CoreUser, val label: String)
data class Feat370UserItem6(val user: CoreUser, val label: String)
data class Feat370UserItem7(val user: CoreUser, val label: String)
data class Feat370UserItem8(val user: CoreUser, val label: String)
data class Feat370UserItem9(val user: CoreUser, val label: String)
data class Feat370UserItem10(val user: CoreUser, val label: String)

data class Feat370StateBlock1(val state: Feat370UiModel, val checksum: Int)
data class Feat370StateBlock2(val state: Feat370UiModel, val checksum: Int)
data class Feat370StateBlock3(val state: Feat370UiModel, val checksum: Int)
data class Feat370StateBlock4(val state: Feat370UiModel, val checksum: Int)
data class Feat370StateBlock5(val state: Feat370UiModel, val checksum: Int)
data class Feat370StateBlock6(val state: Feat370UiModel, val checksum: Int)
data class Feat370StateBlock7(val state: Feat370UiModel, val checksum: Int)
data class Feat370StateBlock8(val state: Feat370UiModel, val checksum: Int)
data class Feat370StateBlock9(val state: Feat370UiModel, val checksum: Int)
data class Feat370StateBlock10(val state: Feat370UiModel, val checksum: Int)

fun buildFeat370UserItem(user: CoreUser, index: Int): Feat370UserItem1 {
    return Feat370UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat370StateBlock(model: Feat370UiModel): Feat370StateBlock1 {
    return Feat370StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat370UserSummary> {
    val list = java.util.ArrayList<Feat370UserSummary>(users.size)
    for (user in users) {
        list += Feat370UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat370UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat370UiModel {
    val summaries = (0 until count).map {
        Feat370UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat370UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat370UiModel> {
    val models = java.util.ArrayList<Feat370UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat370AnalyticsEvent1(val name: String, val value: String)
data class Feat370AnalyticsEvent2(val name: String, val value: String)
data class Feat370AnalyticsEvent3(val name: String, val value: String)
data class Feat370AnalyticsEvent4(val name: String, val value: String)
data class Feat370AnalyticsEvent5(val name: String, val value: String)
data class Feat370AnalyticsEvent6(val name: String, val value: String)
data class Feat370AnalyticsEvent7(val name: String, val value: String)
data class Feat370AnalyticsEvent8(val name: String, val value: String)
data class Feat370AnalyticsEvent9(val name: String, val value: String)
data class Feat370AnalyticsEvent10(val name: String, val value: String)

fun logFeat370Event1(event: Feat370AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat370Event2(event: Feat370AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat370Event3(event: Feat370AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat370Event4(event: Feat370AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat370Event5(event: Feat370AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat370Event6(event: Feat370AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat370Event7(event: Feat370AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat370Event8(event: Feat370AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat370Event9(event: Feat370AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat370Event10(event: Feat370AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat370Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat370Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat370Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat370Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat370Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat370Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat370Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat370Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat370Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat370Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat370(u: CoreUser): Feat370Projection1 =
    Feat370Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat370Projection1> {
    val list = java.util.ArrayList<Feat370Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat370(u)
    }
    return list
}
