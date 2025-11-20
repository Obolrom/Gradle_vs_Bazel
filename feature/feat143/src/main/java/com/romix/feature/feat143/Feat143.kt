package com.romix.feature.feat143

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat143Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat143UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat143FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat143UserSummary
)

data class Feat143UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat143NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat143Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat143Config = Feat143Config()
) {

    fun loadSnapshot(userId: Long): Feat143NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat143NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat143UserSummary {
        return Feat143UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat143FeedItem> {
        val result = java.util.ArrayList<Feat143FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat143FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat143UiMapper {

    fun mapToUi(model: List<Feat143FeedItem>): Feat143UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat143UiModel(
            header = UiText("Feat143 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat143UiModel =
        Feat143UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat143UiModel =
        Feat143UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat143UiModel =
        Feat143UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat143Service(
    private val repository: Feat143Repository,
    private val uiMapper: Feat143UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat143UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat143UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat143UserItem1(val user: CoreUser, val label: String)
data class Feat143UserItem2(val user: CoreUser, val label: String)
data class Feat143UserItem3(val user: CoreUser, val label: String)
data class Feat143UserItem4(val user: CoreUser, val label: String)
data class Feat143UserItem5(val user: CoreUser, val label: String)
data class Feat143UserItem6(val user: CoreUser, val label: String)
data class Feat143UserItem7(val user: CoreUser, val label: String)
data class Feat143UserItem8(val user: CoreUser, val label: String)
data class Feat143UserItem9(val user: CoreUser, val label: String)
data class Feat143UserItem10(val user: CoreUser, val label: String)

data class Feat143StateBlock1(val state: Feat143UiModel, val checksum: Int)
data class Feat143StateBlock2(val state: Feat143UiModel, val checksum: Int)
data class Feat143StateBlock3(val state: Feat143UiModel, val checksum: Int)
data class Feat143StateBlock4(val state: Feat143UiModel, val checksum: Int)
data class Feat143StateBlock5(val state: Feat143UiModel, val checksum: Int)
data class Feat143StateBlock6(val state: Feat143UiModel, val checksum: Int)
data class Feat143StateBlock7(val state: Feat143UiModel, val checksum: Int)
data class Feat143StateBlock8(val state: Feat143UiModel, val checksum: Int)
data class Feat143StateBlock9(val state: Feat143UiModel, val checksum: Int)
data class Feat143StateBlock10(val state: Feat143UiModel, val checksum: Int)

fun buildFeat143UserItem(user: CoreUser, index: Int): Feat143UserItem1 {
    return Feat143UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat143StateBlock(model: Feat143UiModel): Feat143StateBlock1 {
    return Feat143StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat143UserSummary> {
    val list = java.util.ArrayList<Feat143UserSummary>(users.size)
    for (user in users) {
        list += Feat143UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat143UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat143UiModel {
    val summaries = (0 until count).map {
        Feat143UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat143UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat143UiModel> {
    val models = java.util.ArrayList<Feat143UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat143AnalyticsEvent1(val name: String, val value: String)
data class Feat143AnalyticsEvent2(val name: String, val value: String)
data class Feat143AnalyticsEvent3(val name: String, val value: String)
data class Feat143AnalyticsEvent4(val name: String, val value: String)
data class Feat143AnalyticsEvent5(val name: String, val value: String)
data class Feat143AnalyticsEvent6(val name: String, val value: String)
data class Feat143AnalyticsEvent7(val name: String, val value: String)
data class Feat143AnalyticsEvent8(val name: String, val value: String)
data class Feat143AnalyticsEvent9(val name: String, val value: String)
data class Feat143AnalyticsEvent10(val name: String, val value: String)

fun logFeat143Event1(event: Feat143AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat143Event2(event: Feat143AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat143Event3(event: Feat143AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat143Event4(event: Feat143AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat143Event5(event: Feat143AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat143Event6(event: Feat143AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat143Event7(event: Feat143AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat143Event8(event: Feat143AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat143Event9(event: Feat143AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat143Event10(event: Feat143AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat143Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat143Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat143Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat143Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat143Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat143Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat143Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat143Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat143Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat143Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat143(u: CoreUser): Feat143Projection1 =
    Feat143Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat143Projection1> {
    val list = java.util.ArrayList<Feat143Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat143(u)
    }
    return list
}
