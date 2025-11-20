package com.romix.feature.feat520

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat520Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat520UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat520FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat520UserSummary
)

data class Feat520UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat520NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat520Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat520Config = Feat520Config()
) {

    fun loadSnapshot(userId: Long): Feat520NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat520NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat520UserSummary {
        return Feat520UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat520FeedItem> {
        val result = java.util.ArrayList<Feat520FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat520FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat520UiMapper {

    fun mapToUi(model: List<Feat520FeedItem>): Feat520UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat520UiModel(
            header = UiText("Feat520 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat520UiModel =
        Feat520UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat520UiModel =
        Feat520UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat520UiModel =
        Feat520UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat520Service(
    private val repository: Feat520Repository,
    private val uiMapper: Feat520UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat520UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat520UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat520UserItem1(val user: CoreUser, val label: String)
data class Feat520UserItem2(val user: CoreUser, val label: String)
data class Feat520UserItem3(val user: CoreUser, val label: String)
data class Feat520UserItem4(val user: CoreUser, val label: String)
data class Feat520UserItem5(val user: CoreUser, val label: String)
data class Feat520UserItem6(val user: CoreUser, val label: String)
data class Feat520UserItem7(val user: CoreUser, val label: String)
data class Feat520UserItem8(val user: CoreUser, val label: String)
data class Feat520UserItem9(val user: CoreUser, val label: String)
data class Feat520UserItem10(val user: CoreUser, val label: String)

data class Feat520StateBlock1(val state: Feat520UiModel, val checksum: Int)
data class Feat520StateBlock2(val state: Feat520UiModel, val checksum: Int)
data class Feat520StateBlock3(val state: Feat520UiModel, val checksum: Int)
data class Feat520StateBlock4(val state: Feat520UiModel, val checksum: Int)
data class Feat520StateBlock5(val state: Feat520UiModel, val checksum: Int)
data class Feat520StateBlock6(val state: Feat520UiModel, val checksum: Int)
data class Feat520StateBlock7(val state: Feat520UiModel, val checksum: Int)
data class Feat520StateBlock8(val state: Feat520UiModel, val checksum: Int)
data class Feat520StateBlock9(val state: Feat520UiModel, val checksum: Int)
data class Feat520StateBlock10(val state: Feat520UiModel, val checksum: Int)

fun buildFeat520UserItem(user: CoreUser, index: Int): Feat520UserItem1 {
    return Feat520UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat520StateBlock(model: Feat520UiModel): Feat520StateBlock1 {
    return Feat520StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat520UserSummary> {
    val list = java.util.ArrayList<Feat520UserSummary>(users.size)
    for (user in users) {
        list += Feat520UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat520UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat520UiModel {
    val summaries = (0 until count).map {
        Feat520UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat520UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat520UiModel> {
    val models = java.util.ArrayList<Feat520UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat520AnalyticsEvent1(val name: String, val value: String)
data class Feat520AnalyticsEvent2(val name: String, val value: String)
data class Feat520AnalyticsEvent3(val name: String, val value: String)
data class Feat520AnalyticsEvent4(val name: String, val value: String)
data class Feat520AnalyticsEvent5(val name: String, val value: String)
data class Feat520AnalyticsEvent6(val name: String, val value: String)
data class Feat520AnalyticsEvent7(val name: String, val value: String)
data class Feat520AnalyticsEvent8(val name: String, val value: String)
data class Feat520AnalyticsEvent9(val name: String, val value: String)
data class Feat520AnalyticsEvent10(val name: String, val value: String)

fun logFeat520Event1(event: Feat520AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat520Event2(event: Feat520AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat520Event3(event: Feat520AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat520Event4(event: Feat520AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat520Event5(event: Feat520AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat520Event6(event: Feat520AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat520Event7(event: Feat520AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat520Event8(event: Feat520AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat520Event9(event: Feat520AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat520Event10(event: Feat520AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat520Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat520Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat520Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat520Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat520Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat520Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat520Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat520Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat520Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat520Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat520(u: CoreUser): Feat520Projection1 =
    Feat520Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat520Projection1> {
    val list = java.util.ArrayList<Feat520Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat520(u)
    }
    return list
}
