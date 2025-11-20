package com.romix.feature.feat588

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat588Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat588UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat588FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat588UserSummary
)

data class Feat588UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat588NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat588Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat588Config = Feat588Config()
) {

    fun loadSnapshot(userId: Long): Feat588NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat588NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat588UserSummary {
        return Feat588UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat588FeedItem> {
        val result = java.util.ArrayList<Feat588FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat588FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat588UiMapper {

    fun mapToUi(model: List<Feat588FeedItem>): Feat588UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat588UiModel(
            header = UiText("Feat588 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat588UiModel =
        Feat588UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat588UiModel =
        Feat588UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat588UiModel =
        Feat588UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat588Service(
    private val repository: Feat588Repository,
    private val uiMapper: Feat588UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat588UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat588UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat588UserItem1(val user: CoreUser, val label: String)
data class Feat588UserItem2(val user: CoreUser, val label: String)
data class Feat588UserItem3(val user: CoreUser, val label: String)
data class Feat588UserItem4(val user: CoreUser, val label: String)
data class Feat588UserItem5(val user: CoreUser, val label: String)
data class Feat588UserItem6(val user: CoreUser, val label: String)
data class Feat588UserItem7(val user: CoreUser, val label: String)
data class Feat588UserItem8(val user: CoreUser, val label: String)
data class Feat588UserItem9(val user: CoreUser, val label: String)
data class Feat588UserItem10(val user: CoreUser, val label: String)

data class Feat588StateBlock1(val state: Feat588UiModel, val checksum: Int)
data class Feat588StateBlock2(val state: Feat588UiModel, val checksum: Int)
data class Feat588StateBlock3(val state: Feat588UiModel, val checksum: Int)
data class Feat588StateBlock4(val state: Feat588UiModel, val checksum: Int)
data class Feat588StateBlock5(val state: Feat588UiModel, val checksum: Int)
data class Feat588StateBlock6(val state: Feat588UiModel, val checksum: Int)
data class Feat588StateBlock7(val state: Feat588UiModel, val checksum: Int)
data class Feat588StateBlock8(val state: Feat588UiModel, val checksum: Int)
data class Feat588StateBlock9(val state: Feat588UiModel, val checksum: Int)
data class Feat588StateBlock10(val state: Feat588UiModel, val checksum: Int)

fun buildFeat588UserItem(user: CoreUser, index: Int): Feat588UserItem1 {
    return Feat588UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat588StateBlock(model: Feat588UiModel): Feat588StateBlock1 {
    return Feat588StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat588UserSummary> {
    val list = java.util.ArrayList<Feat588UserSummary>(users.size)
    for (user in users) {
        list += Feat588UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat588UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat588UiModel {
    val summaries = (0 until count).map {
        Feat588UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat588UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat588UiModel> {
    val models = java.util.ArrayList<Feat588UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat588AnalyticsEvent1(val name: String, val value: String)
data class Feat588AnalyticsEvent2(val name: String, val value: String)
data class Feat588AnalyticsEvent3(val name: String, val value: String)
data class Feat588AnalyticsEvent4(val name: String, val value: String)
data class Feat588AnalyticsEvent5(val name: String, val value: String)
data class Feat588AnalyticsEvent6(val name: String, val value: String)
data class Feat588AnalyticsEvent7(val name: String, val value: String)
data class Feat588AnalyticsEvent8(val name: String, val value: String)
data class Feat588AnalyticsEvent9(val name: String, val value: String)
data class Feat588AnalyticsEvent10(val name: String, val value: String)

fun logFeat588Event1(event: Feat588AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat588Event2(event: Feat588AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat588Event3(event: Feat588AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat588Event4(event: Feat588AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat588Event5(event: Feat588AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat588Event6(event: Feat588AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat588Event7(event: Feat588AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat588Event8(event: Feat588AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat588Event9(event: Feat588AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat588Event10(event: Feat588AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat588Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat588Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat588Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat588Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat588Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat588Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat588Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat588Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat588Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat588Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat588(u: CoreUser): Feat588Projection1 =
    Feat588Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat588Projection1> {
    val list = java.util.ArrayList<Feat588Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat588(u)
    }
    return list
}
