package com.romix.feature.feat393

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat393Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat393UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat393FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat393UserSummary
)

data class Feat393UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat393NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat393Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat393Config = Feat393Config()
) {

    fun loadSnapshot(userId: Long): Feat393NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat393NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat393UserSummary {
        return Feat393UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat393FeedItem> {
        val result = java.util.ArrayList<Feat393FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat393FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat393UiMapper {

    fun mapToUi(model: List<Feat393FeedItem>): Feat393UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat393UiModel(
            header = UiText("Feat393 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat393UiModel =
        Feat393UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat393UiModel =
        Feat393UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat393UiModel =
        Feat393UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat393Service(
    private val repository: Feat393Repository,
    private val uiMapper: Feat393UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat393UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat393UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat393UserItem1(val user: CoreUser, val label: String)
data class Feat393UserItem2(val user: CoreUser, val label: String)
data class Feat393UserItem3(val user: CoreUser, val label: String)
data class Feat393UserItem4(val user: CoreUser, val label: String)
data class Feat393UserItem5(val user: CoreUser, val label: String)
data class Feat393UserItem6(val user: CoreUser, val label: String)
data class Feat393UserItem7(val user: CoreUser, val label: String)
data class Feat393UserItem8(val user: CoreUser, val label: String)
data class Feat393UserItem9(val user: CoreUser, val label: String)
data class Feat393UserItem10(val user: CoreUser, val label: String)

data class Feat393StateBlock1(val state: Feat393UiModel, val checksum: Int)
data class Feat393StateBlock2(val state: Feat393UiModel, val checksum: Int)
data class Feat393StateBlock3(val state: Feat393UiModel, val checksum: Int)
data class Feat393StateBlock4(val state: Feat393UiModel, val checksum: Int)
data class Feat393StateBlock5(val state: Feat393UiModel, val checksum: Int)
data class Feat393StateBlock6(val state: Feat393UiModel, val checksum: Int)
data class Feat393StateBlock7(val state: Feat393UiModel, val checksum: Int)
data class Feat393StateBlock8(val state: Feat393UiModel, val checksum: Int)
data class Feat393StateBlock9(val state: Feat393UiModel, val checksum: Int)
data class Feat393StateBlock10(val state: Feat393UiModel, val checksum: Int)

fun buildFeat393UserItem(user: CoreUser, index: Int): Feat393UserItem1 {
    return Feat393UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat393StateBlock(model: Feat393UiModel): Feat393StateBlock1 {
    return Feat393StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat393UserSummary> {
    val list = java.util.ArrayList<Feat393UserSummary>(users.size)
    for (user in users) {
        list += Feat393UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat393UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat393UiModel {
    val summaries = (0 until count).map {
        Feat393UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat393UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat393UiModel> {
    val models = java.util.ArrayList<Feat393UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat393AnalyticsEvent1(val name: String, val value: String)
data class Feat393AnalyticsEvent2(val name: String, val value: String)
data class Feat393AnalyticsEvent3(val name: String, val value: String)
data class Feat393AnalyticsEvent4(val name: String, val value: String)
data class Feat393AnalyticsEvent5(val name: String, val value: String)
data class Feat393AnalyticsEvent6(val name: String, val value: String)
data class Feat393AnalyticsEvent7(val name: String, val value: String)
data class Feat393AnalyticsEvent8(val name: String, val value: String)
data class Feat393AnalyticsEvent9(val name: String, val value: String)
data class Feat393AnalyticsEvent10(val name: String, val value: String)

fun logFeat393Event1(event: Feat393AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat393Event2(event: Feat393AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat393Event3(event: Feat393AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat393Event4(event: Feat393AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat393Event5(event: Feat393AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat393Event6(event: Feat393AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat393Event7(event: Feat393AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat393Event8(event: Feat393AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat393Event9(event: Feat393AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat393Event10(event: Feat393AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat393Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat393Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat393Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat393Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat393Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat393Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat393Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat393Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat393Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat393Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat393(u: CoreUser): Feat393Projection1 =
    Feat393Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat393Projection1> {
    val list = java.util.ArrayList<Feat393Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat393(u)
    }
    return list
}
