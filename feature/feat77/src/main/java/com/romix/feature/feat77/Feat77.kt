package com.romix.feature.feat77

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat77Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat77UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat77FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat77UserSummary
)

data class Feat77UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat77NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat77Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat77Config = Feat77Config()
) {

    fun loadSnapshot(userId: Long): Feat77NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat77NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat77UserSummary {
        return Feat77UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat77FeedItem> {
        val result = java.util.ArrayList<Feat77FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat77FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat77UiMapper {

    fun mapToUi(model: List<Feat77FeedItem>): Feat77UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat77UiModel(
            header = UiText("Feat77 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat77UiModel =
        Feat77UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat77UiModel =
        Feat77UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat77UiModel =
        Feat77UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat77Service(
    private val repository: Feat77Repository,
    private val uiMapper: Feat77UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat77UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat77UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat77UserItem1(val user: CoreUser, val label: String)
data class Feat77UserItem2(val user: CoreUser, val label: String)
data class Feat77UserItem3(val user: CoreUser, val label: String)
data class Feat77UserItem4(val user: CoreUser, val label: String)
data class Feat77UserItem5(val user: CoreUser, val label: String)
data class Feat77UserItem6(val user: CoreUser, val label: String)
data class Feat77UserItem7(val user: CoreUser, val label: String)
data class Feat77UserItem8(val user: CoreUser, val label: String)
data class Feat77UserItem9(val user: CoreUser, val label: String)
data class Feat77UserItem10(val user: CoreUser, val label: String)

data class Feat77StateBlock1(val state: Feat77UiModel, val checksum: Int)
data class Feat77StateBlock2(val state: Feat77UiModel, val checksum: Int)
data class Feat77StateBlock3(val state: Feat77UiModel, val checksum: Int)
data class Feat77StateBlock4(val state: Feat77UiModel, val checksum: Int)
data class Feat77StateBlock5(val state: Feat77UiModel, val checksum: Int)
data class Feat77StateBlock6(val state: Feat77UiModel, val checksum: Int)
data class Feat77StateBlock7(val state: Feat77UiModel, val checksum: Int)
data class Feat77StateBlock8(val state: Feat77UiModel, val checksum: Int)
data class Feat77StateBlock9(val state: Feat77UiModel, val checksum: Int)
data class Feat77StateBlock10(val state: Feat77UiModel, val checksum: Int)

fun buildFeat77UserItem(user: CoreUser, index: Int): Feat77UserItem1 {
    return Feat77UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat77StateBlock(model: Feat77UiModel): Feat77StateBlock1 {
    return Feat77StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat77UserSummary> {
    val list = java.util.ArrayList<Feat77UserSummary>(users.size)
    for (user in users) {
        list += Feat77UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat77UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat77UiModel {
    val summaries = (0 until count).map {
        Feat77UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat77UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat77UiModel> {
    val models = java.util.ArrayList<Feat77UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat77AnalyticsEvent1(val name: String, val value: String)
data class Feat77AnalyticsEvent2(val name: String, val value: String)
data class Feat77AnalyticsEvent3(val name: String, val value: String)
data class Feat77AnalyticsEvent4(val name: String, val value: String)
data class Feat77AnalyticsEvent5(val name: String, val value: String)
data class Feat77AnalyticsEvent6(val name: String, val value: String)
data class Feat77AnalyticsEvent7(val name: String, val value: String)
data class Feat77AnalyticsEvent8(val name: String, val value: String)
data class Feat77AnalyticsEvent9(val name: String, val value: String)
data class Feat77AnalyticsEvent10(val name: String, val value: String)

fun logFeat77Event1(event: Feat77AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat77Event2(event: Feat77AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat77Event3(event: Feat77AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat77Event4(event: Feat77AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat77Event5(event: Feat77AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat77Event6(event: Feat77AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat77Event7(event: Feat77AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat77Event8(event: Feat77AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat77Event9(event: Feat77AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat77Event10(event: Feat77AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat77Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat77Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat77Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat77Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat77Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat77Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat77Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat77Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat77Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat77Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat77(u: CoreUser): Feat77Projection1 =
    Feat77Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat77Projection1> {
    val list = java.util.ArrayList<Feat77Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat77(u)
    }
    return list
}
