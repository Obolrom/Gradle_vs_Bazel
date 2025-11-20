package com.romix.feature.feat50

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat50Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat50UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat50FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat50UserSummary
)

data class Feat50UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat50NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat50Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat50Config = Feat50Config()
) {

    fun loadSnapshot(userId: Long): Feat50NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat50NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat50UserSummary {
        return Feat50UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat50FeedItem> {
        val result = java.util.ArrayList<Feat50FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat50FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat50UiMapper {

    fun mapToUi(model: List<Feat50FeedItem>): Feat50UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat50UiModel(
            header = UiText("Feat50 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat50UiModel =
        Feat50UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat50UiModel =
        Feat50UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat50UiModel =
        Feat50UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat50Service(
    private val repository: Feat50Repository,
    private val uiMapper: Feat50UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat50UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat50UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat50UserItem1(val user: CoreUser, val label: String)
data class Feat50UserItem2(val user: CoreUser, val label: String)
data class Feat50UserItem3(val user: CoreUser, val label: String)
data class Feat50UserItem4(val user: CoreUser, val label: String)
data class Feat50UserItem5(val user: CoreUser, val label: String)
data class Feat50UserItem6(val user: CoreUser, val label: String)
data class Feat50UserItem7(val user: CoreUser, val label: String)
data class Feat50UserItem8(val user: CoreUser, val label: String)
data class Feat50UserItem9(val user: CoreUser, val label: String)
data class Feat50UserItem10(val user: CoreUser, val label: String)

data class Feat50StateBlock1(val state: Feat50UiModel, val checksum: Int)
data class Feat50StateBlock2(val state: Feat50UiModel, val checksum: Int)
data class Feat50StateBlock3(val state: Feat50UiModel, val checksum: Int)
data class Feat50StateBlock4(val state: Feat50UiModel, val checksum: Int)
data class Feat50StateBlock5(val state: Feat50UiModel, val checksum: Int)
data class Feat50StateBlock6(val state: Feat50UiModel, val checksum: Int)
data class Feat50StateBlock7(val state: Feat50UiModel, val checksum: Int)
data class Feat50StateBlock8(val state: Feat50UiModel, val checksum: Int)
data class Feat50StateBlock9(val state: Feat50UiModel, val checksum: Int)
data class Feat50StateBlock10(val state: Feat50UiModel, val checksum: Int)

fun buildFeat50UserItem(user: CoreUser, index: Int): Feat50UserItem1 {
    return Feat50UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat50StateBlock(model: Feat50UiModel): Feat50StateBlock1 {
    return Feat50StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat50UserSummary> {
    val list = java.util.ArrayList<Feat50UserSummary>(users.size)
    for (user in users) {
        list += Feat50UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat50UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat50UiModel {
    val summaries = (0 until count).map {
        Feat50UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat50UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat50UiModel> {
    val models = java.util.ArrayList<Feat50UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat50AnalyticsEvent1(val name: String, val value: String)
data class Feat50AnalyticsEvent2(val name: String, val value: String)
data class Feat50AnalyticsEvent3(val name: String, val value: String)
data class Feat50AnalyticsEvent4(val name: String, val value: String)
data class Feat50AnalyticsEvent5(val name: String, val value: String)
data class Feat50AnalyticsEvent6(val name: String, val value: String)
data class Feat50AnalyticsEvent7(val name: String, val value: String)
data class Feat50AnalyticsEvent8(val name: String, val value: String)
data class Feat50AnalyticsEvent9(val name: String, val value: String)
data class Feat50AnalyticsEvent10(val name: String, val value: String)

fun logFeat50Event1(event: Feat50AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat50Event2(event: Feat50AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat50Event3(event: Feat50AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat50Event4(event: Feat50AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat50Event5(event: Feat50AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat50Event6(event: Feat50AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat50Event7(event: Feat50AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat50Event8(event: Feat50AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat50Event9(event: Feat50AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat50Event10(event: Feat50AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat50Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat50Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat50Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat50Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat50Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat50Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat50Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat50Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat50Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat50Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat50(u: CoreUser): Feat50Projection1 =
    Feat50Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat50Projection1> {
    val list = java.util.ArrayList<Feat50Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat50(u)
    }
    return list
}
