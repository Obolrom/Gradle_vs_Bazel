package com.romix.feature.feat157

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat157Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat157UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat157FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat157UserSummary
)

data class Feat157UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat157NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat157Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat157Config = Feat157Config()
) {

    fun loadSnapshot(userId: Long): Feat157NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat157NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat157UserSummary {
        return Feat157UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat157FeedItem> {
        val result = java.util.ArrayList<Feat157FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat157FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat157UiMapper {

    fun mapToUi(model: List<Feat157FeedItem>): Feat157UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat157UiModel(
            header = UiText("Feat157 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat157UiModel =
        Feat157UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat157UiModel =
        Feat157UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat157UiModel =
        Feat157UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat157Service(
    private val repository: Feat157Repository,
    private val uiMapper: Feat157UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat157UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat157UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat157UserItem1(val user: CoreUser, val label: String)
data class Feat157UserItem2(val user: CoreUser, val label: String)
data class Feat157UserItem3(val user: CoreUser, val label: String)
data class Feat157UserItem4(val user: CoreUser, val label: String)
data class Feat157UserItem5(val user: CoreUser, val label: String)
data class Feat157UserItem6(val user: CoreUser, val label: String)
data class Feat157UserItem7(val user: CoreUser, val label: String)
data class Feat157UserItem8(val user: CoreUser, val label: String)
data class Feat157UserItem9(val user: CoreUser, val label: String)
data class Feat157UserItem10(val user: CoreUser, val label: String)

data class Feat157StateBlock1(val state: Feat157UiModel, val checksum: Int)
data class Feat157StateBlock2(val state: Feat157UiModel, val checksum: Int)
data class Feat157StateBlock3(val state: Feat157UiModel, val checksum: Int)
data class Feat157StateBlock4(val state: Feat157UiModel, val checksum: Int)
data class Feat157StateBlock5(val state: Feat157UiModel, val checksum: Int)
data class Feat157StateBlock6(val state: Feat157UiModel, val checksum: Int)
data class Feat157StateBlock7(val state: Feat157UiModel, val checksum: Int)
data class Feat157StateBlock8(val state: Feat157UiModel, val checksum: Int)
data class Feat157StateBlock9(val state: Feat157UiModel, val checksum: Int)
data class Feat157StateBlock10(val state: Feat157UiModel, val checksum: Int)

fun buildFeat157UserItem(user: CoreUser, index: Int): Feat157UserItem1 {
    return Feat157UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat157StateBlock(model: Feat157UiModel): Feat157StateBlock1 {
    return Feat157StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat157UserSummary> {
    val list = java.util.ArrayList<Feat157UserSummary>(users.size)
    for (user in users) {
        list += Feat157UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat157UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat157UiModel {
    val summaries = (0 until count).map {
        Feat157UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat157UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat157UiModel> {
    val models = java.util.ArrayList<Feat157UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat157AnalyticsEvent1(val name: String, val value: String)
data class Feat157AnalyticsEvent2(val name: String, val value: String)
data class Feat157AnalyticsEvent3(val name: String, val value: String)
data class Feat157AnalyticsEvent4(val name: String, val value: String)
data class Feat157AnalyticsEvent5(val name: String, val value: String)
data class Feat157AnalyticsEvent6(val name: String, val value: String)
data class Feat157AnalyticsEvent7(val name: String, val value: String)
data class Feat157AnalyticsEvent8(val name: String, val value: String)
data class Feat157AnalyticsEvent9(val name: String, val value: String)
data class Feat157AnalyticsEvent10(val name: String, val value: String)

fun logFeat157Event1(event: Feat157AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat157Event2(event: Feat157AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat157Event3(event: Feat157AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat157Event4(event: Feat157AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat157Event5(event: Feat157AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat157Event6(event: Feat157AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat157Event7(event: Feat157AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat157Event8(event: Feat157AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat157Event9(event: Feat157AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat157Event10(event: Feat157AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat157Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat157Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat157Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat157Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat157Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat157Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat157Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat157Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat157Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat157Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat157(u: CoreUser): Feat157Projection1 =
    Feat157Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat157Projection1> {
    val list = java.util.ArrayList<Feat157Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat157(u)
    }
    return list
}
