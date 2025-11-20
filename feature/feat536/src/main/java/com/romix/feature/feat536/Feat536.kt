package com.romix.feature.feat536

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat536Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat536UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat536FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat536UserSummary
)

data class Feat536UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat536NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat536Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat536Config = Feat536Config()
) {

    fun loadSnapshot(userId: Long): Feat536NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat536NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat536UserSummary {
        return Feat536UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat536FeedItem> {
        val result = java.util.ArrayList<Feat536FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat536FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat536UiMapper {

    fun mapToUi(model: List<Feat536FeedItem>): Feat536UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat536UiModel(
            header = UiText("Feat536 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat536UiModel =
        Feat536UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat536UiModel =
        Feat536UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat536UiModel =
        Feat536UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat536Service(
    private val repository: Feat536Repository,
    private val uiMapper: Feat536UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat536UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat536UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat536UserItem1(val user: CoreUser, val label: String)
data class Feat536UserItem2(val user: CoreUser, val label: String)
data class Feat536UserItem3(val user: CoreUser, val label: String)
data class Feat536UserItem4(val user: CoreUser, val label: String)
data class Feat536UserItem5(val user: CoreUser, val label: String)
data class Feat536UserItem6(val user: CoreUser, val label: String)
data class Feat536UserItem7(val user: CoreUser, val label: String)
data class Feat536UserItem8(val user: CoreUser, val label: String)
data class Feat536UserItem9(val user: CoreUser, val label: String)
data class Feat536UserItem10(val user: CoreUser, val label: String)

data class Feat536StateBlock1(val state: Feat536UiModel, val checksum: Int)
data class Feat536StateBlock2(val state: Feat536UiModel, val checksum: Int)
data class Feat536StateBlock3(val state: Feat536UiModel, val checksum: Int)
data class Feat536StateBlock4(val state: Feat536UiModel, val checksum: Int)
data class Feat536StateBlock5(val state: Feat536UiModel, val checksum: Int)
data class Feat536StateBlock6(val state: Feat536UiModel, val checksum: Int)
data class Feat536StateBlock7(val state: Feat536UiModel, val checksum: Int)
data class Feat536StateBlock8(val state: Feat536UiModel, val checksum: Int)
data class Feat536StateBlock9(val state: Feat536UiModel, val checksum: Int)
data class Feat536StateBlock10(val state: Feat536UiModel, val checksum: Int)

fun buildFeat536UserItem(user: CoreUser, index: Int): Feat536UserItem1 {
    return Feat536UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat536StateBlock(model: Feat536UiModel): Feat536StateBlock1 {
    return Feat536StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat536UserSummary> {
    val list = java.util.ArrayList<Feat536UserSummary>(users.size)
    for (user in users) {
        list += Feat536UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat536UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat536UiModel {
    val summaries = (0 until count).map {
        Feat536UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat536UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat536UiModel> {
    val models = java.util.ArrayList<Feat536UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat536AnalyticsEvent1(val name: String, val value: String)
data class Feat536AnalyticsEvent2(val name: String, val value: String)
data class Feat536AnalyticsEvent3(val name: String, val value: String)
data class Feat536AnalyticsEvent4(val name: String, val value: String)
data class Feat536AnalyticsEvent5(val name: String, val value: String)
data class Feat536AnalyticsEvent6(val name: String, val value: String)
data class Feat536AnalyticsEvent7(val name: String, val value: String)
data class Feat536AnalyticsEvent8(val name: String, val value: String)
data class Feat536AnalyticsEvent9(val name: String, val value: String)
data class Feat536AnalyticsEvent10(val name: String, val value: String)

fun logFeat536Event1(event: Feat536AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat536Event2(event: Feat536AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat536Event3(event: Feat536AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat536Event4(event: Feat536AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat536Event5(event: Feat536AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat536Event6(event: Feat536AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat536Event7(event: Feat536AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat536Event8(event: Feat536AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat536Event9(event: Feat536AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat536Event10(event: Feat536AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat536Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat536Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat536Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat536Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat536Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat536Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat536Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat536Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat536Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat536Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat536(u: CoreUser): Feat536Projection1 =
    Feat536Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat536Projection1> {
    val list = java.util.ArrayList<Feat536Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat536(u)
    }
    return list
}
