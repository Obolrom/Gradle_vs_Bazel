package com.romix.feature.feat300

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat300Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat300UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat300FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat300UserSummary
)

data class Feat300UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat300NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat300Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat300Config = Feat300Config()
) {

    fun loadSnapshot(userId: Long): Feat300NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat300NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat300UserSummary {
        return Feat300UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat300FeedItem> {
        val result = java.util.ArrayList<Feat300FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat300FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat300UiMapper {

    fun mapToUi(model: List<Feat300FeedItem>): Feat300UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat300UiModel(
            header = UiText("Feat300 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat300UiModel =
        Feat300UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat300UiModel =
        Feat300UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat300UiModel =
        Feat300UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat300Service(
    private val repository: Feat300Repository,
    private val uiMapper: Feat300UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat300UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat300UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat300UserItem1(val user: CoreUser, val label: String)
data class Feat300UserItem2(val user: CoreUser, val label: String)
data class Feat300UserItem3(val user: CoreUser, val label: String)
data class Feat300UserItem4(val user: CoreUser, val label: String)
data class Feat300UserItem5(val user: CoreUser, val label: String)
data class Feat300UserItem6(val user: CoreUser, val label: String)
data class Feat300UserItem7(val user: CoreUser, val label: String)
data class Feat300UserItem8(val user: CoreUser, val label: String)
data class Feat300UserItem9(val user: CoreUser, val label: String)
data class Feat300UserItem10(val user: CoreUser, val label: String)

data class Feat300StateBlock1(val state: Feat300UiModel, val checksum: Int)
data class Feat300StateBlock2(val state: Feat300UiModel, val checksum: Int)
data class Feat300StateBlock3(val state: Feat300UiModel, val checksum: Int)
data class Feat300StateBlock4(val state: Feat300UiModel, val checksum: Int)
data class Feat300StateBlock5(val state: Feat300UiModel, val checksum: Int)
data class Feat300StateBlock6(val state: Feat300UiModel, val checksum: Int)
data class Feat300StateBlock7(val state: Feat300UiModel, val checksum: Int)
data class Feat300StateBlock8(val state: Feat300UiModel, val checksum: Int)
data class Feat300StateBlock9(val state: Feat300UiModel, val checksum: Int)
data class Feat300StateBlock10(val state: Feat300UiModel, val checksum: Int)

fun buildFeat300UserItem(user: CoreUser, index: Int): Feat300UserItem1 {
    return Feat300UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat300StateBlock(model: Feat300UiModel): Feat300StateBlock1 {
    return Feat300StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat300UserSummary> {
    val list = java.util.ArrayList<Feat300UserSummary>(users.size)
    for (user in users) {
        list += Feat300UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat300UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat300UiModel {
    val summaries = (0 until count).map {
        Feat300UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat300UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat300UiModel> {
    val models = java.util.ArrayList<Feat300UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat300AnalyticsEvent1(val name: String, val value: String)
data class Feat300AnalyticsEvent2(val name: String, val value: String)
data class Feat300AnalyticsEvent3(val name: String, val value: String)
data class Feat300AnalyticsEvent4(val name: String, val value: String)
data class Feat300AnalyticsEvent5(val name: String, val value: String)
data class Feat300AnalyticsEvent6(val name: String, val value: String)
data class Feat300AnalyticsEvent7(val name: String, val value: String)
data class Feat300AnalyticsEvent8(val name: String, val value: String)
data class Feat300AnalyticsEvent9(val name: String, val value: String)
data class Feat300AnalyticsEvent10(val name: String, val value: String)

fun logFeat300Event1(event: Feat300AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat300Event2(event: Feat300AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat300Event3(event: Feat300AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat300Event4(event: Feat300AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat300Event5(event: Feat300AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat300Event6(event: Feat300AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat300Event7(event: Feat300AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat300Event8(event: Feat300AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat300Event9(event: Feat300AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat300Event10(event: Feat300AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat300Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat300Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat300Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat300Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat300Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat300Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat300Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat300Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat300Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat300Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat300(u: CoreUser): Feat300Projection1 =
    Feat300Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat300Projection1> {
    val list = java.util.ArrayList<Feat300Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat300(u)
    }
    return list
}
