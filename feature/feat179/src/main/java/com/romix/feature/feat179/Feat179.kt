package com.romix.feature.feat179

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat179Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat179UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat179FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat179UserSummary
)

data class Feat179UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat179NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat179Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat179Config = Feat179Config()
) {

    fun loadSnapshot(userId: Long): Feat179NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat179NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat179UserSummary {
        return Feat179UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat179FeedItem> {
        val result = java.util.ArrayList<Feat179FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat179FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat179UiMapper {

    fun mapToUi(model: List<Feat179FeedItem>): Feat179UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat179UiModel(
            header = UiText("Feat179 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat179UiModel =
        Feat179UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat179UiModel =
        Feat179UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat179UiModel =
        Feat179UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat179Service(
    private val repository: Feat179Repository,
    private val uiMapper: Feat179UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat179UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat179UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat179UserItem1(val user: CoreUser, val label: String)
data class Feat179UserItem2(val user: CoreUser, val label: String)
data class Feat179UserItem3(val user: CoreUser, val label: String)
data class Feat179UserItem4(val user: CoreUser, val label: String)
data class Feat179UserItem5(val user: CoreUser, val label: String)
data class Feat179UserItem6(val user: CoreUser, val label: String)
data class Feat179UserItem7(val user: CoreUser, val label: String)
data class Feat179UserItem8(val user: CoreUser, val label: String)
data class Feat179UserItem9(val user: CoreUser, val label: String)
data class Feat179UserItem10(val user: CoreUser, val label: String)

data class Feat179StateBlock1(val state: Feat179UiModel, val checksum: Int)
data class Feat179StateBlock2(val state: Feat179UiModel, val checksum: Int)
data class Feat179StateBlock3(val state: Feat179UiModel, val checksum: Int)
data class Feat179StateBlock4(val state: Feat179UiModel, val checksum: Int)
data class Feat179StateBlock5(val state: Feat179UiModel, val checksum: Int)
data class Feat179StateBlock6(val state: Feat179UiModel, val checksum: Int)
data class Feat179StateBlock7(val state: Feat179UiModel, val checksum: Int)
data class Feat179StateBlock8(val state: Feat179UiModel, val checksum: Int)
data class Feat179StateBlock9(val state: Feat179UiModel, val checksum: Int)
data class Feat179StateBlock10(val state: Feat179UiModel, val checksum: Int)

fun buildFeat179UserItem(user: CoreUser, index: Int): Feat179UserItem1 {
    return Feat179UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat179StateBlock(model: Feat179UiModel): Feat179StateBlock1 {
    return Feat179StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat179UserSummary> {
    val list = java.util.ArrayList<Feat179UserSummary>(users.size)
    for (user in users) {
        list += Feat179UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat179UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat179UiModel {
    val summaries = (0 until count).map {
        Feat179UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat179UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat179UiModel> {
    val models = java.util.ArrayList<Feat179UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat179AnalyticsEvent1(val name: String, val value: String)
data class Feat179AnalyticsEvent2(val name: String, val value: String)
data class Feat179AnalyticsEvent3(val name: String, val value: String)
data class Feat179AnalyticsEvent4(val name: String, val value: String)
data class Feat179AnalyticsEvent5(val name: String, val value: String)
data class Feat179AnalyticsEvent6(val name: String, val value: String)
data class Feat179AnalyticsEvent7(val name: String, val value: String)
data class Feat179AnalyticsEvent8(val name: String, val value: String)
data class Feat179AnalyticsEvent9(val name: String, val value: String)
data class Feat179AnalyticsEvent10(val name: String, val value: String)

fun logFeat179Event1(event: Feat179AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat179Event2(event: Feat179AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat179Event3(event: Feat179AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat179Event4(event: Feat179AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat179Event5(event: Feat179AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat179Event6(event: Feat179AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat179Event7(event: Feat179AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat179Event8(event: Feat179AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat179Event9(event: Feat179AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat179Event10(event: Feat179AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat179Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat179Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat179Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat179Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat179Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat179Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat179Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat179Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat179Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat179Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat179(u: CoreUser): Feat179Projection1 =
    Feat179Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat179Projection1> {
    val list = java.util.ArrayList<Feat179Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat179(u)
    }
    return list
}
