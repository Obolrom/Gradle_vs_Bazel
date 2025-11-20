package com.romix.feature.feat250

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat250Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat250UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat250FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat250UserSummary
)

data class Feat250UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat250NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat250Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat250Config = Feat250Config()
) {

    fun loadSnapshot(userId: Long): Feat250NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat250NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat250UserSummary {
        return Feat250UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat250FeedItem> {
        val result = java.util.ArrayList<Feat250FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat250FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat250UiMapper {

    fun mapToUi(model: List<Feat250FeedItem>): Feat250UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat250UiModel(
            header = UiText("Feat250 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat250UiModel =
        Feat250UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat250UiModel =
        Feat250UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat250UiModel =
        Feat250UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat250Service(
    private val repository: Feat250Repository,
    private val uiMapper: Feat250UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat250UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat250UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat250UserItem1(val user: CoreUser, val label: String)
data class Feat250UserItem2(val user: CoreUser, val label: String)
data class Feat250UserItem3(val user: CoreUser, val label: String)
data class Feat250UserItem4(val user: CoreUser, val label: String)
data class Feat250UserItem5(val user: CoreUser, val label: String)
data class Feat250UserItem6(val user: CoreUser, val label: String)
data class Feat250UserItem7(val user: CoreUser, val label: String)
data class Feat250UserItem8(val user: CoreUser, val label: String)
data class Feat250UserItem9(val user: CoreUser, val label: String)
data class Feat250UserItem10(val user: CoreUser, val label: String)

data class Feat250StateBlock1(val state: Feat250UiModel, val checksum: Int)
data class Feat250StateBlock2(val state: Feat250UiModel, val checksum: Int)
data class Feat250StateBlock3(val state: Feat250UiModel, val checksum: Int)
data class Feat250StateBlock4(val state: Feat250UiModel, val checksum: Int)
data class Feat250StateBlock5(val state: Feat250UiModel, val checksum: Int)
data class Feat250StateBlock6(val state: Feat250UiModel, val checksum: Int)
data class Feat250StateBlock7(val state: Feat250UiModel, val checksum: Int)
data class Feat250StateBlock8(val state: Feat250UiModel, val checksum: Int)
data class Feat250StateBlock9(val state: Feat250UiModel, val checksum: Int)
data class Feat250StateBlock10(val state: Feat250UiModel, val checksum: Int)

fun buildFeat250UserItem(user: CoreUser, index: Int): Feat250UserItem1 {
    return Feat250UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat250StateBlock(model: Feat250UiModel): Feat250StateBlock1 {
    return Feat250StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat250UserSummary> {
    val list = java.util.ArrayList<Feat250UserSummary>(users.size)
    for (user in users) {
        list += Feat250UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat250UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat250UiModel {
    val summaries = (0 until count).map {
        Feat250UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat250UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat250UiModel> {
    val models = java.util.ArrayList<Feat250UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat250AnalyticsEvent1(val name: String, val value: String)
data class Feat250AnalyticsEvent2(val name: String, val value: String)
data class Feat250AnalyticsEvent3(val name: String, val value: String)
data class Feat250AnalyticsEvent4(val name: String, val value: String)
data class Feat250AnalyticsEvent5(val name: String, val value: String)
data class Feat250AnalyticsEvent6(val name: String, val value: String)
data class Feat250AnalyticsEvent7(val name: String, val value: String)
data class Feat250AnalyticsEvent8(val name: String, val value: String)
data class Feat250AnalyticsEvent9(val name: String, val value: String)
data class Feat250AnalyticsEvent10(val name: String, val value: String)

fun logFeat250Event1(event: Feat250AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat250Event2(event: Feat250AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat250Event3(event: Feat250AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat250Event4(event: Feat250AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat250Event5(event: Feat250AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat250Event6(event: Feat250AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat250Event7(event: Feat250AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat250Event8(event: Feat250AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat250Event9(event: Feat250AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat250Event10(event: Feat250AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat250Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat250Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat250Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat250Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat250Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat250Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat250Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat250Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat250Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat250Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat250(u: CoreUser): Feat250Projection1 =
    Feat250Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat250Projection1> {
    val list = java.util.ArrayList<Feat250Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat250(u)
    }
    return list
}
