package com.romix.feature.feat449

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat449Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat449UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat449FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat449UserSummary
)

data class Feat449UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat449NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat449Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat449Config = Feat449Config()
) {

    fun loadSnapshot(userId: Long): Feat449NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat449NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat449UserSummary {
        return Feat449UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat449FeedItem> {
        val result = java.util.ArrayList<Feat449FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat449FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat449UiMapper {

    fun mapToUi(model: List<Feat449FeedItem>): Feat449UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat449UiModel(
            header = UiText("Feat449 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat449UiModel =
        Feat449UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat449UiModel =
        Feat449UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat449UiModel =
        Feat449UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat449Service(
    private val repository: Feat449Repository,
    private val uiMapper: Feat449UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat449UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat449UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat449UserItem1(val user: CoreUser, val label: String)
data class Feat449UserItem2(val user: CoreUser, val label: String)
data class Feat449UserItem3(val user: CoreUser, val label: String)
data class Feat449UserItem4(val user: CoreUser, val label: String)
data class Feat449UserItem5(val user: CoreUser, val label: String)
data class Feat449UserItem6(val user: CoreUser, val label: String)
data class Feat449UserItem7(val user: CoreUser, val label: String)
data class Feat449UserItem8(val user: CoreUser, val label: String)
data class Feat449UserItem9(val user: CoreUser, val label: String)
data class Feat449UserItem10(val user: CoreUser, val label: String)

data class Feat449StateBlock1(val state: Feat449UiModel, val checksum: Int)
data class Feat449StateBlock2(val state: Feat449UiModel, val checksum: Int)
data class Feat449StateBlock3(val state: Feat449UiModel, val checksum: Int)
data class Feat449StateBlock4(val state: Feat449UiModel, val checksum: Int)
data class Feat449StateBlock5(val state: Feat449UiModel, val checksum: Int)
data class Feat449StateBlock6(val state: Feat449UiModel, val checksum: Int)
data class Feat449StateBlock7(val state: Feat449UiModel, val checksum: Int)
data class Feat449StateBlock8(val state: Feat449UiModel, val checksum: Int)
data class Feat449StateBlock9(val state: Feat449UiModel, val checksum: Int)
data class Feat449StateBlock10(val state: Feat449UiModel, val checksum: Int)

fun buildFeat449UserItem(user: CoreUser, index: Int): Feat449UserItem1 {
    return Feat449UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat449StateBlock(model: Feat449UiModel): Feat449StateBlock1 {
    return Feat449StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat449UserSummary> {
    val list = java.util.ArrayList<Feat449UserSummary>(users.size)
    for (user in users) {
        list += Feat449UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat449UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat449UiModel {
    val summaries = (0 until count).map {
        Feat449UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat449UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat449UiModel> {
    val models = java.util.ArrayList<Feat449UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat449AnalyticsEvent1(val name: String, val value: String)
data class Feat449AnalyticsEvent2(val name: String, val value: String)
data class Feat449AnalyticsEvent3(val name: String, val value: String)
data class Feat449AnalyticsEvent4(val name: String, val value: String)
data class Feat449AnalyticsEvent5(val name: String, val value: String)
data class Feat449AnalyticsEvent6(val name: String, val value: String)
data class Feat449AnalyticsEvent7(val name: String, val value: String)
data class Feat449AnalyticsEvent8(val name: String, val value: String)
data class Feat449AnalyticsEvent9(val name: String, val value: String)
data class Feat449AnalyticsEvent10(val name: String, val value: String)

fun logFeat449Event1(event: Feat449AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat449Event2(event: Feat449AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat449Event3(event: Feat449AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat449Event4(event: Feat449AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat449Event5(event: Feat449AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat449Event6(event: Feat449AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat449Event7(event: Feat449AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat449Event8(event: Feat449AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat449Event9(event: Feat449AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat449Event10(event: Feat449AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat449Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat449Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat449Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat449Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat449Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat449Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat449Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat449Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat449Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat449Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat449(u: CoreUser): Feat449Projection1 =
    Feat449Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat449Projection1> {
    val list = java.util.ArrayList<Feat449Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat449(u)
    }
    return list
}
