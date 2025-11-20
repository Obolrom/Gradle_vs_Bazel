package com.romix.feature.feat369

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat369Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat369UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat369FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat369UserSummary
)

data class Feat369UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat369NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat369Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat369Config = Feat369Config()
) {

    fun loadSnapshot(userId: Long): Feat369NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat369NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat369UserSummary {
        return Feat369UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat369FeedItem> {
        val result = java.util.ArrayList<Feat369FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat369FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat369UiMapper {

    fun mapToUi(model: List<Feat369FeedItem>): Feat369UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat369UiModel(
            header = UiText("Feat369 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat369UiModel =
        Feat369UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat369UiModel =
        Feat369UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat369UiModel =
        Feat369UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat369Service(
    private val repository: Feat369Repository,
    private val uiMapper: Feat369UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat369UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat369UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat369UserItem1(val user: CoreUser, val label: String)
data class Feat369UserItem2(val user: CoreUser, val label: String)
data class Feat369UserItem3(val user: CoreUser, val label: String)
data class Feat369UserItem4(val user: CoreUser, val label: String)
data class Feat369UserItem5(val user: CoreUser, val label: String)
data class Feat369UserItem6(val user: CoreUser, val label: String)
data class Feat369UserItem7(val user: CoreUser, val label: String)
data class Feat369UserItem8(val user: CoreUser, val label: String)
data class Feat369UserItem9(val user: CoreUser, val label: String)
data class Feat369UserItem10(val user: CoreUser, val label: String)

data class Feat369StateBlock1(val state: Feat369UiModel, val checksum: Int)
data class Feat369StateBlock2(val state: Feat369UiModel, val checksum: Int)
data class Feat369StateBlock3(val state: Feat369UiModel, val checksum: Int)
data class Feat369StateBlock4(val state: Feat369UiModel, val checksum: Int)
data class Feat369StateBlock5(val state: Feat369UiModel, val checksum: Int)
data class Feat369StateBlock6(val state: Feat369UiModel, val checksum: Int)
data class Feat369StateBlock7(val state: Feat369UiModel, val checksum: Int)
data class Feat369StateBlock8(val state: Feat369UiModel, val checksum: Int)
data class Feat369StateBlock9(val state: Feat369UiModel, val checksum: Int)
data class Feat369StateBlock10(val state: Feat369UiModel, val checksum: Int)

fun buildFeat369UserItem(user: CoreUser, index: Int): Feat369UserItem1 {
    return Feat369UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat369StateBlock(model: Feat369UiModel): Feat369StateBlock1 {
    return Feat369StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat369UserSummary> {
    val list = java.util.ArrayList<Feat369UserSummary>(users.size)
    for (user in users) {
        list += Feat369UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat369UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat369UiModel {
    val summaries = (0 until count).map {
        Feat369UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat369UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat369UiModel> {
    val models = java.util.ArrayList<Feat369UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat369AnalyticsEvent1(val name: String, val value: String)
data class Feat369AnalyticsEvent2(val name: String, val value: String)
data class Feat369AnalyticsEvent3(val name: String, val value: String)
data class Feat369AnalyticsEvent4(val name: String, val value: String)
data class Feat369AnalyticsEvent5(val name: String, val value: String)
data class Feat369AnalyticsEvent6(val name: String, val value: String)
data class Feat369AnalyticsEvent7(val name: String, val value: String)
data class Feat369AnalyticsEvent8(val name: String, val value: String)
data class Feat369AnalyticsEvent9(val name: String, val value: String)
data class Feat369AnalyticsEvent10(val name: String, val value: String)

fun logFeat369Event1(event: Feat369AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat369Event2(event: Feat369AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat369Event3(event: Feat369AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat369Event4(event: Feat369AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat369Event5(event: Feat369AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat369Event6(event: Feat369AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat369Event7(event: Feat369AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat369Event8(event: Feat369AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat369Event9(event: Feat369AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat369Event10(event: Feat369AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat369Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat369Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat369Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat369Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat369Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat369Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat369Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat369Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat369Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat369Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat369(u: CoreUser): Feat369Projection1 =
    Feat369Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat369Projection1> {
    val list = java.util.ArrayList<Feat369Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat369(u)
    }
    return list
}
