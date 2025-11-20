package com.romix.feature.feat542

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat542Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat542UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat542FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat542UserSummary
)

data class Feat542UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat542NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat542Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat542Config = Feat542Config()
) {

    fun loadSnapshot(userId: Long): Feat542NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat542NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat542UserSummary {
        return Feat542UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat542FeedItem> {
        val result = java.util.ArrayList<Feat542FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat542FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat542UiMapper {

    fun mapToUi(model: List<Feat542FeedItem>): Feat542UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat542UiModel(
            header = UiText("Feat542 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat542UiModel =
        Feat542UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat542UiModel =
        Feat542UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat542UiModel =
        Feat542UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat542Service(
    private val repository: Feat542Repository,
    private val uiMapper: Feat542UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat542UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat542UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat542UserItem1(val user: CoreUser, val label: String)
data class Feat542UserItem2(val user: CoreUser, val label: String)
data class Feat542UserItem3(val user: CoreUser, val label: String)
data class Feat542UserItem4(val user: CoreUser, val label: String)
data class Feat542UserItem5(val user: CoreUser, val label: String)
data class Feat542UserItem6(val user: CoreUser, val label: String)
data class Feat542UserItem7(val user: CoreUser, val label: String)
data class Feat542UserItem8(val user: CoreUser, val label: String)
data class Feat542UserItem9(val user: CoreUser, val label: String)
data class Feat542UserItem10(val user: CoreUser, val label: String)

data class Feat542StateBlock1(val state: Feat542UiModel, val checksum: Int)
data class Feat542StateBlock2(val state: Feat542UiModel, val checksum: Int)
data class Feat542StateBlock3(val state: Feat542UiModel, val checksum: Int)
data class Feat542StateBlock4(val state: Feat542UiModel, val checksum: Int)
data class Feat542StateBlock5(val state: Feat542UiModel, val checksum: Int)
data class Feat542StateBlock6(val state: Feat542UiModel, val checksum: Int)
data class Feat542StateBlock7(val state: Feat542UiModel, val checksum: Int)
data class Feat542StateBlock8(val state: Feat542UiModel, val checksum: Int)
data class Feat542StateBlock9(val state: Feat542UiModel, val checksum: Int)
data class Feat542StateBlock10(val state: Feat542UiModel, val checksum: Int)

fun buildFeat542UserItem(user: CoreUser, index: Int): Feat542UserItem1 {
    return Feat542UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat542StateBlock(model: Feat542UiModel): Feat542StateBlock1 {
    return Feat542StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat542UserSummary> {
    val list = java.util.ArrayList<Feat542UserSummary>(users.size)
    for (user in users) {
        list += Feat542UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat542UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat542UiModel {
    val summaries = (0 until count).map {
        Feat542UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat542UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat542UiModel> {
    val models = java.util.ArrayList<Feat542UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat542AnalyticsEvent1(val name: String, val value: String)
data class Feat542AnalyticsEvent2(val name: String, val value: String)
data class Feat542AnalyticsEvent3(val name: String, val value: String)
data class Feat542AnalyticsEvent4(val name: String, val value: String)
data class Feat542AnalyticsEvent5(val name: String, val value: String)
data class Feat542AnalyticsEvent6(val name: String, val value: String)
data class Feat542AnalyticsEvent7(val name: String, val value: String)
data class Feat542AnalyticsEvent8(val name: String, val value: String)
data class Feat542AnalyticsEvent9(val name: String, val value: String)
data class Feat542AnalyticsEvent10(val name: String, val value: String)

fun logFeat542Event1(event: Feat542AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat542Event2(event: Feat542AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat542Event3(event: Feat542AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat542Event4(event: Feat542AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat542Event5(event: Feat542AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat542Event6(event: Feat542AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat542Event7(event: Feat542AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat542Event8(event: Feat542AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat542Event9(event: Feat542AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat542Event10(event: Feat542AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat542Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat542Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat542Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat542Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat542Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat542Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat542Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat542Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat542Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat542Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat542(u: CoreUser): Feat542Projection1 =
    Feat542Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat542Projection1> {
    val list = java.util.ArrayList<Feat542Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat542(u)
    }
    return list
}
