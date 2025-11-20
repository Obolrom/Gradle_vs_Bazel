package com.romix.feature.feat243

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat243Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat243UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat243FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat243UserSummary
)

data class Feat243UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat243NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat243Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat243Config = Feat243Config()
) {

    fun loadSnapshot(userId: Long): Feat243NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat243NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat243UserSummary {
        return Feat243UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat243FeedItem> {
        val result = java.util.ArrayList<Feat243FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat243FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat243UiMapper {

    fun mapToUi(model: List<Feat243FeedItem>): Feat243UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat243UiModel(
            header = UiText("Feat243 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat243UiModel =
        Feat243UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat243UiModel =
        Feat243UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat243UiModel =
        Feat243UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat243Service(
    private val repository: Feat243Repository,
    private val uiMapper: Feat243UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat243UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat243UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat243UserItem1(val user: CoreUser, val label: String)
data class Feat243UserItem2(val user: CoreUser, val label: String)
data class Feat243UserItem3(val user: CoreUser, val label: String)
data class Feat243UserItem4(val user: CoreUser, val label: String)
data class Feat243UserItem5(val user: CoreUser, val label: String)
data class Feat243UserItem6(val user: CoreUser, val label: String)
data class Feat243UserItem7(val user: CoreUser, val label: String)
data class Feat243UserItem8(val user: CoreUser, val label: String)
data class Feat243UserItem9(val user: CoreUser, val label: String)
data class Feat243UserItem10(val user: CoreUser, val label: String)

data class Feat243StateBlock1(val state: Feat243UiModel, val checksum: Int)
data class Feat243StateBlock2(val state: Feat243UiModel, val checksum: Int)
data class Feat243StateBlock3(val state: Feat243UiModel, val checksum: Int)
data class Feat243StateBlock4(val state: Feat243UiModel, val checksum: Int)
data class Feat243StateBlock5(val state: Feat243UiModel, val checksum: Int)
data class Feat243StateBlock6(val state: Feat243UiModel, val checksum: Int)
data class Feat243StateBlock7(val state: Feat243UiModel, val checksum: Int)
data class Feat243StateBlock8(val state: Feat243UiModel, val checksum: Int)
data class Feat243StateBlock9(val state: Feat243UiModel, val checksum: Int)
data class Feat243StateBlock10(val state: Feat243UiModel, val checksum: Int)

fun buildFeat243UserItem(user: CoreUser, index: Int): Feat243UserItem1 {
    return Feat243UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat243StateBlock(model: Feat243UiModel): Feat243StateBlock1 {
    return Feat243StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat243UserSummary> {
    val list = java.util.ArrayList<Feat243UserSummary>(users.size)
    for (user in users) {
        list += Feat243UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat243UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat243UiModel {
    val summaries = (0 until count).map {
        Feat243UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat243UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat243UiModel> {
    val models = java.util.ArrayList<Feat243UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat243AnalyticsEvent1(val name: String, val value: String)
data class Feat243AnalyticsEvent2(val name: String, val value: String)
data class Feat243AnalyticsEvent3(val name: String, val value: String)
data class Feat243AnalyticsEvent4(val name: String, val value: String)
data class Feat243AnalyticsEvent5(val name: String, val value: String)
data class Feat243AnalyticsEvent6(val name: String, val value: String)
data class Feat243AnalyticsEvent7(val name: String, val value: String)
data class Feat243AnalyticsEvent8(val name: String, val value: String)
data class Feat243AnalyticsEvent9(val name: String, val value: String)
data class Feat243AnalyticsEvent10(val name: String, val value: String)

fun logFeat243Event1(event: Feat243AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat243Event2(event: Feat243AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat243Event3(event: Feat243AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat243Event4(event: Feat243AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat243Event5(event: Feat243AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat243Event6(event: Feat243AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat243Event7(event: Feat243AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat243Event8(event: Feat243AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat243Event9(event: Feat243AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat243Event10(event: Feat243AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat243Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat243Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat243Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat243Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat243Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat243Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat243Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat243Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat243Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat243Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat243(u: CoreUser): Feat243Projection1 =
    Feat243Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat243Projection1> {
    val list = java.util.ArrayList<Feat243Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat243(u)
    }
    return list
}
