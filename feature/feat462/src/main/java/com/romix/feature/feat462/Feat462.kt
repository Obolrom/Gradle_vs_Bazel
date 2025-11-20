package com.romix.feature.feat462

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat462Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat462UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat462FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat462UserSummary
)

data class Feat462UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat462NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat462Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat462Config = Feat462Config()
) {

    fun loadSnapshot(userId: Long): Feat462NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat462NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat462UserSummary {
        return Feat462UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat462FeedItem> {
        val result = java.util.ArrayList<Feat462FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat462FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat462UiMapper {

    fun mapToUi(model: List<Feat462FeedItem>): Feat462UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat462UiModel(
            header = UiText("Feat462 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat462UiModel =
        Feat462UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat462UiModel =
        Feat462UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat462UiModel =
        Feat462UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat462Service(
    private val repository: Feat462Repository,
    private val uiMapper: Feat462UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat462UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat462UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat462UserItem1(val user: CoreUser, val label: String)
data class Feat462UserItem2(val user: CoreUser, val label: String)
data class Feat462UserItem3(val user: CoreUser, val label: String)
data class Feat462UserItem4(val user: CoreUser, val label: String)
data class Feat462UserItem5(val user: CoreUser, val label: String)
data class Feat462UserItem6(val user: CoreUser, val label: String)
data class Feat462UserItem7(val user: CoreUser, val label: String)
data class Feat462UserItem8(val user: CoreUser, val label: String)
data class Feat462UserItem9(val user: CoreUser, val label: String)
data class Feat462UserItem10(val user: CoreUser, val label: String)

data class Feat462StateBlock1(val state: Feat462UiModel, val checksum: Int)
data class Feat462StateBlock2(val state: Feat462UiModel, val checksum: Int)
data class Feat462StateBlock3(val state: Feat462UiModel, val checksum: Int)
data class Feat462StateBlock4(val state: Feat462UiModel, val checksum: Int)
data class Feat462StateBlock5(val state: Feat462UiModel, val checksum: Int)
data class Feat462StateBlock6(val state: Feat462UiModel, val checksum: Int)
data class Feat462StateBlock7(val state: Feat462UiModel, val checksum: Int)
data class Feat462StateBlock8(val state: Feat462UiModel, val checksum: Int)
data class Feat462StateBlock9(val state: Feat462UiModel, val checksum: Int)
data class Feat462StateBlock10(val state: Feat462UiModel, val checksum: Int)

fun buildFeat462UserItem(user: CoreUser, index: Int): Feat462UserItem1 {
    return Feat462UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat462StateBlock(model: Feat462UiModel): Feat462StateBlock1 {
    return Feat462StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat462UserSummary> {
    val list = java.util.ArrayList<Feat462UserSummary>(users.size)
    for (user in users) {
        list += Feat462UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat462UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat462UiModel {
    val summaries = (0 until count).map {
        Feat462UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat462UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat462UiModel> {
    val models = java.util.ArrayList<Feat462UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat462AnalyticsEvent1(val name: String, val value: String)
data class Feat462AnalyticsEvent2(val name: String, val value: String)
data class Feat462AnalyticsEvent3(val name: String, val value: String)
data class Feat462AnalyticsEvent4(val name: String, val value: String)
data class Feat462AnalyticsEvent5(val name: String, val value: String)
data class Feat462AnalyticsEvent6(val name: String, val value: String)
data class Feat462AnalyticsEvent7(val name: String, val value: String)
data class Feat462AnalyticsEvent8(val name: String, val value: String)
data class Feat462AnalyticsEvent9(val name: String, val value: String)
data class Feat462AnalyticsEvent10(val name: String, val value: String)

fun logFeat462Event1(event: Feat462AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat462Event2(event: Feat462AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat462Event3(event: Feat462AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat462Event4(event: Feat462AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat462Event5(event: Feat462AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat462Event6(event: Feat462AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat462Event7(event: Feat462AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat462Event8(event: Feat462AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat462Event9(event: Feat462AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat462Event10(event: Feat462AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat462Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat462Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat462Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat462Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat462Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat462Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat462Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat462Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat462Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat462Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat462(u: CoreUser): Feat462Projection1 =
    Feat462Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat462Projection1> {
    val list = java.util.ArrayList<Feat462Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat462(u)
    }
    return list
}
