package com.romix.feature.feat43

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat43Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat43UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat43FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat43UserSummary
)

data class Feat43UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat43NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat43Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat43Config = Feat43Config()
) {

    fun loadSnapshot(userId: Long): Feat43NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat43NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat43UserSummary {
        return Feat43UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat43FeedItem> {
        val result = java.util.ArrayList<Feat43FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat43FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat43UiMapper {

    fun mapToUi(model: List<Feat43FeedItem>): Feat43UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat43UiModel(
            header = UiText("Feat43 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat43UiModel =
        Feat43UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat43UiModel =
        Feat43UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat43UiModel =
        Feat43UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat43Service(
    private val repository: Feat43Repository,
    private val uiMapper: Feat43UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat43UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat43UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat43UserItem1(val user: CoreUser, val label: String)
data class Feat43UserItem2(val user: CoreUser, val label: String)
data class Feat43UserItem3(val user: CoreUser, val label: String)
data class Feat43UserItem4(val user: CoreUser, val label: String)
data class Feat43UserItem5(val user: CoreUser, val label: String)
data class Feat43UserItem6(val user: CoreUser, val label: String)
data class Feat43UserItem7(val user: CoreUser, val label: String)
data class Feat43UserItem8(val user: CoreUser, val label: String)
data class Feat43UserItem9(val user: CoreUser, val label: String)
data class Feat43UserItem10(val user: CoreUser, val label: String)

data class Feat43StateBlock1(val state: Feat43UiModel, val checksum: Int)
data class Feat43StateBlock2(val state: Feat43UiModel, val checksum: Int)
data class Feat43StateBlock3(val state: Feat43UiModel, val checksum: Int)
data class Feat43StateBlock4(val state: Feat43UiModel, val checksum: Int)
data class Feat43StateBlock5(val state: Feat43UiModel, val checksum: Int)
data class Feat43StateBlock6(val state: Feat43UiModel, val checksum: Int)
data class Feat43StateBlock7(val state: Feat43UiModel, val checksum: Int)
data class Feat43StateBlock8(val state: Feat43UiModel, val checksum: Int)
data class Feat43StateBlock9(val state: Feat43UiModel, val checksum: Int)
data class Feat43StateBlock10(val state: Feat43UiModel, val checksum: Int)

fun buildFeat43UserItem(user: CoreUser, index: Int): Feat43UserItem1 {
    return Feat43UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat43StateBlock(model: Feat43UiModel): Feat43StateBlock1 {
    return Feat43StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat43UserSummary> {
    val list = java.util.ArrayList<Feat43UserSummary>(users.size)
    for (user in users) {
        list += Feat43UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat43UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat43UiModel {
    val summaries = (0 until count).map {
        Feat43UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat43UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat43UiModel> {
    val models = java.util.ArrayList<Feat43UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat43AnalyticsEvent1(val name: String, val value: String)
data class Feat43AnalyticsEvent2(val name: String, val value: String)
data class Feat43AnalyticsEvent3(val name: String, val value: String)
data class Feat43AnalyticsEvent4(val name: String, val value: String)
data class Feat43AnalyticsEvent5(val name: String, val value: String)
data class Feat43AnalyticsEvent6(val name: String, val value: String)
data class Feat43AnalyticsEvent7(val name: String, val value: String)
data class Feat43AnalyticsEvent8(val name: String, val value: String)
data class Feat43AnalyticsEvent9(val name: String, val value: String)
data class Feat43AnalyticsEvent10(val name: String, val value: String)

fun logFeat43Event1(event: Feat43AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat43Event2(event: Feat43AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat43Event3(event: Feat43AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat43Event4(event: Feat43AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat43Event5(event: Feat43AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat43Event6(event: Feat43AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat43Event7(event: Feat43AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat43Event8(event: Feat43AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat43Event9(event: Feat43AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat43Event10(event: Feat43AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat43Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat43Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat43Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat43Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat43Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat43Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat43Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat43Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat43Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat43Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat43(u: CoreUser): Feat43Projection1 =
    Feat43Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat43Projection1> {
    val list = java.util.ArrayList<Feat43Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat43(u)
    }
    return list
}
