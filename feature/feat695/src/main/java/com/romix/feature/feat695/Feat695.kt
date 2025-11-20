package com.romix.feature.feat695

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat695Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat695UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat695FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat695UserSummary
)

data class Feat695UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat695NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat695Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat695Config = Feat695Config()
) {

    fun loadSnapshot(userId: Long): Feat695NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat695NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat695UserSummary {
        return Feat695UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat695FeedItem> {
        val result = java.util.ArrayList<Feat695FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat695FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat695UiMapper {

    fun mapToUi(model: List<Feat695FeedItem>): Feat695UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat695UiModel(
            header = UiText("Feat695 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat695UiModel =
        Feat695UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat695UiModel =
        Feat695UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat695UiModel =
        Feat695UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat695Service(
    private val repository: Feat695Repository,
    private val uiMapper: Feat695UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat695UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat695UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat695UserItem1(val user: CoreUser, val label: String)
data class Feat695UserItem2(val user: CoreUser, val label: String)
data class Feat695UserItem3(val user: CoreUser, val label: String)
data class Feat695UserItem4(val user: CoreUser, val label: String)
data class Feat695UserItem5(val user: CoreUser, val label: String)
data class Feat695UserItem6(val user: CoreUser, val label: String)
data class Feat695UserItem7(val user: CoreUser, val label: String)
data class Feat695UserItem8(val user: CoreUser, val label: String)
data class Feat695UserItem9(val user: CoreUser, val label: String)
data class Feat695UserItem10(val user: CoreUser, val label: String)

data class Feat695StateBlock1(val state: Feat695UiModel, val checksum: Int)
data class Feat695StateBlock2(val state: Feat695UiModel, val checksum: Int)
data class Feat695StateBlock3(val state: Feat695UiModel, val checksum: Int)
data class Feat695StateBlock4(val state: Feat695UiModel, val checksum: Int)
data class Feat695StateBlock5(val state: Feat695UiModel, val checksum: Int)
data class Feat695StateBlock6(val state: Feat695UiModel, val checksum: Int)
data class Feat695StateBlock7(val state: Feat695UiModel, val checksum: Int)
data class Feat695StateBlock8(val state: Feat695UiModel, val checksum: Int)
data class Feat695StateBlock9(val state: Feat695UiModel, val checksum: Int)
data class Feat695StateBlock10(val state: Feat695UiModel, val checksum: Int)

fun buildFeat695UserItem(user: CoreUser, index: Int): Feat695UserItem1 {
    return Feat695UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat695StateBlock(model: Feat695UiModel): Feat695StateBlock1 {
    return Feat695StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat695UserSummary> {
    val list = java.util.ArrayList<Feat695UserSummary>(users.size)
    for (user in users) {
        list += Feat695UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat695UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat695UiModel {
    val summaries = (0 until count).map {
        Feat695UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat695UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat695UiModel> {
    val models = java.util.ArrayList<Feat695UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat695AnalyticsEvent1(val name: String, val value: String)
data class Feat695AnalyticsEvent2(val name: String, val value: String)
data class Feat695AnalyticsEvent3(val name: String, val value: String)
data class Feat695AnalyticsEvent4(val name: String, val value: String)
data class Feat695AnalyticsEvent5(val name: String, val value: String)
data class Feat695AnalyticsEvent6(val name: String, val value: String)
data class Feat695AnalyticsEvent7(val name: String, val value: String)
data class Feat695AnalyticsEvent8(val name: String, val value: String)
data class Feat695AnalyticsEvent9(val name: String, val value: String)
data class Feat695AnalyticsEvent10(val name: String, val value: String)

fun logFeat695Event1(event: Feat695AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat695Event2(event: Feat695AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat695Event3(event: Feat695AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat695Event4(event: Feat695AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat695Event5(event: Feat695AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat695Event6(event: Feat695AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat695Event7(event: Feat695AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat695Event8(event: Feat695AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat695Event9(event: Feat695AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat695Event10(event: Feat695AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat695Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat695Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat695Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat695Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat695Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat695Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat695Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat695Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat695Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat695Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat695(u: CoreUser): Feat695Projection1 =
    Feat695Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat695Projection1> {
    val list = java.util.ArrayList<Feat695Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat695(u)
    }
    return list
}
