package com.romix.feature.feat225

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat225Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat225UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat225FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat225UserSummary
)

data class Feat225UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat225NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat225Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat225Config = Feat225Config()
) {

    fun loadSnapshot(userId: Long): Feat225NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat225NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat225UserSummary {
        return Feat225UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat225FeedItem> {
        val result = java.util.ArrayList<Feat225FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat225FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat225UiMapper {

    fun mapToUi(model: List<Feat225FeedItem>): Feat225UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat225UiModel(
            header = UiText("Feat225 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat225UiModel =
        Feat225UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat225UiModel =
        Feat225UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat225UiModel =
        Feat225UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat225Service(
    private val repository: Feat225Repository,
    private val uiMapper: Feat225UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat225UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat225UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat225UserItem1(val user: CoreUser, val label: String)
data class Feat225UserItem2(val user: CoreUser, val label: String)
data class Feat225UserItem3(val user: CoreUser, val label: String)
data class Feat225UserItem4(val user: CoreUser, val label: String)
data class Feat225UserItem5(val user: CoreUser, val label: String)
data class Feat225UserItem6(val user: CoreUser, val label: String)
data class Feat225UserItem7(val user: CoreUser, val label: String)
data class Feat225UserItem8(val user: CoreUser, val label: String)
data class Feat225UserItem9(val user: CoreUser, val label: String)
data class Feat225UserItem10(val user: CoreUser, val label: String)

data class Feat225StateBlock1(val state: Feat225UiModel, val checksum: Int)
data class Feat225StateBlock2(val state: Feat225UiModel, val checksum: Int)
data class Feat225StateBlock3(val state: Feat225UiModel, val checksum: Int)
data class Feat225StateBlock4(val state: Feat225UiModel, val checksum: Int)
data class Feat225StateBlock5(val state: Feat225UiModel, val checksum: Int)
data class Feat225StateBlock6(val state: Feat225UiModel, val checksum: Int)
data class Feat225StateBlock7(val state: Feat225UiModel, val checksum: Int)
data class Feat225StateBlock8(val state: Feat225UiModel, val checksum: Int)
data class Feat225StateBlock9(val state: Feat225UiModel, val checksum: Int)
data class Feat225StateBlock10(val state: Feat225UiModel, val checksum: Int)

fun buildFeat225UserItem(user: CoreUser, index: Int): Feat225UserItem1 {
    return Feat225UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat225StateBlock(model: Feat225UiModel): Feat225StateBlock1 {
    return Feat225StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat225UserSummary> {
    val list = java.util.ArrayList<Feat225UserSummary>(users.size)
    for (user in users) {
        list += Feat225UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat225UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat225UiModel {
    val summaries = (0 until count).map {
        Feat225UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat225UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat225UiModel> {
    val models = java.util.ArrayList<Feat225UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat225AnalyticsEvent1(val name: String, val value: String)
data class Feat225AnalyticsEvent2(val name: String, val value: String)
data class Feat225AnalyticsEvent3(val name: String, val value: String)
data class Feat225AnalyticsEvent4(val name: String, val value: String)
data class Feat225AnalyticsEvent5(val name: String, val value: String)
data class Feat225AnalyticsEvent6(val name: String, val value: String)
data class Feat225AnalyticsEvent7(val name: String, val value: String)
data class Feat225AnalyticsEvent8(val name: String, val value: String)
data class Feat225AnalyticsEvent9(val name: String, val value: String)
data class Feat225AnalyticsEvent10(val name: String, val value: String)

fun logFeat225Event1(event: Feat225AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat225Event2(event: Feat225AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat225Event3(event: Feat225AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat225Event4(event: Feat225AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat225Event5(event: Feat225AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat225Event6(event: Feat225AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat225Event7(event: Feat225AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat225Event8(event: Feat225AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat225Event9(event: Feat225AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat225Event10(event: Feat225AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat225Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat225Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat225Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat225Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat225Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat225Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat225Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat225Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat225Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat225Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat225(u: CoreUser): Feat225Projection1 =
    Feat225Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat225Projection1> {
    val list = java.util.ArrayList<Feat225Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat225(u)
    }
    return list
}
