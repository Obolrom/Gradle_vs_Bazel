package com.romix.feature.feat17

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat17Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat17UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat17FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat17UserSummary
)

data class Feat17UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat17NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat17Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat17Config = Feat17Config()
) {

    fun loadSnapshot(userId: Long): Feat17NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat17NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat17UserSummary {
        return Feat17UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat17FeedItem> {
        val result = java.util.ArrayList<Feat17FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat17FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat17UiMapper {

    fun mapToUi(model: List<Feat17FeedItem>): Feat17UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat17UiModel(
            header = UiText("Feat17 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat17UiModel =
        Feat17UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat17UiModel =
        Feat17UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat17UiModel =
        Feat17UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat17Service(
    private val repository: Feat17Repository,
    private val uiMapper: Feat17UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat17UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat17UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat17UserItem1(val user: CoreUser, val label: String)
data class Feat17UserItem2(val user: CoreUser, val label: String)
data class Feat17UserItem3(val user: CoreUser, val label: String)
data class Feat17UserItem4(val user: CoreUser, val label: String)
data class Feat17UserItem5(val user: CoreUser, val label: String)
data class Feat17UserItem6(val user: CoreUser, val label: String)
data class Feat17UserItem7(val user: CoreUser, val label: String)
data class Feat17UserItem8(val user: CoreUser, val label: String)
data class Feat17UserItem9(val user: CoreUser, val label: String)
data class Feat17UserItem10(val user: CoreUser, val label: String)

data class Feat17StateBlock1(val state: Feat17UiModel, val checksum: Int)
data class Feat17StateBlock2(val state: Feat17UiModel, val checksum: Int)
data class Feat17StateBlock3(val state: Feat17UiModel, val checksum: Int)
data class Feat17StateBlock4(val state: Feat17UiModel, val checksum: Int)
data class Feat17StateBlock5(val state: Feat17UiModel, val checksum: Int)
data class Feat17StateBlock6(val state: Feat17UiModel, val checksum: Int)
data class Feat17StateBlock7(val state: Feat17UiModel, val checksum: Int)
data class Feat17StateBlock8(val state: Feat17UiModel, val checksum: Int)
data class Feat17StateBlock9(val state: Feat17UiModel, val checksum: Int)
data class Feat17StateBlock10(val state: Feat17UiModel, val checksum: Int)

fun buildFeat17UserItem(user: CoreUser, index: Int): Feat17UserItem1 {
    return Feat17UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat17StateBlock(model: Feat17UiModel): Feat17StateBlock1 {
    return Feat17StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat17UserSummary> {
    val list = java.util.ArrayList<Feat17UserSummary>(users.size)
    for (user in users) {
        list += Feat17UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat17UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat17UiModel {
    val summaries = (0 until count).map {
        Feat17UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat17UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat17UiModel> {
    val models = java.util.ArrayList<Feat17UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat17AnalyticsEvent1(val name: String, val value: String)
data class Feat17AnalyticsEvent2(val name: String, val value: String)
data class Feat17AnalyticsEvent3(val name: String, val value: String)
data class Feat17AnalyticsEvent4(val name: String, val value: String)
data class Feat17AnalyticsEvent5(val name: String, val value: String)
data class Feat17AnalyticsEvent6(val name: String, val value: String)
data class Feat17AnalyticsEvent7(val name: String, val value: String)
data class Feat17AnalyticsEvent8(val name: String, val value: String)
data class Feat17AnalyticsEvent9(val name: String, val value: String)
data class Feat17AnalyticsEvent10(val name: String, val value: String)

fun logFeat17Event1(event: Feat17AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat17Event2(event: Feat17AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat17Event3(event: Feat17AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat17Event4(event: Feat17AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat17Event5(event: Feat17AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat17Event6(event: Feat17AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat17Event7(event: Feat17AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat17Event8(event: Feat17AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat17Event9(event: Feat17AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat17Event10(event: Feat17AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat17Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat17Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat17Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat17Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat17Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat17Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat17Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat17Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat17Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat17Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat17(u: CoreUser): Feat17Projection1 =
    Feat17Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat17Projection1> {
    val list = java.util.ArrayList<Feat17Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat17(u)
    }
    return list
}
