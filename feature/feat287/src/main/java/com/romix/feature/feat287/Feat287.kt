package com.romix.feature.feat287

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat287Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat287UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat287FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat287UserSummary
)

data class Feat287UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat287NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat287Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat287Config = Feat287Config()
) {

    fun loadSnapshot(userId: Long): Feat287NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat287NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat287UserSummary {
        return Feat287UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat287FeedItem> {
        val result = java.util.ArrayList<Feat287FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat287FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat287UiMapper {

    fun mapToUi(model: List<Feat287FeedItem>): Feat287UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat287UiModel(
            header = UiText("Feat287 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat287UiModel =
        Feat287UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat287UiModel =
        Feat287UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat287UiModel =
        Feat287UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat287Service(
    private val repository: Feat287Repository,
    private val uiMapper: Feat287UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat287UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat287UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat287UserItem1(val user: CoreUser, val label: String)
data class Feat287UserItem2(val user: CoreUser, val label: String)
data class Feat287UserItem3(val user: CoreUser, val label: String)
data class Feat287UserItem4(val user: CoreUser, val label: String)
data class Feat287UserItem5(val user: CoreUser, val label: String)
data class Feat287UserItem6(val user: CoreUser, val label: String)
data class Feat287UserItem7(val user: CoreUser, val label: String)
data class Feat287UserItem8(val user: CoreUser, val label: String)
data class Feat287UserItem9(val user: CoreUser, val label: String)
data class Feat287UserItem10(val user: CoreUser, val label: String)

data class Feat287StateBlock1(val state: Feat287UiModel, val checksum: Int)
data class Feat287StateBlock2(val state: Feat287UiModel, val checksum: Int)
data class Feat287StateBlock3(val state: Feat287UiModel, val checksum: Int)
data class Feat287StateBlock4(val state: Feat287UiModel, val checksum: Int)
data class Feat287StateBlock5(val state: Feat287UiModel, val checksum: Int)
data class Feat287StateBlock6(val state: Feat287UiModel, val checksum: Int)
data class Feat287StateBlock7(val state: Feat287UiModel, val checksum: Int)
data class Feat287StateBlock8(val state: Feat287UiModel, val checksum: Int)
data class Feat287StateBlock9(val state: Feat287UiModel, val checksum: Int)
data class Feat287StateBlock10(val state: Feat287UiModel, val checksum: Int)

fun buildFeat287UserItem(user: CoreUser, index: Int): Feat287UserItem1 {
    return Feat287UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat287StateBlock(model: Feat287UiModel): Feat287StateBlock1 {
    return Feat287StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat287UserSummary> {
    val list = java.util.ArrayList<Feat287UserSummary>(users.size)
    for (user in users) {
        list += Feat287UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat287UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat287UiModel {
    val summaries = (0 until count).map {
        Feat287UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat287UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat287UiModel> {
    val models = java.util.ArrayList<Feat287UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat287AnalyticsEvent1(val name: String, val value: String)
data class Feat287AnalyticsEvent2(val name: String, val value: String)
data class Feat287AnalyticsEvent3(val name: String, val value: String)
data class Feat287AnalyticsEvent4(val name: String, val value: String)
data class Feat287AnalyticsEvent5(val name: String, val value: String)
data class Feat287AnalyticsEvent6(val name: String, val value: String)
data class Feat287AnalyticsEvent7(val name: String, val value: String)
data class Feat287AnalyticsEvent8(val name: String, val value: String)
data class Feat287AnalyticsEvent9(val name: String, val value: String)
data class Feat287AnalyticsEvent10(val name: String, val value: String)

fun logFeat287Event1(event: Feat287AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat287Event2(event: Feat287AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat287Event3(event: Feat287AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat287Event4(event: Feat287AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat287Event5(event: Feat287AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat287Event6(event: Feat287AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat287Event7(event: Feat287AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat287Event8(event: Feat287AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat287Event9(event: Feat287AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat287Event10(event: Feat287AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat287Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat287Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat287Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat287Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat287Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat287Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat287Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat287Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat287Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat287Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat287(u: CoreUser): Feat287Projection1 =
    Feat287Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat287Projection1> {
    val list = java.util.ArrayList<Feat287Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat287(u)
    }
    return list
}
