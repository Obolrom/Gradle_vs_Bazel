package com.romix.feature.feat172

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat172Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat172UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat172FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat172UserSummary
)

data class Feat172UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat172NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat172Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat172Config = Feat172Config()
) {

    fun loadSnapshot(userId: Long): Feat172NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat172NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat172UserSummary {
        return Feat172UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat172FeedItem> {
        val result = java.util.ArrayList<Feat172FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat172FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat172UiMapper {

    fun mapToUi(model: List<Feat172FeedItem>): Feat172UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat172UiModel(
            header = UiText("Feat172 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat172UiModel =
        Feat172UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat172UiModel =
        Feat172UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat172UiModel =
        Feat172UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat172Service(
    private val repository: Feat172Repository,
    private val uiMapper: Feat172UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat172UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat172UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat172UserItem1(val user: CoreUser, val label: String)
data class Feat172UserItem2(val user: CoreUser, val label: String)
data class Feat172UserItem3(val user: CoreUser, val label: String)
data class Feat172UserItem4(val user: CoreUser, val label: String)
data class Feat172UserItem5(val user: CoreUser, val label: String)
data class Feat172UserItem6(val user: CoreUser, val label: String)
data class Feat172UserItem7(val user: CoreUser, val label: String)
data class Feat172UserItem8(val user: CoreUser, val label: String)
data class Feat172UserItem9(val user: CoreUser, val label: String)
data class Feat172UserItem10(val user: CoreUser, val label: String)

data class Feat172StateBlock1(val state: Feat172UiModel, val checksum: Int)
data class Feat172StateBlock2(val state: Feat172UiModel, val checksum: Int)
data class Feat172StateBlock3(val state: Feat172UiModel, val checksum: Int)
data class Feat172StateBlock4(val state: Feat172UiModel, val checksum: Int)
data class Feat172StateBlock5(val state: Feat172UiModel, val checksum: Int)
data class Feat172StateBlock6(val state: Feat172UiModel, val checksum: Int)
data class Feat172StateBlock7(val state: Feat172UiModel, val checksum: Int)
data class Feat172StateBlock8(val state: Feat172UiModel, val checksum: Int)
data class Feat172StateBlock9(val state: Feat172UiModel, val checksum: Int)
data class Feat172StateBlock10(val state: Feat172UiModel, val checksum: Int)

fun buildFeat172UserItem(user: CoreUser, index: Int): Feat172UserItem1 {
    return Feat172UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat172StateBlock(model: Feat172UiModel): Feat172StateBlock1 {
    return Feat172StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat172UserSummary> {
    val list = java.util.ArrayList<Feat172UserSummary>(users.size)
    for (user in users) {
        list += Feat172UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat172UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat172UiModel {
    val summaries = (0 until count).map {
        Feat172UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat172UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat172UiModel> {
    val models = java.util.ArrayList<Feat172UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat172AnalyticsEvent1(val name: String, val value: String)
data class Feat172AnalyticsEvent2(val name: String, val value: String)
data class Feat172AnalyticsEvent3(val name: String, val value: String)
data class Feat172AnalyticsEvent4(val name: String, val value: String)
data class Feat172AnalyticsEvent5(val name: String, val value: String)
data class Feat172AnalyticsEvent6(val name: String, val value: String)
data class Feat172AnalyticsEvent7(val name: String, val value: String)
data class Feat172AnalyticsEvent8(val name: String, val value: String)
data class Feat172AnalyticsEvent9(val name: String, val value: String)
data class Feat172AnalyticsEvent10(val name: String, val value: String)

fun logFeat172Event1(event: Feat172AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat172Event2(event: Feat172AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat172Event3(event: Feat172AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat172Event4(event: Feat172AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat172Event5(event: Feat172AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat172Event6(event: Feat172AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat172Event7(event: Feat172AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat172Event8(event: Feat172AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat172Event9(event: Feat172AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat172Event10(event: Feat172AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat172Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat172Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat172Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat172Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat172Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat172Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat172Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat172Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat172Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat172Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat172(u: CoreUser): Feat172Projection1 =
    Feat172Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat172Projection1> {
    val list = java.util.ArrayList<Feat172Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat172(u)
    }
    return list
}
