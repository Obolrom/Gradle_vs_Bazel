package com.romix.feature.feat284

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat284Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat284UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat284FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat284UserSummary
)

data class Feat284UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat284NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat284Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat284Config = Feat284Config()
) {

    fun loadSnapshot(userId: Long): Feat284NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat284NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat284UserSummary {
        return Feat284UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat284FeedItem> {
        val result = java.util.ArrayList<Feat284FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat284FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat284UiMapper {

    fun mapToUi(model: List<Feat284FeedItem>): Feat284UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat284UiModel(
            header = UiText("Feat284 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat284UiModel =
        Feat284UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat284UiModel =
        Feat284UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat284UiModel =
        Feat284UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat284Service(
    private val repository: Feat284Repository,
    private val uiMapper: Feat284UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat284UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat284UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat284UserItem1(val user: CoreUser, val label: String)
data class Feat284UserItem2(val user: CoreUser, val label: String)
data class Feat284UserItem3(val user: CoreUser, val label: String)
data class Feat284UserItem4(val user: CoreUser, val label: String)
data class Feat284UserItem5(val user: CoreUser, val label: String)
data class Feat284UserItem6(val user: CoreUser, val label: String)
data class Feat284UserItem7(val user: CoreUser, val label: String)
data class Feat284UserItem8(val user: CoreUser, val label: String)
data class Feat284UserItem9(val user: CoreUser, val label: String)
data class Feat284UserItem10(val user: CoreUser, val label: String)

data class Feat284StateBlock1(val state: Feat284UiModel, val checksum: Int)
data class Feat284StateBlock2(val state: Feat284UiModel, val checksum: Int)
data class Feat284StateBlock3(val state: Feat284UiModel, val checksum: Int)
data class Feat284StateBlock4(val state: Feat284UiModel, val checksum: Int)
data class Feat284StateBlock5(val state: Feat284UiModel, val checksum: Int)
data class Feat284StateBlock6(val state: Feat284UiModel, val checksum: Int)
data class Feat284StateBlock7(val state: Feat284UiModel, val checksum: Int)
data class Feat284StateBlock8(val state: Feat284UiModel, val checksum: Int)
data class Feat284StateBlock9(val state: Feat284UiModel, val checksum: Int)
data class Feat284StateBlock10(val state: Feat284UiModel, val checksum: Int)

fun buildFeat284UserItem(user: CoreUser, index: Int): Feat284UserItem1 {
    return Feat284UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat284StateBlock(model: Feat284UiModel): Feat284StateBlock1 {
    return Feat284StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat284UserSummary> {
    val list = java.util.ArrayList<Feat284UserSummary>(users.size)
    for (user in users) {
        list += Feat284UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat284UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat284UiModel {
    val summaries = (0 until count).map {
        Feat284UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat284UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat284UiModel> {
    val models = java.util.ArrayList<Feat284UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat284AnalyticsEvent1(val name: String, val value: String)
data class Feat284AnalyticsEvent2(val name: String, val value: String)
data class Feat284AnalyticsEvent3(val name: String, val value: String)
data class Feat284AnalyticsEvent4(val name: String, val value: String)
data class Feat284AnalyticsEvent5(val name: String, val value: String)
data class Feat284AnalyticsEvent6(val name: String, val value: String)
data class Feat284AnalyticsEvent7(val name: String, val value: String)
data class Feat284AnalyticsEvent8(val name: String, val value: String)
data class Feat284AnalyticsEvent9(val name: String, val value: String)
data class Feat284AnalyticsEvent10(val name: String, val value: String)

fun logFeat284Event1(event: Feat284AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat284Event2(event: Feat284AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat284Event3(event: Feat284AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat284Event4(event: Feat284AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat284Event5(event: Feat284AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat284Event6(event: Feat284AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat284Event7(event: Feat284AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat284Event8(event: Feat284AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat284Event9(event: Feat284AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat284Event10(event: Feat284AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat284Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat284Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat284Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat284Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat284Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat284Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat284Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat284Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat284Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat284Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat284(u: CoreUser): Feat284Projection1 =
    Feat284Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat284Projection1> {
    val list = java.util.ArrayList<Feat284Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat284(u)
    }
    return list
}
