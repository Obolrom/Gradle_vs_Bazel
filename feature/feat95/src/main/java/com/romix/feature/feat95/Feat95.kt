package com.romix.feature.feat95

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat95Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat95UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat95FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat95UserSummary
)

data class Feat95UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat95NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat95Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat95Config = Feat95Config()
) {

    fun loadSnapshot(userId: Long): Feat95NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat95NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat95UserSummary {
        return Feat95UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat95FeedItem> {
        val result = java.util.ArrayList<Feat95FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat95FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat95UiMapper {

    fun mapToUi(model: List<Feat95FeedItem>): Feat95UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat95UiModel(
            header = UiText("Feat95 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat95UiModel =
        Feat95UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat95UiModel =
        Feat95UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat95UiModel =
        Feat95UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat95Service(
    private val repository: Feat95Repository,
    private val uiMapper: Feat95UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat95UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat95UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat95UserItem1(val user: CoreUser, val label: String)
data class Feat95UserItem2(val user: CoreUser, val label: String)
data class Feat95UserItem3(val user: CoreUser, val label: String)
data class Feat95UserItem4(val user: CoreUser, val label: String)
data class Feat95UserItem5(val user: CoreUser, val label: String)
data class Feat95UserItem6(val user: CoreUser, val label: String)
data class Feat95UserItem7(val user: CoreUser, val label: String)
data class Feat95UserItem8(val user: CoreUser, val label: String)
data class Feat95UserItem9(val user: CoreUser, val label: String)
data class Feat95UserItem10(val user: CoreUser, val label: String)

data class Feat95StateBlock1(val state: Feat95UiModel, val checksum: Int)
data class Feat95StateBlock2(val state: Feat95UiModel, val checksum: Int)
data class Feat95StateBlock3(val state: Feat95UiModel, val checksum: Int)
data class Feat95StateBlock4(val state: Feat95UiModel, val checksum: Int)
data class Feat95StateBlock5(val state: Feat95UiModel, val checksum: Int)
data class Feat95StateBlock6(val state: Feat95UiModel, val checksum: Int)
data class Feat95StateBlock7(val state: Feat95UiModel, val checksum: Int)
data class Feat95StateBlock8(val state: Feat95UiModel, val checksum: Int)
data class Feat95StateBlock9(val state: Feat95UiModel, val checksum: Int)
data class Feat95StateBlock10(val state: Feat95UiModel, val checksum: Int)

fun buildFeat95UserItem(user: CoreUser, index: Int): Feat95UserItem1 {
    return Feat95UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat95StateBlock(model: Feat95UiModel): Feat95StateBlock1 {
    return Feat95StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat95UserSummary> {
    val list = java.util.ArrayList<Feat95UserSummary>(users.size)
    for (user in users) {
        list += Feat95UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat95UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat95UiModel {
    val summaries = (0 until count).map {
        Feat95UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat95UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat95UiModel> {
    val models = java.util.ArrayList<Feat95UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat95AnalyticsEvent1(val name: String, val value: String)
data class Feat95AnalyticsEvent2(val name: String, val value: String)
data class Feat95AnalyticsEvent3(val name: String, val value: String)
data class Feat95AnalyticsEvent4(val name: String, val value: String)
data class Feat95AnalyticsEvent5(val name: String, val value: String)
data class Feat95AnalyticsEvent6(val name: String, val value: String)
data class Feat95AnalyticsEvent7(val name: String, val value: String)
data class Feat95AnalyticsEvent8(val name: String, val value: String)
data class Feat95AnalyticsEvent9(val name: String, val value: String)
data class Feat95AnalyticsEvent10(val name: String, val value: String)

fun logFeat95Event1(event: Feat95AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat95Event2(event: Feat95AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat95Event3(event: Feat95AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat95Event4(event: Feat95AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat95Event5(event: Feat95AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat95Event6(event: Feat95AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat95Event7(event: Feat95AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat95Event8(event: Feat95AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat95Event9(event: Feat95AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat95Event10(event: Feat95AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat95Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat95Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat95Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat95Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat95Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat95Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat95Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat95Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat95Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat95Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat95(u: CoreUser): Feat95Projection1 =
    Feat95Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat95Projection1> {
    val list = java.util.ArrayList<Feat95Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat95(u)
    }
    return list
}
