package com.romix.feature.feat618

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat618Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat618UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat618FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat618UserSummary
)

data class Feat618UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat618NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat618Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat618Config = Feat618Config()
) {

    fun loadSnapshot(userId: Long): Feat618NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat618NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat618UserSummary {
        return Feat618UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat618FeedItem> {
        val result = java.util.ArrayList<Feat618FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat618FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat618UiMapper {

    fun mapToUi(model: List<Feat618FeedItem>): Feat618UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat618UiModel(
            header = UiText("Feat618 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat618UiModel =
        Feat618UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat618UiModel =
        Feat618UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat618UiModel =
        Feat618UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat618Service(
    private val repository: Feat618Repository,
    private val uiMapper: Feat618UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat618UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat618UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat618UserItem1(val user: CoreUser, val label: String)
data class Feat618UserItem2(val user: CoreUser, val label: String)
data class Feat618UserItem3(val user: CoreUser, val label: String)
data class Feat618UserItem4(val user: CoreUser, val label: String)
data class Feat618UserItem5(val user: CoreUser, val label: String)
data class Feat618UserItem6(val user: CoreUser, val label: String)
data class Feat618UserItem7(val user: CoreUser, val label: String)
data class Feat618UserItem8(val user: CoreUser, val label: String)
data class Feat618UserItem9(val user: CoreUser, val label: String)
data class Feat618UserItem10(val user: CoreUser, val label: String)

data class Feat618StateBlock1(val state: Feat618UiModel, val checksum: Int)
data class Feat618StateBlock2(val state: Feat618UiModel, val checksum: Int)
data class Feat618StateBlock3(val state: Feat618UiModel, val checksum: Int)
data class Feat618StateBlock4(val state: Feat618UiModel, val checksum: Int)
data class Feat618StateBlock5(val state: Feat618UiModel, val checksum: Int)
data class Feat618StateBlock6(val state: Feat618UiModel, val checksum: Int)
data class Feat618StateBlock7(val state: Feat618UiModel, val checksum: Int)
data class Feat618StateBlock8(val state: Feat618UiModel, val checksum: Int)
data class Feat618StateBlock9(val state: Feat618UiModel, val checksum: Int)
data class Feat618StateBlock10(val state: Feat618UiModel, val checksum: Int)

fun buildFeat618UserItem(user: CoreUser, index: Int): Feat618UserItem1 {
    return Feat618UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat618StateBlock(model: Feat618UiModel): Feat618StateBlock1 {
    return Feat618StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat618UserSummary> {
    val list = java.util.ArrayList<Feat618UserSummary>(users.size)
    for (user in users) {
        list += Feat618UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat618UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat618UiModel {
    val summaries = (0 until count).map {
        Feat618UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat618UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat618UiModel> {
    val models = java.util.ArrayList<Feat618UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat618AnalyticsEvent1(val name: String, val value: String)
data class Feat618AnalyticsEvent2(val name: String, val value: String)
data class Feat618AnalyticsEvent3(val name: String, val value: String)
data class Feat618AnalyticsEvent4(val name: String, val value: String)
data class Feat618AnalyticsEvent5(val name: String, val value: String)
data class Feat618AnalyticsEvent6(val name: String, val value: String)
data class Feat618AnalyticsEvent7(val name: String, val value: String)
data class Feat618AnalyticsEvent8(val name: String, val value: String)
data class Feat618AnalyticsEvent9(val name: String, val value: String)
data class Feat618AnalyticsEvent10(val name: String, val value: String)

fun logFeat618Event1(event: Feat618AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat618Event2(event: Feat618AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat618Event3(event: Feat618AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat618Event4(event: Feat618AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat618Event5(event: Feat618AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat618Event6(event: Feat618AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat618Event7(event: Feat618AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat618Event8(event: Feat618AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat618Event9(event: Feat618AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat618Event10(event: Feat618AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat618Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat618Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat618Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat618Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat618Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat618Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat618Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat618Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat618Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat618Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat618(u: CoreUser): Feat618Projection1 =
    Feat618Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat618Projection1> {
    val list = java.util.ArrayList<Feat618Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat618(u)
    }
    return list
}
