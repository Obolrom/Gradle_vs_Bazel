package com.romix.feature.feat481

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat481Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat481UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat481FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat481UserSummary
)

data class Feat481UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat481NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat481Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat481Config = Feat481Config()
) {

    fun loadSnapshot(userId: Long): Feat481NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat481NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat481UserSummary {
        return Feat481UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat481FeedItem> {
        val result = java.util.ArrayList<Feat481FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat481FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat481UiMapper {

    fun mapToUi(model: List<Feat481FeedItem>): Feat481UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat481UiModel(
            header = UiText("Feat481 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat481UiModel =
        Feat481UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat481UiModel =
        Feat481UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat481UiModel =
        Feat481UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat481Service(
    private val repository: Feat481Repository,
    private val uiMapper: Feat481UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat481UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat481UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat481UserItem1(val user: CoreUser, val label: String)
data class Feat481UserItem2(val user: CoreUser, val label: String)
data class Feat481UserItem3(val user: CoreUser, val label: String)
data class Feat481UserItem4(val user: CoreUser, val label: String)
data class Feat481UserItem5(val user: CoreUser, val label: String)
data class Feat481UserItem6(val user: CoreUser, val label: String)
data class Feat481UserItem7(val user: CoreUser, val label: String)
data class Feat481UserItem8(val user: CoreUser, val label: String)
data class Feat481UserItem9(val user: CoreUser, val label: String)
data class Feat481UserItem10(val user: CoreUser, val label: String)

data class Feat481StateBlock1(val state: Feat481UiModel, val checksum: Int)
data class Feat481StateBlock2(val state: Feat481UiModel, val checksum: Int)
data class Feat481StateBlock3(val state: Feat481UiModel, val checksum: Int)
data class Feat481StateBlock4(val state: Feat481UiModel, val checksum: Int)
data class Feat481StateBlock5(val state: Feat481UiModel, val checksum: Int)
data class Feat481StateBlock6(val state: Feat481UiModel, val checksum: Int)
data class Feat481StateBlock7(val state: Feat481UiModel, val checksum: Int)
data class Feat481StateBlock8(val state: Feat481UiModel, val checksum: Int)
data class Feat481StateBlock9(val state: Feat481UiModel, val checksum: Int)
data class Feat481StateBlock10(val state: Feat481UiModel, val checksum: Int)

fun buildFeat481UserItem(user: CoreUser, index: Int): Feat481UserItem1 {
    return Feat481UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat481StateBlock(model: Feat481UiModel): Feat481StateBlock1 {
    return Feat481StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat481UserSummary> {
    val list = java.util.ArrayList<Feat481UserSummary>(users.size)
    for (user in users) {
        list += Feat481UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat481UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat481UiModel {
    val summaries = (0 until count).map {
        Feat481UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat481UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat481UiModel> {
    val models = java.util.ArrayList<Feat481UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat481AnalyticsEvent1(val name: String, val value: String)
data class Feat481AnalyticsEvent2(val name: String, val value: String)
data class Feat481AnalyticsEvent3(val name: String, val value: String)
data class Feat481AnalyticsEvent4(val name: String, val value: String)
data class Feat481AnalyticsEvent5(val name: String, val value: String)
data class Feat481AnalyticsEvent6(val name: String, val value: String)
data class Feat481AnalyticsEvent7(val name: String, val value: String)
data class Feat481AnalyticsEvent8(val name: String, val value: String)
data class Feat481AnalyticsEvent9(val name: String, val value: String)
data class Feat481AnalyticsEvent10(val name: String, val value: String)

fun logFeat481Event1(event: Feat481AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat481Event2(event: Feat481AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat481Event3(event: Feat481AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat481Event4(event: Feat481AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat481Event5(event: Feat481AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat481Event6(event: Feat481AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat481Event7(event: Feat481AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat481Event8(event: Feat481AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat481Event9(event: Feat481AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat481Event10(event: Feat481AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat481Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat481Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat481Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat481Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat481Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat481Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat481Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat481Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat481Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat481Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat481(u: CoreUser): Feat481Projection1 =
    Feat481Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat481Projection1> {
    val list = java.util.ArrayList<Feat481Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat481(u)
    }
    return list
}
