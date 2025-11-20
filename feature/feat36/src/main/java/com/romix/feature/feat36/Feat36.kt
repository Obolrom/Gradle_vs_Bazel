package com.romix.feature.feat36

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat36Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat36UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat36FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat36UserSummary
)

data class Feat36UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat36NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat36Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat36Config = Feat36Config()
) {

    fun loadSnapshot(userId: Long): Feat36NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat36NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat36UserSummary {
        return Feat36UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat36FeedItem> {
        val result = java.util.ArrayList<Feat36FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat36FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat36UiMapper {

    fun mapToUi(model: List<Feat36FeedItem>): Feat36UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat36UiModel(
            header = UiText("Feat36 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat36UiModel =
        Feat36UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat36UiModel =
        Feat36UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat36UiModel =
        Feat36UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat36Service(
    private val repository: Feat36Repository,
    private val uiMapper: Feat36UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat36UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat36UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat36UserItem1(val user: CoreUser, val label: String)
data class Feat36UserItem2(val user: CoreUser, val label: String)
data class Feat36UserItem3(val user: CoreUser, val label: String)
data class Feat36UserItem4(val user: CoreUser, val label: String)
data class Feat36UserItem5(val user: CoreUser, val label: String)
data class Feat36UserItem6(val user: CoreUser, val label: String)
data class Feat36UserItem7(val user: CoreUser, val label: String)
data class Feat36UserItem8(val user: CoreUser, val label: String)
data class Feat36UserItem9(val user: CoreUser, val label: String)
data class Feat36UserItem10(val user: CoreUser, val label: String)

data class Feat36StateBlock1(val state: Feat36UiModel, val checksum: Int)
data class Feat36StateBlock2(val state: Feat36UiModel, val checksum: Int)
data class Feat36StateBlock3(val state: Feat36UiModel, val checksum: Int)
data class Feat36StateBlock4(val state: Feat36UiModel, val checksum: Int)
data class Feat36StateBlock5(val state: Feat36UiModel, val checksum: Int)
data class Feat36StateBlock6(val state: Feat36UiModel, val checksum: Int)
data class Feat36StateBlock7(val state: Feat36UiModel, val checksum: Int)
data class Feat36StateBlock8(val state: Feat36UiModel, val checksum: Int)
data class Feat36StateBlock9(val state: Feat36UiModel, val checksum: Int)
data class Feat36StateBlock10(val state: Feat36UiModel, val checksum: Int)

fun buildFeat36UserItem(user: CoreUser, index: Int): Feat36UserItem1 {
    return Feat36UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat36StateBlock(model: Feat36UiModel): Feat36StateBlock1 {
    return Feat36StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat36UserSummary> {
    val list = java.util.ArrayList<Feat36UserSummary>(users.size)
    for (user in users) {
        list += Feat36UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat36UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat36UiModel {
    val summaries = (0 until count).map {
        Feat36UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat36UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat36UiModel> {
    val models = java.util.ArrayList<Feat36UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat36AnalyticsEvent1(val name: String, val value: String)
data class Feat36AnalyticsEvent2(val name: String, val value: String)
data class Feat36AnalyticsEvent3(val name: String, val value: String)
data class Feat36AnalyticsEvent4(val name: String, val value: String)
data class Feat36AnalyticsEvent5(val name: String, val value: String)
data class Feat36AnalyticsEvent6(val name: String, val value: String)
data class Feat36AnalyticsEvent7(val name: String, val value: String)
data class Feat36AnalyticsEvent8(val name: String, val value: String)
data class Feat36AnalyticsEvent9(val name: String, val value: String)
data class Feat36AnalyticsEvent10(val name: String, val value: String)

fun logFeat36Event1(event: Feat36AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat36Event2(event: Feat36AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat36Event3(event: Feat36AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat36Event4(event: Feat36AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat36Event5(event: Feat36AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat36Event6(event: Feat36AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat36Event7(event: Feat36AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat36Event8(event: Feat36AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat36Event9(event: Feat36AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat36Event10(event: Feat36AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat36Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat36Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat36Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat36Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat36Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat36Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat36Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat36Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat36Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat36Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat36(u: CoreUser): Feat36Projection1 =
    Feat36Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat36Projection1> {
    val list = java.util.ArrayList<Feat36Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat36(u)
    }
    return list
}
