package com.romix.feature.feat176

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat176Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat176UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat176FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat176UserSummary
)

data class Feat176UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat176NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat176Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat176Config = Feat176Config()
) {

    fun loadSnapshot(userId: Long): Feat176NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat176NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat176UserSummary {
        return Feat176UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat176FeedItem> {
        val result = java.util.ArrayList<Feat176FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat176FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat176UiMapper {

    fun mapToUi(model: List<Feat176FeedItem>): Feat176UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat176UiModel(
            header = UiText("Feat176 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat176UiModel =
        Feat176UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat176UiModel =
        Feat176UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat176UiModel =
        Feat176UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat176Service(
    private val repository: Feat176Repository,
    private val uiMapper: Feat176UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat176UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat176UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat176UserItem1(val user: CoreUser, val label: String)
data class Feat176UserItem2(val user: CoreUser, val label: String)
data class Feat176UserItem3(val user: CoreUser, val label: String)
data class Feat176UserItem4(val user: CoreUser, val label: String)
data class Feat176UserItem5(val user: CoreUser, val label: String)
data class Feat176UserItem6(val user: CoreUser, val label: String)
data class Feat176UserItem7(val user: CoreUser, val label: String)
data class Feat176UserItem8(val user: CoreUser, val label: String)
data class Feat176UserItem9(val user: CoreUser, val label: String)
data class Feat176UserItem10(val user: CoreUser, val label: String)

data class Feat176StateBlock1(val state: Feat176UiModel, val checksum: Int)
data class Feat176StateBlock2(val state: Feat176UiModel, val checksum: Int)
data class Feat176StateBlock3(val state: Feat176UiModel, val checksum: Int)
data class Feat176StateBlock4(val state: Feat176UiModel, val checksum: Int)
data class Feat176StateBlock5(val state: Feat176UiModel, val checksum: Int)
data class Feat176StateBlock6(val state: Feat176UiModel, val checksum: Int)
data class Feat176StateBlock7(val state: Feat176UiModel, val checksum: Int)
data class Feat176StateBlock8(val state: Feat176UiModel, val checksum: Int)
data class Feat176StateBlock9(val state: Feat176UiModel, val checksum: Int)
data class Feat176StateBlock10(val state: Feat176UiModel, val checksum: Int)

fun buildFeat176UserItem(user: CoreUser, index: Int): Feat176UserItem1 {
    return Feat176UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat176StateBlock(model: Feat176UiModel): Feat176StateBlock1 {
    return Feat176StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat176UserSummary> {
    val list = java.util.ArrayList<Feat176UserSummary>(users.size)
    for (user in users) {
        list += Feat176UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat176UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat176UiModel {
    val summaries = (0 until count).map {
        Feat176UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat176UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat176UiModel> {
    val models = java.util.ArrayList<Feat176UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat176AnalyticsEvent1(val name: String, val value: String)
data class Feat176AnalyticsEvent2(val name: String, val value: String)
data class Feat176AnalyticsEvent3(val name: String, val value: String)
data class Feat176AnalyticsEvent4(val name: String, val value: String)
data class Feat176AnalyticsEvent5(val name: String, val value: String)
data class Feat176AnalyticsEvent6(val name: String, val value: String)
data class Feat176AnalyticsEvent7(val name: String, val value: String)
data class Feat176AnalyticsEvent8(val name: String, val value: String)
data class Feat176AnalyticsEvent9(val name: String, val value: String)
data class Feat176AnalyticsEvent10(val name: String, val value: String)

fun logFeat176Event1(event: Feat176AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat176Event2(event: Feat176AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat176Event3(event: Feat176AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat176Event4(event: Feat176AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat176Event5(event: Feat176AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat176Event6(event: Feat176AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat176Event7(event: Feat176AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat176Event8(event: Feat176AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat176Event9(event: Feat176AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat176Event10(event: Feat176AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat176Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat176Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat176Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat176Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat176Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat176Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat176Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat176Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat176Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat176Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat176(u: CoreUser): Feat176Projection1 =
    Feat176Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat176Projection1> {
    val list = java.util.ArrayList<Feat176Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat176(u)
    }
    return list
}
