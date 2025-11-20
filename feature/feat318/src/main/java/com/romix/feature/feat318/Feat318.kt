package com.romix.feature.feat318

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat318Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat318UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat318FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat318UserSummary
)

data class Feat318UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat318NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat318Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat318Config = Feat318Config()
) {

    fun loadSnapshot(userId: Long): Feat318NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat318NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat318UserSummary {
        return Feat318UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat318FeedItem> {
        val result = java.util.ArrayList<Feat318FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat318FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat318UiMapper {

    fun mapToUi(model: List<Feat318FeedItem>): Feat318UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat318UiModel(
            header = UiText("Feat318 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat318UiModel =
        Feat318UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat318UiModel =
        Feat318UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat318UiModel =
        Feat318UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat318Service(
    private val repository: Feat318Repository,
    private val uiMapper: Feat318UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat318UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat318UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat318UserItem1(val user: CoreUser, val label: String)
data class Feat318UserItem2(val user: CoreUser, val label: String)
data class Feat318UserItem3(val user: CoreUser, val label: String)
data class Feat318UserItem4(val user: CoreUser, val label: String)
data class Feat318UserItem5(val user: CoreUser, val label: String)
data class Feat318UserItem6(val user: CoreUser, val label: String)
data class Feat318UserItem7(val user: CoreUser, val label: String)
data class Feat318UserItem8(val user: CoreUser, val label: String)
data class Feat318UserItem9(val user: CoreUser, val label: String)
data class Feat318UserItem10(val user: CoreUser, val label: String)

data class Feat318StateBlock1(val state: Feat318UiModel, val checksum: Int)
data class Feat318StateBlock2(val state: Feat318UiModel, val checksum: Int)
data class Feat318StateBlock3(val state: Feat318UiModel, val checksum: Int)
data class Feat318StateBlock4(val state: Feat318UiModel, val checksum: Int)
data class Feat318StateBlock5(val state: Feat318UiModel, val checksum: Int)
data class Feat318StateBlock6(val state: Feat318UiModel, val checksum: Int)
data class Feat318StateBlock7(val state: Feat318UiModel, val checksum: Int)
data class Feat318StateBlock8(val state: Feat318UiModel, val checksum: Int)
data class Feat318StateBlock9(val state: Feat318UiModel, val checksum: Int)
data class Feat318StateBlock10(val state: Feat318UiModel, val checksum: Int)

fun buildFeat318UserItem(user: CoreUser, index: Int): Feat318UserItem1 {
    return Feat318UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat318StateBlock(model: Feat318UiModel): Feat318StateBlock1 {
    return Feat318StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat318UserSummary> {
    val list = java.util.ArrayList<Feat318UserSummary>(users.size)
    for (user in users) {
        list += Feat318UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat318UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat318UiModel {
    val summaries = (0 until count).map {
        Feat318UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat318UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat318UiModel> {
    val models = java.util.ArrayList<Feat318UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat318AnalyticsEvent1(val name: String, val value: String)
data class Feat318AnalyticsEvent2(val name: String, val value: String)
data class Feat318AnalyticsEvent3(val name: String, val value: String)
data class Feat318AnalyticsEvent4(val name: String, val value: String)
data class Feat318AnalyticsEvent5(val name: String, val value: String)
data class Feat318AnalyticsEvent6(val name: String, val value: String)
data class Feat318AnalyticsEvent7(val name: String, val value: String)
data class Feat318AnalyticsEvent8(val name: String, val value: String)
data class Feat318AnalyticsEvent9(val name: String, val value: String)
data class Feat318AnalyticsEvent10(val name: String, val value: String)

fun logFeat318Event1(event: Feat318AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat318Event2(event: Feat318AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat318Event3(event: Feat318AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat318Event4(event: Feat318AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat318Event5(event: Feat318AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat318Event6(event: Feat318AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat318Event7(event: Feat318AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat318Event8(event: Feat318AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat318Event9(event: Feat318AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat318Event10(event: Feat318AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat318Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat318Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat318Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat318Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat318Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat318Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat318Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat318Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat318Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat318Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat318(u: CoreUser): Feat318Projection1 =
    Feat318Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat318Projection1> {
    val list = java.util.ArrayList<Feat318Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat318(u)
    }
    return list
}
