package com.romix.feature.feat514

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat514Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat514UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat514FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat514UserSummary
)

data class Feat514UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat514NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat514Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat514Config = Feat514Config()
) {

    fun loadSnapshot(userId: Long): Feat514NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat514NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat514UserSummary {
        return Feat514UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat514FeedItem> {
        val result = java.util.ArrayList<Feat514FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat514FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat514UiMapper {

    fun mapToUi(model: List<Feat514FeedItem>): Feat514UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat514UiModel(
            header = UiText("Feat514 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat514UiModel =
        Feat514UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat514UiModel =
        Feat514UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat514UiModel =
        Feat514UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat514Service(
    private val repository: Feat514Repository,
    private val uiMapper: Feat514UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat514UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat514UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat514UserItem1(val user: CoreUser, val label: String)
data class Feat514UserItem2(val user: CoreUser, val label: String)
data class Feat514UserItem3(val user: CoreUser, val label: String)
data class Feat514UserItem4(val user: CoreUser, val label: String)
data class Feat514UserItem5(val user: CoreUser, val label: String)
data class Feat514UserItem6(val user: CoreUser, val label: String)
data class Feat514UserItem7(val user: CoreUser, val label: String)
data class Feat514UserItem8(val user: CoreUser, val label: String)
data class Feat514UserItem9(val user: CoreUser, val label: String)
data class Feat514UserItem10(val user: CoreUser, val label: String)

data class Feat514StateBlock1(val state: Feat514UiModel, val checksum: Int)
data class Feat514StateBlock2(val state: Feat514UiModel, val checksum: Int)
data class Feat514StateBlock3(val state: Feat514UiModel, val checksum: Int)
data class Feat514StateBlock4(val state: Feat514UiModel, val checksum: Int)
data class Feat514StateBlock5(val state: Feat514UiModel, val checksum: Int)
data class Feat514StateBlock6(val state: Feat514UiModel, val checksum: Int)
data class Feat514StateBlock7(val state: Feat514UiModel, val checksum: Int)
data class Feat514StateBlock8(val state: Feat514UiModel, val checksum: Int)
data class Feat514StateBlock9(val state: Feat514UiModel, val checksum: Int)
data class Feat514StateBlock10(val state: Feat514UiModel, val checksum: Int)

fun buildFeat514UserItem(user: CoreUser, index: Int): Feat514UserItem1 {
    return Feat514UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat514StateBlock(model: Feat514UiModel): Feat514StateBlock1 {
    return Feat514StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat514UserSummary> {
    val list = java.util.ArrayList<Feat514UserSummary>(users.size)
    for (user in users) {
        list += Feat514UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat514UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat514UiModel {
    val summaries = (0 until count).map {
        Feat514UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat514UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat514UiModel> {
    val models = java.util.ArrayList<Feat514UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat514AnalyticsEvent1(val name: String, val value: String)
data class Feat514AnalyticsEvent2(val name: String, val value: String)
data class Feat514AnalyticsEvent3(val name: String, val value: String)
data class Feat514AnalyticsEvent4(val name: String, val value: String)
data class Feat514AnalyticsEvent5(val name: String, val value: String)
data class Feat514AnalyticsEvent6(val name: String, val value: String)
data class Feat514AnalyticsEvent7(val name: String, val value: String)
data class Feat514AnalyticsEvent8(val name: String, val value: String)
data class Feat514AnalyticsEvent9(val name: String, val value: String)
data class Feat514AnalyticsEvent10(val name: String, val value: String)

fun logFeat514Event1(event: Feat514AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat514Event2(event: Feat514AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat514Event3(event: Feat514AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat514Event4(event: Feat514AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat514Event5(event: Feat514AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat514Event6(event: Feat514AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat514Event7(event: Feat514AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat514Event8(event: Feat514AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat514Event9(event: Feat514AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat514Event10(event: Feat514AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat514Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat514Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat514Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat514Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat514Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat514Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat514Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat514Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat514Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat514Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat514(u: CoreUser): Feat514Projection1 =
    Feat514Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat514Projection1> {
    val list = java.util.ArrayList<Feat514Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat514(u)
    }
    return list
}
