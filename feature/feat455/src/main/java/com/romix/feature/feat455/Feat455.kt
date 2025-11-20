package com.romix.feature.feat455

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat455Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat455UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat455FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat455UserSummary
)

data class Feat455UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat455NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat455Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat455Config = Feat455Config()
) {

    fun loadSnapshot(userId: Long): Feat455NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat455NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat455UserSummary {
        return Feat455UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat455FeedItem> {
        val result = java.util.ArrayList<Feat455FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat455FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat455UiMapper {

    fun mapToUi(model: List<Feat455FeedItem>): Feat455UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat455UiModel(
            header = UiText("Feat455 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat455UiModel =
        Feat455UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat455UiModel =
        Feat455UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat455UiModel =
        Feat455UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat455Service(
    private val repository: Feat455Repository,
    private val uiMapper: Feat455UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat455UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat455UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat455UserItem1(val user: CoreUser, val label: String)
data class Feat455UserItem2(val user: CoreUser, val label: String)
data class Feat455UserItem3(val user: CoreUser, val label: String)
data class Feat455UserItem4(val user: CoreUser, val label: String)
data class Feat455UserItem5(val user: CoreUser, val label: String)
data class Feat455UserItem6(val user: CoreUser, val label: String)
data class Feat455UserItem7(val user: CoreUser, val label: String)
data class Feat455UserItem8(val user: CoreUser, val label: String)
data class Feat455UserItem9(val user: CoreUser, val label: String)
data class Feat455UserItem10(val user: CoreUser, val label: String)

data class Feat455StateBlock1(val state: Feat455UiModel, val checksum: Int)
data class Feat455StateBlock2(val state: Feat455UiModel, val checksum: Int)
data class Feat455StateBlock3(val state: Feat455UiModel, val checksum: Int)
data class Feat455StateBlock4(val state: Feat455UiModel, val checksum: Int)
data class Feat455StateBlock5(val state: Feat455UiModel, val checksum: Int)
data class Feat455StateBlock6(val state: Feat455UiModel, val checksum: Int)
data class Feat455StateBlock7(val state: Feat455UiModel, val checksum: Int)
data class Feat455StateBlock8(val state: Feat455UiModel, val checksum: Int)
data class Feat455StateBlock9(val state: Feat455UiModel, val checksum: Int)
data class Feat455StateBlock10(val state: Feat455UiModel, val checksum: Int)

fun buildFeat455UserItem(user: CoreUser, index: Int): Feat455UserItem1 {
    return Feat455UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat455StateBlock(model: Feat455UiModel): Feat455StateBlock1 {
    return Feat455StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat455UserSummary> {
    val list = java.util.ArrayList<Feat455UserSummary>(users.size)
    for (user in users) {
        list += Feat455UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat455UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat455UiModel {
    val summaries = (0 until count).map {
        Feat455UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat455UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat455UiModel> {
    val models = java.util.ArrayList<Feat455UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat455AnalyticsEvent1(val name: String, val value: String)
data class Feat455AnalyticsEvent2(val name: String, val value: String)
data class Feat455AnalyticsEvent3(val name: String, val value: String)
data class Feat455AnalyticsEvent4(val name: String, val value: String)
data class Feat455AnalyticsEvent5(val name: String, val value: String)
data class Feat455AnalyticsEvent6(val name: String, val value: String)
data class Feat455AnalyticsEvent7(val name: String, val value: String)
data class Feat455AnalyticsEvent8(val name: String, val value: String)
data class Feat455AnalyticsEvent9(val name: String, val value: String)
data class Feat455AnalyticsEvent10(val name: String, val value: String)

fun logFeat455Event1(event: Feat455AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat455Event2(event: Feat455AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat455Event3(event: Feat455AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat455Event4(event: Feat455AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat455Event5(event: Feat455AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat455Event6(event: Feat455AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat455Event7(event: Feat455AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat455Event8(event: Feat455AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat455Event9(event: Feat455AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat455Event10(event: Feat455AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat455Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat455Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat455Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat455Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat455Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat455Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat455Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat455Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat455Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat455Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat455(u: CoreUser): Feat455Projection1 =
    Feat455Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat455Projection1> {
    val list = java.util.ArrayList<Feat455Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat455(u)
    }
    return list
}
