package com.romix.feature.feat88

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat88Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat88UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat88FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat88UserSummary
)

data class Feat88UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat88NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat88Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat88Config = Feat88Config()
) {

    fun loadSnapshot(userId: Long): Feat88NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat88NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat88UserSummary {
        return Feat88UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat88FeedItem> {
        val result = java.util.ArrayList<Feat88FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat88FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat88UiMapper {

    fun mapToUi(model: List<Feat88FeedItem>): Feat88UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat88UiModel(
            header = UiText("Feat88 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat88UiModel =
        Feat88UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat88UiModel =
        Feat88UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat88UiModel =
        Feat88UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat88Service(
    private val repository: Feat88Repository,
    private val uiMapper: Feat88UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat88UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat88UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat88UserItem1(val user: CoreUser, val label: String)
data class Feat88UserItem2(val user: CoreUser, val label: String)
data class Feat88UserItem3(val user: CoreUser, val label: String)
data class Feat88UserItem4(val user: CoreUser, val label: String)
data class Feat88UserItem5(val user: CoreUser, val label: String)
data class Feat88UserItem6(val user: CoreUser, val label: String)
data class Feat88UserItem7(val user: CoreUser, val label: String)
data class Feat88UserItem8(val user: CoreUser, val label: String)
data class Feat88UserItem9(val user: CoreUser, val label: String)
data class Feat88UserItem10(val user: CoreUser, val label: String)

data class Feat88StateBlock1(val state: Feat88UiModel, val checksum: Int)
data class Feat88StateBlock2(val state: Feat88UiModel, val checksum: Int)
data class Feat88StateBlock3(val state: Feat88UiModel, val checksum: Int)
data class Feat88StateBlock4(val state: Feat88UiModel, val checksum: Int)
data class Feat88StateBlock5(val state: Feat88UiModel, val checksum: Int)
data class Feat88StateBlock6(val state: Feat88UiModel, val checksum: Int)
data class Feat88StateBlock7(val state: Feat88UiModel, val checksum: Int)
data class Feat88StateBlock8(val state: Feat88UiModel, val checksum: Int)
data class Feat88StateBlock9(val state: Feat88UiModel, val checksum: Int)
data class Feat88StateBlock10(val state: Feat88UiModel, val checksum: Int)

fun buildFeat88UserItem(user: CoreUser, index: Int): Feat88UserItem1 {
    return Feat88UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat88StateBlock(model: Feat88UiModel): Feat88StateBlock1 {
    return Feat88StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat88UserSummary> {
    val list = java.util.ArrayList<Feat88UserSummary>(users.size)
    for (user in users) {
        list += Feat88UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat88UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat88UiModel {
    val summaries = (0 until count).map {
        Feat88UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat88UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat88UiModel> {
    val models = java.util.ArrayList<Feat88UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat88AnalyticsEvent1(val name: String, val value: String)
data class Feat88AnalyticsEvent2(val name: String, val value: String)
data class Feat88AnalyticsEvent3(val name: String, val value: String)
data class Feat88AnalyticsEvent4(val name: String, val value: String)
data class Feat88AnalyticsEvent5(val name: String, val value: String)
data class Feat88AnalyticsEvent6(val name: String, val value: String)
data class Feat88AnalyticsEvent7(val name: String, val value: String)
data class Feat88AnalyticsEvent8(val name: String, val value: String)
data class Feat88AnalyticsEvent9(val name: String, val value: String)
data class Feat88AnalyticsEvent10(val name: String, val value: String)

fun logFeat88Event1(event: Feat88AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat88Event2(event: Feat88AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat88Event3(event: Feat88AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat88Event4(event: Feat88AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat88Event5(event: Feat88AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat88Event6(event: Feat88AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat88Event7(event: Feat88AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat88Event8(event: Feat88AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat88Event9(event: Feat88AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat88Event10(event: Feat88AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat88Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat88Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat88Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat88Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat88Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat88Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat88Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat88Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat88Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat88Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat88(u: CoreUser): Feat88Projection1 =
    Feat88Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat88Projection1> {
    val list = java.util.ArrayList<Feat88Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat88(u)
    }
    return list
}
