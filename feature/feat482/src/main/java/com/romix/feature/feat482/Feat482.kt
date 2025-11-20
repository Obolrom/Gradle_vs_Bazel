package com.romix.feature.feat482

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat482Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat482UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat482FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat482UserSummary
)

data class Feat482UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat482NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat482Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat482Config = Feat482Config()
) {

    fun loadSnapshot(userId: Long): Feat482NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat482NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat482UserSummary {
        return Feat482UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat482FeedItem> {
        val result = java.util.ArrayList<Feat482FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat482FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat482UiMapper {

    fun mapToUi(model: List<Feat482FeedItem>): Feat482UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat482UiModel(
            header = UiText("Feat482 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat482UiModel =
        Feat482UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat482UiModel =
        Feat482UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat482UiModel =
        Feat482UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat482Service(
    private val repository: Feat482Repository,
    private val uiMapper: Feat482UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat482UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat482UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat482UserItem1(val user: CoreUser, val label: String)
data class Feat482UserItem2(val user: CoreUser, val label: String)
data class Feat482UserItem3(val user: CoreUser, val label: String)
data class Feat482UserItem4(val user: CoreUser, val label: String)
data class Feat482UserItem5(val user: CoreUser, val label: String)
data class Feat482UserItem6(val user: CoreUser, val label: String)
data class Feat482UserItem7(val user: CoreUser, val label: String)
data class Feat482UserItem8(val user: CoreUser, val label: String)
data class Feat482UserItem9(val user: CoreUser, val label: String)
data class Feat482UserItem10(val user: CoreUser, val label: String)

data class Feat482StateBlock1(val state: Feat482UiModel, val checksum: Int)
data class Feat482StateBlock2(val state: Feat482UiModel, val checksum: Int)
data class Feat482StateBlock3(val state: Feat482UiModel, val checksum: Int)
data class Feat482StateBlock4(val state: Feat482UiModel, val checksum: Int)
data class Feat482StateBlock5(val state: Feat482UiModel, val checksum: Int)
data class Feat482StateBlock6(val state: Feat482UiModel, val checksum: Int)
data class Feat482StateBlock7(val state: Feat482UiModel, val checksum: Int)
data class Feat482StateBlock8(val state: Feat482UiModel, val checksum: Int)
data class Feat482StateBlock9(val state: Feat482UiModel, val checksum: Int)
data class Feat482StateBlock10(val state: Feat482UiModel, val checksum: Int)

fun buildFeat482UserItem(user: CoreUser, index: Int): Feat482UserItem1 {
    return Feat482UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat482StateBlock(model: Feat482UiModel): Feat482StateBlock1 {
    return Feat482StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat482UserSummary> {
    val list = java.util.ArrayList<Feat482UserSummary>(users.size)
    for (user in users) {
        list += Feat482UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat482UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat482UiModel {
    val summaries = (0 until count).map {
        Feat482UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat482UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat482UiModel> {
    val models = java.util.ArrayList<Feat482UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat482AnalyticsEvent1(val name: String, val value: String)
data class Feat482AnalyticsEvent2(val name: String, val value: String)
data class Feat482AnalyticsEvent3(val name: String, val value: String)
data class Feat482AnalyticsEvent4(val name: String, val value: String)
data class Feat482AnalyticsEvent5(val name: String, val value: String)
data class Feat482AnalyticsEvent6(val name: String, val value: String)
data class Feat482AnalyticsEvent7(val name: String, val value: String)
data class Feat482AnalyticsEvent8(val name: String, val value: String)
data class Feat482AnalyticsEvent9(val name: String, val value: String)
data class Feat482AnalyticsEvent10(val name: String, val value: String)

fun logFeat482Event1(event: Feat482AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat482Event2(event: Feat482AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat482Event3(event: Feat482AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat482Event4(event: Feat482AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat482Event5(event: Feat482AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat482Event6(event: Feat482AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat482Event7(event: Feat482AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat482Event8(event: Feat482AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat482Event9(event: Feat482AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat482Event10(event: Feat482AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat482Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat482Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat482Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat482Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat482Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat482Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat482Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat482Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat482Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat482Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat482(u: CoreUser): Feat482Projection1 =
    Feat482Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat482Projection1> {
    val list = java.util.ArrayList<Feat482Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat482(u)
    }
    return list
}
