package com.romix.feature.feat685

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat685Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat685UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat685FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat685UserSummary
)

data class Feat685UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat685NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat685Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat685Config = Feat685Config()
) {

    fun loadSnapshot(userId: Long): Feat685NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat685NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat685UserSummary {
        return Feat685UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat685FeedItem> {
        val result = java.util.ArrayList<Feat685FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat685FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat685UiMapper {

    fun mapToUi(model: List<Feat685FeedItem>): Feat685UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat685UiModel(
            header = UiText("Feat685 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat685UiModel =
        Feat685UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat685UiModel =
        Feat685UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat685UiModel =
        Feat685UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat685Service(
    private val repository: Feat685Repository,
    private val uiMapper: Feat685UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat685UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat685UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat685UserItem1(val user: CoreUser, val label: String)
data class Feat685UserItem2(val user: CoreUser, val label: String)
data class Feat685UserItem3(val user: CoreUser, val label: String)
data class Feat685UserItem4(val user: CoreUser, val label: String)
data class Feat685UserItem5(val user: CoreUser, val label: String)
data class Feat685UserItem6(val user: CoreUser, val label: String)
data class Feat685UserItem7(val user: CoreUser, val label: String)
data class Feat685UserItem8(val user: CoreUser, val label: String)
data class Feat685UserItem9(val user: CoreUser, val label: String)
data class Feat685UserItem10(val user: CoreUser, val label: String)

data class Feat685StateBlock1(val state: Feat685UiModel, val checksum: Int)
data class Feat685StateBlock2(val state: Feat685UiModel, val checksum: Int)
data class Feat685StateBlock3(val state: Feat685UiModel, val checksum: Int)
data class Feat685StateBlock4(val state: Feat685UiModel, val checksum: Int)
data class Feat685StateBlock5(val state: Feat685UiModel, val checksum: Int)
data class Feat685StateBlock6(val state: Feat685UiModel, val checksum: Int)
data class Feat685StateBlock7(val state: Feat685UiModel, val checksum: Int)
data class Feat685StateBlock8(val state: Feat685UiModel, val checksum: Int)
data class Feat685StateBlock9(val state: Feat685UiModel, val checksum: Int)
data class Feat685StateBlock10(val state: Feat685UiModel, val checksum: Int)

fun buildFeat685UserItem(user: CoreUser, index: Int): Feat685UserItem1 {
    return Feat685UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat685StateBlock(model: Feat685UiModel): Feat685StateBlock1 {
    return Feat685StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat685UserSummary> {
    val list = java.util.ArrayList<Feat685UserSummary>(users.size)
    for (user in users) {
        list += Feat685UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat685UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat685UiModel {
    val summaries = (0 until count).map {
        Feat685UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat685UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat685UiModel> {
    val models = java.util.ArrayList<Feat685UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat685AnalyticsEvent1(val name: String, val value: String)
data class Feat685AnalyticsEvent2(val name: String, val value: String)
data class Feat685AnalyticsEvent3(val name: String, val value: String)
data class Feat685AnalyticsEvent4(val name: String, val value: String)
data class Feat685AnalyticsEvent5(val name: String, val value: String)
data class Feat685AnalyticsEvent6(val name: String, val value: String)
data class Feat685AnalyticsEvent7(val name: String, val value: String)
data class Feat685AnalyticsEvent8(val name: String, val value: String)
data class Feat685AnalyticsEvent9(val name: String, val value: String)
data class Feat685AnalyticsEvent10(val name: String, val value: String)

fun logFeat685Event1(event: Feat685AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat685Event2(event: Feat685AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat685Event3(event: Feat685AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat685Event4(event: Feat685AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat685Event5(event: Feat685AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat685Event6(event: Feat685AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat685Event7(event: Feat685AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat685Event8(event: Feat685AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat685Event9(event: Feat685AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat685Event10(event: Feat685AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat685Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat685Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat685Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat685Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat685Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat685Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat685Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat685Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat685Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat685Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat685(u: CoreUser): Feat685Projection1 =
    Feat685Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat685Projection1> {
    val list = java.util.ArrayList<Feat685Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat685(u)
    }
    return list
}
