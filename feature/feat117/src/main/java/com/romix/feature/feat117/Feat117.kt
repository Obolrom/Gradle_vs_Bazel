package com.romix.feature.feat117

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat117Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat117UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat117FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat117UserSummary
)

data class Feat117UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat117NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat117Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat117Config = Feat117Config()
) {

    fun loadSnapshot(userId: Long): Feat117NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat117NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat117UserSummary {
        return Feat117UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat117FeedItem> {
        val result = java.util.ArrayList<Feat117FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat117FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat117UiMapper {

    fun mapToUi(model: List<Feat117FeedItem>): Feat117UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat117UiModel(
            header = UiText("Feat117 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat117UiModel =
        Feat117UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat117UiModel =
        Feat117UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat117UiModel =
        Feat117UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat117Service(
    private val repository: Feat117Repository,
    private val uiMapper: Feat117UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat117UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat117UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat117UserItem1(val user: CoreUser, val label: String)
data class Feat117UserItem2(val user: CoreUser, val label: String)
data class Feat117UserItem3(val user: CoreUser, val label: String)
data class Feat117UserItem4(val user: CoreUser, val label: String)
data class Feat117UserItem5(val user: CoreUser, val label: String)
data class Feat117UserItem6(val user: CoreUser, val label: String)
data class Feat117UserItem7(val user: CoreUser, val label: String)
data class Feat117UserItem8(val user: CoreUser, val label: String)
data class Feat117UserItem9(val user: CoreUser, val label: String)
data class Feat117UserItem10(val user: CoreUser, val label: String)

data class Feat117StateBlock1(val state: Feat117UiModel, val checksum: Int)
data class Feat117StateBlock2(val state: Feat117UiModel, val checksum: Int)
data class Feat117StateBlock3(val state: Feat117UiModel, val checksum: Int)
data class Feat117StateBlock4(val state: Feat117UiModel, val checksum: Int)
data class Feat117StateBlock5(val state: Feat117UiModel, val checksum: Int)
data class Feat117StateBlock6(val state: Feat117UiModel, val checksum: Int)
data class Feat117StateBlock7(val state: Feat117UiModel, val checksum: Int)
data class Feat117StateBlock8(val state: Feat117UiModel, val checksum: Int)
data class Feat117StateBlock9(val state: Feat117UiModel, val checksum: Int)
data class Feat117StateBlock10(val state: Feat117UiModel, val checksum: Int)

fun buildFeat117UserItem(user: CoreUser, index: Int): Feat117UserItem1 {
    return Feat117UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat117StateBlock(model: Feat117UiModel): Feat117StateBlock1 {
    return Feat117StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat117UserSummary> {
    val list = java.util.ArrayList<Feat117UserSummary>(users.size)
    for (user in users) {
        list += Feat117UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat117UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat117UiModel {
    val summaries = (0 until count).map {
        Feat117UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat117UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat117UiModel> {
    val models = java.util.ArrayList<Feat117UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat117AnalyticsEvent1(val name: String, val value: String)
data class Feat117AnalyticsEvent2(val name: String, val value: String)
data class Feat117AnalyticsEvent3(val name: String, val value: String)
data class Feat117AnalyticsEvent4(val name: String, val value: String)
data class Feat117AnalyticsEvent5(val name: String, val value: String)
data class Feat117AnalyticsEvent6(val name: String, val value: String)
data class Feat117AnalyticsEvent7(val name: String, val value: String)
data class Feat117AnalyticsEvent8(val name: String, val value: String)
data class Feat117AnalyticsEvent9(val name: String, val value: String)
data class Feat117AnalyticsEvent10(val name: String, val value: String)

fun logFeat117Event1(event: Feat117AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat117Event2(event: Feat117AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat117Event3(event: Feat117AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat117Event4(event: Feat117AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat117Event5(event: Feat117AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat117Event6(event: Feat117AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat117Event7(event: Feat117AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat117Event8(event: Feat117AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat117Event9(event: Feat117AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat117Event10(event: Feat117AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat117Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat117Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat117Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat117Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat117Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat117Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat117Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat117Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat117Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat117Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat117(u: CoreUser): Feat117Projection1 =
    Feat117Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat117Projection1> {
    val list = java.util.ArrayList<Feat117Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat117(u)
    }
    return list
}
