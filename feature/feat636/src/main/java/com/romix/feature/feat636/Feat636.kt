package com.romix.feature.feat636

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat636Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat636UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat636FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat636UserSummary
)

data class Feat636UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat636NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat636Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat636Config = Feat636Config()
) {

    fun loadSnapshot(userId: Long): Feat636NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat636NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat636UserSummary {
        return Feat636UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat636FeedItem> {
        val result = java.util.ArrayList<Feat636FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat636FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat636UiMapper {

    fun mapToUi(model: List<Feat636FeedItem>): Feat636UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat636UiModel(
            header = UiText("Feat636 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat636UiModel =
        Feat636UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat636UiModel =
        Feat636UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat636UiModel =
        Feat636UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat636Service(
    private val repository: Feat636Repository,
    private val uiMapper: Feat636UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat636UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat636UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat636UserItem1(val user: CoreUser, val label: String)
data class Feat636UserItem2(val user: CoreUser, val label: String)
data class Feat636UserItem3(val user: CoreUser, val label: String)
data class Feat636UserItem4(val user: CoreUser, val label: String)
data class Feat636UserItem5(val user: CoreUser, val label: String)
data class Feat636UserItem6(val user: CoreUser, val label: String)
data class Feat636UserItem7(val user: CoreUser, val label: String)
data class Feat636UserItem8(val user: CoreUser, val label: String)
data class Feat636UserItem9(val user: CoreUser, val label: String)
data class Feat636UserItem10(val user: CoreUser, val label: String)

data class Feat636StateBlock1(val state: Feat636UiModel, val checksum: Int)
data class Feat636StateBlock2(val state: Feat636UiModel, val checksum: Int)
data class Feat636StateBlock3(val state: Feat636UiModel, val checksum: Int)
data class Feat636StateBlock4(val state: Feat636UiModel, val checksum: Int)
data class Feat636StateBlock5(val state: Feat636UiModel, val checksum: Int)
data class Feat636StateBlock6(val state: Feat636UiModel, val checksum: Int)
data class Feat636StateBlock7(val state: Feat636UiModel, val checksum: Int)
data class Feat636StateBlock8(val state: Feat636UiModel, val checksum: Int)
data class Feat636StateBlock9(val state: Feat636UiModel, val checksum: Int)
data class Feat636StateBlock10(val state: Feat636UiModel, val checksum: Int)

fun buildFeat636UserItem(user: CoreUser, index: Int): Feat636UserItem1 {
    return Feat636UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat636StateBlock(model: Feat636UiModel): Feat636StateBlock1 {
    return Feat636StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat636UserSummary> {
    val list = java.util.ArrayList<Feat636UserSummary>(users.size)
    for (user in users) {
        list += Feat636UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat636UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat636UiModel {
    val summaries = (0 until count).map {
        Feat636UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat636UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat636UiModel> {
    val models = java.util.ArrayList<Feat636UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat636AnalyticsEvent1(val name: String, val value: String)
data class Feat636AnalyticsEvent2(val name: String, val value: String)
data class Feat636AnalyticsEvent3(val name: String, val value: String)
data class Feat636AnalyticsEvent4(val name: String, val value: String)
data class Feat636AnalyticsEvent5(val name: String, val value: String)
data class Feat636AnalyticsEvent6(val name: String, val value: String)
data class Feat636AnalyticsEvent7(val name: String, val value: String)
data class Feat636AnalyticsEvent8(val name: String, val value: String)
data class Feat636AnalyticsEvent9(val name: String, val value: String)
data class Feat636AnalyticsEvent10(val name: String, val value: String)

fun logFeat636Event1(event: Feat636AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat636Event2(event: Feat636AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat636Event3(event: Feat636AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat636Event4(event: Feat636AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat636Event5(event: Feat636AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat636Event6(event: Feat636AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat636Event7(event: Feat636AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat636Event8(event: Feat636AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat636Event9(event: Feat636AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat636Event10(event: Feat636AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat636Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat636Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat636Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat636Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat636Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat636Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat636Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat636Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat636Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat636Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat636(u: CoreUser): Feat636Projection1 =
    Feat636Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat636Projection1> {
    val list = java.util.ArrayList<Feat636Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat636(u)
    }
    return list
}
