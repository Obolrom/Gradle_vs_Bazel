package com.romix.feature.feat677

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat677Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat677UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat677FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat677UserSummary
)

data class Feat677UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat677NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat677Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat677Config = Feat677Config()
) {

    fun loadSnapshot(userId: Long): Feat677NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat677NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat677UserSummary {
        return Feat677UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat677FeedItem> {
        val result = java.util.ArrayList<Feat677FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat677FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat677UiMapper {

    fun mapToUi(model: List<Feat677FeedItem>): Feat677UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat677UiModel(
            header = UiText("Feat677 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat677UiModel =
        Feat677UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat677UiModel =
        Feat677UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat677UiModel =
        Feat677UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat677Service(
    private val repository: Feat677Repository,
    private val uiMapper: Feat677UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat677UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat677UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat677UserItem1(val user: CoreUser, val label: String)
data class Feat677UserItem2(val user: CoreUser, val label: String)
data class Feat677UserItem3(val user: CoreUser, val label: String)
data class Feat677UserItem4(val user: CoreUser, val label: String)
data class Feat677UserItem5(val user: CoreUser, val label: String)
data class Feat677UserItem6(val user: CoreUser, val label: String)
data class Feat677UserItem7(val user: CoreUser, val label: String)
data class Feat677UserItem8(val user: CoreUser, val label: String)
data class Feat677UserItem9(val user: CoreUser, val label: String)
data class Feat677UserItem10(val user: CoreUser, val label: String)

data class Feat677StateBlock1(val state: Feat677UiModel, val checksum: Int)
data class Feat677StateBlock2(val state: Feat677UiModel, val checksum: Int)
data class Feat677StateBlock3(val state: Feat677UiModel, val checksum: Int)
data class Feat677StateBlock4(val state: Feat677UiModel, val checksum: Int)
data class Feat677StateBlock5(val state: Feat677UiModel, val checksum: Int)
data class Feat677StateBlock6(val state: Feat677UiModel, val checksum: Int)
data class Feat677StateBlock7(val state: Feat677UiModel, val checksum: Int)
data class Feat677StateBlock8(val state: Feat677UiModel, val checksum: Int)
data class Feat677StateBlock9(val state: Feat677UiModel, val checksum: Int)
data class Feat677StateBlock10(val state: Feat677UiModel, val checksum: Int)

fun buildFeat677UserItem(user: CoreUser, index: Int): Feat677UserItem1 {
    return Feat677UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat677StateBlock(model: Feat677UiModel): Feat677StateBlock1 {
    return Feat677StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat677UserSummary> {
    val list = java.util.ArrayList<Feat677UserSummary>(users.size)
    for (user in users) {
        list += Feat677UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat677UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat677UiModel {
    val summaries = (0 until count).map {
        Feat677UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat677UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat677UiModel> {
    val models = java.util.ArrayList<Feat677UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat677AnalyticsEvent1(val name: String, val value: String)
data class Feat677AnalyticsEvent2(val name: String, val value: String)
data class Feat677AnalyticsEvent3(val name: String, val value: String)
data class Feat677AnalyticsEvent4(val name: String, val value: String)
data class Feat677AnalyticsEvent5(val name: String, val value: String)
data class Feat677AnalyticsEvent6(val name: String, val value: String)
data class Feat677AnalyticsEvent7(val name: String, val value: String)
data class Feat677AnalyticsEvent8(val name: String, val value: String)
data class Feat677AnalyticsEvent9(val name: String, val value: String)
data class Feat677AnalyticsEvent10(val name: String, val value: String)

fun logFeat677Event1(event: Feat677AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat677Event2(event: Feat677AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat677Event3(event: Feat677AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat677Event4(event: Feat677AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat677Event5(event: Feat677AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat677Event6(event: Feat677AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat677Event7(event: Feat677AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat677Event8(event: Feat677AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat677Event9(event: Feat677AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat677Event10(event: Feat677AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat677Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat677Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat677Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat677Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat677Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat677Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat677Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat677Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat677Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat677Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat677(u: CoreUser): Feat677Projection1 =
    Feat677Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat677Projection1> {
    val list = java.util.ArrayList<Feat677Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat677(u)
    }
    return list
}
