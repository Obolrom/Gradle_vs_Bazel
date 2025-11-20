package com.romix.feature.feat104

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat104Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat104UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat104FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat104UserSummary
)

data class Feat104UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat104NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat104Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat104Config = Feat104Config()
) {

    fun loadSnapshot(userId: Long): Feat104NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat104NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat104UserSummary {
        return Feat104UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat104FeedItem> {
        val result = java.util.ArrayList<Feat104FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat104FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat104UiMapper {

    fun mapToUi(model: List<Feat104FeedItem>): Feat104UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat104UiModel(
            header = UiText("Feat104 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat104UiModel =
        Feat104UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat104UiModel =
        Feat104UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat104UiModel =
        Feat104UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat104Service(
    private val repository: Feat104Repository,
    private val uiMapper: Feat104UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat104UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat104UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat104UserItem1(val user: CoreUser, val label: String)
data class Feat104UserItem2(val user: CoreUser, val label: String)
data class Feat104UserItem3(val user: CoreUser, val label: String)
data class Feat104UserItem4(val user: CoreUser, val label: String)
data class Feat104UserItem5(val user: CoreUser, val label: String)
data class Feat104UserItem6(val user: CoreUser, val label: String)
data class Feat104UserItem7(val user: CoreUser, val label: String)
data class Feat104UserItem8(val user: CoreUser, val label: String)
data class Feat104UserItem9(val user: CoreUser, val label: String)
data class Feat104UserItem10(val user: CoreUser, val label: String)

data class Feat104StateBlock1(val state: Feat104UiModel, val checksum: Int)
data class Feat104StateBlock2(val state: Feat104UiModel, val checksum: Int)
data class Feat104StateBlock3(val state: Feat104UiModel, val checksum: Int)
data class Feat104StateBlock4(val state: Feat104UiModel, val checksum: Int)
data class Feat104StateBlock5(val state: Feat104UiModel, val checksum: Int)
data class Feat104StateBlock6(val state: Feat104UiModel, val checksum: Int)
data class Feat104StateBlock7(val state: Feat104UiModel, val checksum: Int)
data class Feat104StateBlock8(val state: Feat104UiModel, val checksum: Int)
data class Feat104StateBlock9(val state: Feat104UiModel, val checksum: Int)
data class Feat104StateBlock10(val state: Feat104UiModel, val checksum: Int)

fun buildFeat104UserItem(user: CoreUser, index: Int): Feat104UserItem1 {
    return Feat104UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat104StateBlock(model: Feat104UiModel): Feat104StateBlock1 {
    return Feat104StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat104UserSummary> {
    val list = java.util.ArrayList<Feat104UserSummary>(users.size)
    for (user in users) {
        list += Feat104UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat104UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat104UiModel {
    val summaries = (0 until count).map {
        Feat104UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat104UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat104UiModel> {
    val models = java.util.ArrayList<Feat104UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat104AnalyticsEvent1(val name: String, val value: String)
data class Feat104AnalyticsEvent2(val name: String, val value: String)
data class Feat104AnalyticsEvent3(val name: String, val value: String)
data class Feat104AnalyticsEvent4(val name: String, val value: String)
data class Feat104AnalyticsEvent5(val name: String, val value: String)
data class Feat104AnalyticsEvent6(val name: String, val value: String)
data class Feat104AnalyticsEvent7(val name: String, val value: String)
data class Feat104AnalyticsEvent8(val name: String, val value: String)
data class Feat104AnalyticsEvent9(val name: String, val value: String)
data class Feat104AnalyticsEvent10(val name: String, val value: String)

fun logFeat104Event1(event: Feat104AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat104Event2(event: Feat104AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat104Event3(event: Feat104AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat104Event4(event: Feat104AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat104Event5(event: Feat104AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat104Event6(event: Feat104AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat104Event7(event: Feat104AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat104Event8(event: Feat104AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat104Event9(event: Feat104AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat104Event10(event: Feat104AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat104Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat104Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat104Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat104Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat104Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat104Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat104Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat104Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat104Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat104Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat104(u: CoreUser): Feat104Projection1 =
    Feat104Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat104Projection1> {
    val list = java.util.ArrayList<Feat104Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat104(u)
    }
    return list
}
