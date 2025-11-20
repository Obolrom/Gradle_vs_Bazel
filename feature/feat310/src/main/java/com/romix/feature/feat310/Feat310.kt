package com.romix.feature.feat310

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat310Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat310UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat310FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat310UserSummary
)

data class Feat310UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat310NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat310Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat310Config = Feat310Config()
) {

    fun loadSnapshot(userId: Long): Feat310NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat310NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat310UserSummary {
        return Feat310UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat310FeedItem> {
        val result = java.util.ArrayList<Feat310FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat310FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat310UiMapper {

    fun mapToUi(model: List<Feat310FeedItem>): Feat310UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat310UiModel(
            header = UiText("Feat310 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat310UiModel =
        Feat310UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat310UiModel =
        Feat310UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat310UiModel =
        Feat310UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat310Service(
    private val repository: Feat310Repository,
    private val uiMapper: Feat310UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat310UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat310UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat310UserItem1(val user: CoreUser, val label: String)
data class Feat310UserItem2(val user: CoreUser, val label: String)
data class Feat310UserItem3(val user: CoreUser, val label: String)
data class Feat310UserItem4(val user: CoreUser, val label: String)
data class Feat310UserItem5(val user: CoreUser, val label: String)
data class Feat310UserItem6(val user: CoreUser, val label: String)
data class Feat310UserItem7(val user: CoreUser, val label: String)
data class Feat310UserItem8(val user: CoreUser, val label: String)
data class Feat310UserItem9(val user: CoreUser, val label: String)
data class Feat310UserItem10(val user: CoreUser, val label: String)

data class Feat310StateBlock1(val state: Feat310UiModel, val checksum: Int)
data class Feat310StateBlock2(val state: Feat310UiModel, val checksum: Int)
data class Feat310StateBlock3(val state: Feat310UiModel, val checksum: Int)
data class Feat310StateBlock4(val state: Feat310UiModel, val checksum: Int)
data class Feat310StateBlock5(val state: Feat310UiModel, val checksum: Int)
data class Feat310StateBlock6(val state: Feat310UiModel, val checksum: Int)
data class Feat310StateBlock7(val state: Feat310UiModel, val checksum: Int)
data class Feat310StateBlock8(val state: Feat310UiModel, val checksum: Int)
data class Feat310StateBlock9(val state: Feat310UiModel, val checksum: Int)
data class Feat310StateBlock10(val state: Feat310UiModel, val checksum: Int)

fun buildFeat310UserItem(user: CoreUser, index: Int): Feat310UserItem1 {
    return Feat310UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat310StateBlock(model: Feat310UiModel): Feat310StateBlock1 {
    return Feat310StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat310UserSummary> {
    val list = java.util.ArrayList<Feat310UserSummary>(users.size)
    for (user in users) {
        list += Feat310UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat310UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat310UiModel {
    val summaries = (0 until count).map {
        Feat310UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat310UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat310UiModel> {
    val models = java.util.ArrayList<Feat310UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat310AnalyticsEvent1(val name: String, val value: String)
data class Feat310AnalyticsEvent2(val name: String, val value: String)
data class Feat310AnalyticsEvent3(val name: String, val value: String)
data class Feat310AnalyticsEvent4(val name: String, val value: String)
data class Feat310AnalyticsEvent5(val name: String, val value: String)
data class Feat310AnalyticsEvent6(val name: String, val value: String)
data class Feat310AnalyticsEvent7(val name: String, val value: String)
data class Feat310AnalyticsEvent8(val name: String, val value: String)
data class Feat310AnalyticsEvent9(val name: String, val value: String)
data class Feat310AnalyticsEvent10(val name: String, val value: String)

fun logFeat310Event1(event: Feat310AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat310Event2(event: Feat310AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat310Event3(event: Feat310AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat310Event4(event: Feat310AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat310Event5(event: Feat310AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat310Event6(event: Feat310AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat310Event7(event: Feat310AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat310Event8(event: Feat310AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat310Event9(event: Feat310AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat310Event10(event: Feat310AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat310Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat310Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat310Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat310Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat310Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat310Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat310Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat310Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat310Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat310Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat310(u: CoreUser): Feat310Projection1 =
    Feat310Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat310Projection1> {
    val list = java.util.ArrayList<Feat310Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat310(u)
    }
    return list
}
