package com.romix.feature.feat412

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat412Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat412UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat412FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat412UserSummary
)

data class Feat412UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat412NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat412Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat412Config = Feat412Config()
) {

    fun loadSnapshot(userId: Long): Feat412NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat412NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat412UserSummary {
        return Feat412UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat412FeedItem> {
        val result = java.util.ArrayList<Feat412FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat412FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat412UiMapper {

    fun mapToUi(model: List<Feat412FeedItem>): Feat412UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat412UiModel(
            header = UiText("Feat412 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat412UiModel =
        Feat412UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat412UiModel =
        Feat412UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat412UiModel =
        Feat412UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat412Service(
    private val repository: Feat412Repository,
    private val uiMapper: Feat412UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat412UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat412UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat412UserItem1(val user: CoreUser, val label: String)
data class Feat412UserItem2(val user: CoreUser, val label: String)
data class Feat412UserItem3(val user: CoreUser, val label: String)
data class Feat412UserItem4(val user: CoreUser, val label: String)
data class Feat412UserItem5(val user: CoreUser, val label: String)
data class Feat412UserItem6(val user: CoreUser, val label: String)
data class Feat412UserItem7(val user: CoreUser, val label: String)
data class Feat412UserItem8(val user: CoreUser, val label: String)
data class Feat412UserItem9(val user: CoreUser, val label: String)
data class Feat412UserItem10(val user: CoreUser, val label: String)

data class Feat412StateBlock1(val state: Feat412UiModel, val checksum: Int)
data class Feat412StateBlock2(val state: Feat412UiModel, val checksum: Int)
data class Feat412StateBlock3(val state: Feat412UiModel, val checksum: Int)
data class Feat412StateBlock4(val state: Feat412UiModel, val checksum: Int)
data class Feat412StateBlock5(val state: Feat412UiModel, val checksum: Int)
data class Feat412StateBlock6(val state: Feat412UiModel, val checksum: Int)
data class Feat412StateBlock7(val state: Feat412UiModel, val checksum: Int)
data class Feat412StateBlock8(val state: Feat412UiModel, val checksum: Int)
data class Feat412StateBlock9(val state: Feat412UiModel, val checksum: Int)
data class Feat412StateBlock10(val state: Feat412UiModel, val checksum: Int)

fun buildFeat412UserItem(user: CoreUser, index: Int): Feat412UserItem1 {
    return Feat412UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat412StateBlock(model: Feat412UiModel): Feat412StateBlock1 {
    return Feat412StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat412UserSummary> {
    val list = java.util.ArrayList<Feat412UserSummary>(users.size)
    for (user in users) {
        list += Feat412UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat412UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat412UiModel {
    val summaries = (0 until count).map {
        Feat412UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat412UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat412UiModel> {
    val models = java.util.ArrayList<Feat412UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat412AnalyticsEvent1(val name: String, val value: String)
data class Feat412AnalyticsEvent2(val name: String, val value: String)
data class Feat412AnalyticsEvent3(val name: String, val value: String)
data class Feat412AnalyticsEvent4(val name: String, val value: String)
data class Feat412AnalyticsEvent5(val name: String, val value: String)
data class Feat412AnalyticsEvent6(val name: String, val value: String)
data class Feat412AnalyticsEvent7(val name: String, val value: String)
data class Feat412AnalyticsEvent8(val name: String, val value: String)
data class Feat412AnalyticsEvent9(val name: String, val value: String)
data class Feat412AnalyticsEvent10(val name: String, val value: String)

fun logFeat412Event1(event: Feat412AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat412Event2(event: Feat412AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat412Event3(event: Feat412AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat412Event4(event: Feat412AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat412Event5(event: Feat412AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat412Event6(event: Feat412AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat412Event7(event: Feat412AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat412Event8(event: Feat412AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat412Event9(event: Feat412AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat412Event10(event: Feat412AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat412Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat412Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat412Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat412Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat412Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat412Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat412Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat412Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat412Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat412Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat412(u: CoreUser): Feat412Projection1 =
    Feat412Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat412Projection1> {
    val list = java.util.ArrayList<Feat412Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat412(u)
    }
    return list
}
