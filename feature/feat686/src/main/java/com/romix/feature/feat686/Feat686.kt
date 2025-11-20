package com.romix.feature.feat686

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat686Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat686UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat686FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat686UserSummary
)

data class Feat686UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat686NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat686Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat686Config = Feat686Config()
) {

    fun loadSnapshot(userId: Long): Feat686NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat686NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat686UserSummary {
        return Feat686UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat686FeedItem> {
        val result = java.util.ArrayList<Feat686FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat686FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat686UiMapper {

    fun mapToUi(model: List<Feat686FeedItem>): Feat686UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat686UiModel(
            header = UiText("Feat686 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat686UiModel =
        Feat686UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat686UiModel =
        Feat686UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat686UiModel =
        Feat686UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat686Service(
    private val repository: Feat686Repository,
    private val uiMapper: Feat686UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat686UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat686UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat686UserItem1(val user: CoreUser, val label: String)
data class Feat686UserItem2(val user: CoreUser, val label: String)
data class Feat686UserItem3(val user: CoreUser, val label: String)
data class Feat686UserItem4(val user: CoreUser, val label: String)
data class Feat686UserItem5(val user: CoreUser, val label: String)
data class Feat686UserItem6(val user: CoreUser, val label: String)
data class Feat686UserItem7(val user: CoreUser, val label: String)
data class Feat686UserItem8(val user: CoreUser, val label: String)
data class Feat686UserItem9(val user: CoreUser, val label: String)
data class Feat686UserItem10(val user: CoreUser, val label: String)

data class Feat686StateBlock1(val state: Feat686UiModel, val checksum: Int)
data class Feat686StateBlock2(val state: Feat686UiModel, val checksum: Int)
data class Feat686StateBlock3(val state: Feat686UiModel, val checksum: Int)
data class Feat686StateBlock4(val state: Feat686UiModel, val checksum: Int)
data class Feat686StateBlock5(val state: Feat686UiModel, val checksum: Int)
data class Feat686StateBlock6(val state: Feat686UiModel, val checksum: Int)
data class Feat686StateBlock7(val state: Feat686UiModel, val checksum: Int)
data class Feat686StateBlock8(val state: Feat686UiModel, val checksum: Int)
data class Feat686StateBlock9(val state: Feat686UiModel, val checksum: Int)
data class Feat686StateBlock10(val state: Feat686UiModel, val checksum: Int)

fun buildFeat686UserItem(user: CoreUser, index: Int): Feat686UserItem1 {
    return Feat686UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat686StateBlock(model: Feat686UiModel): Feat686StateBlock1 {
    return Feat686StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat686UserSummary> {
    val list = java.util.ArrayList<Feat686UserSummary>(users.size)
    for (user in users) {
        list += Feat686UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat686UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat686UiModel {
    val summaries = (0 until count).map {
        Feat686UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat686UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat686UiModel> {
    val models = java.util.ArrayList<Feat686UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat686AnalyticsEvent1(val name: String, val value: String)
data class Feat686AnalyticsEvent2(val name: String, val value: String)
data class Feat686AnalyticsEvent3(val name: String, val value: String)
data class Feat686AnalyticsEvent4(val name: String, val value: String)
data class Feat686AnalyticsEvent5(val name: String, val value: String)
data class Feat686AnalyticsEvent6(val name: String, val value: String)
data class Feat686AnalyticsEvent7(val name: String, val value: String)
data class Feat686AnalyticsEvent8(val name: String, val value: String)
data class Feat686AnalyticsEvent9(val name: String, val value: String)
data class Feat686AnalyticsEvent10(val name: String, val value: String)

fun logFeat686Event1(event: Feat686AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat686Event2(event: Feat686AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat686Event3(event: Feat686AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat686Event4(event: Feat686AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat686Event5(event: Feat686AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat686Event6(event: Feat686AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat686Event7(event: Feat686AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat686Event8(event: Feat686AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat686Event9(event: Feat686AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat686Event10(event: Feat686AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat686Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat686Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat686Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat686Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat686Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat686Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat686Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat686Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat686Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat686Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat686(u: CoreUser): Feat686Projection1 =
    Feat686Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat686Projection1> {
    val list = java.util.ArrayList<Feat686Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat686(u)
    }
    return list
}
