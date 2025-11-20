package com.romix.feature.feat688

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat688Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat688UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat688FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat688UserSummary
)

data class Feat688UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat688NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat688Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat688Config = Feat688Config()
) {

    fun loadSnapshot(userId: Long): Feat688NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat688NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat688UserSummary {
        return Feat688UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat688FeedItem> {
        val result = java.util.ArrayList<Feat688FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat688FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat688UiMapper {

    fun mapToUi(model: List<Feat688FeedItem>): Feat688UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat688UiModel(
            header = UiText("Feat688 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat688UiModel =
        Feat688UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat688UiModel =
        Feat688UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat688UiModel =
        Feat688UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat688Service(
    private val repository: Feat688Repository,
    private val uiMapper: Feat688UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat688UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat688UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat688UserItem1(val user: CoreUser, val label: String)
data class Feat688UserItem2(val user: CoreUser, val label: String)
data class Feat688UserItem3(val user: CoreUser, val label: String)
data class Feat688UserItem4(val user: CoreUser, val label: String)
data class Feat688UserItem5(val user: CoreUser, val label: String)
data class Feat688UserItem6(val user: CoreUser, val label: String)
data class Feat688UserItem7(val user: CoreUser, val label: String)
data class Feat688UserItem8(val user: CoreUser, val label: String)
data class Feat688UserItem9(val user: CoreUser, val label: String)
data class Feat688UserItem10(val user: CoreUser, val label: String)

data class Feat688StateBlock1(val state: Feat688UiModel, val checksum: Int)
data class Feat688StateBlock2(val state: Feat688UiModel, val checksum: Int)
data class Feat688StateBlock3(val state: Feat688UiModel, val checksum: Int)
data class Feat688StateBlock4(val state: Feat688UiModel, val checksum: Int)
data class Feat688StateBlock5(val state: Feat688UiModel, val checksum: Int)
data class Feat688StateBlock6(val state: Feat688UiModel, val checksum: Int)
data class Feat688StateBlock7(val state: Feat688UiModel, val checksum: Int)
data class Feat688StateBlock8(val state: Feat688UiModel, val checksum: Int)
data class Feat688StateBlock9(val state: Feat688UiModel, val checksum: Int)
data class Feat688StateBlock10(val state: Feat688UiModel, val checksum: Int)

fun buildFeat688UserItem(user: CoreUser, index: Int): Feat688UserItem1 {
    return Feat688UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat688StateBlock(model: Feat688UiModel): Feat688StateBlock1 {
    return Feat688StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat688UserSummary> {
    val list = java.util.ArrayList<Feat688UserSummary>(users.size)
    for (user in users) {
        list += Feat688UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat688UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat688UiModel {
    val summaries = (0 until count).map {
        Feat688UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat688UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat688UiModel> {
    val models = java.util.ArrayList<Feat688UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat688AnalyticsEvent1(val name: String, val value: String)
data class Feat688AnalyticsEvent2(val name: String, val value: String)
data class Feat688AnalyticsEvent3(val name: String, val value: String)
data class Feat688AnalyticsEvent4(val name: String, val value: String)
data class Feat688AnalyticsEvent5(val name: String, val value: String)
data class Feat688AnalyticsEvent6(val name: String, val value: String)
data class Feat688AnalyticsEvent7(val name: String, val value: String)
data class Feat688AnalyticsEvent8(val name: String, val value: String)
data class Feat688AnalyticsEvent9(val name: String, val value: String)
data class Feat688AnalyticsEvent10(val name: String, val value: String)

fun logFeat688Event1(event: Feat688AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat688Event2(event: Feat688AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat688Event3(event: Feat688AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat688Event4(event: Feat688AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat688Event5(event: Feat688AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat688Event6(event: Feat688AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat688Event7(event: Feat688AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat688Event8(event: Feat688AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat688Event9(event: Feat688AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat688Event10(event: Feat688AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat688Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat688Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat688Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat688Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat688Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat688Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat688Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat688Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat688Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat688Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat688(u: CoreUser): Feat688Projection1 =
    Feat688Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat688Projection1> {
    val list = java.util.ArrayList<Feat688Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat688(u)
    }
    return list
}
