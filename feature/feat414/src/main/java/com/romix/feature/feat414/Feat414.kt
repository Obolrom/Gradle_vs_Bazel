package com.romix.feature.feat414

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat414Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat414UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat414FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat414UserSummary
)

data class Feat414UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat414NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat414Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat414Config = Feat414Config()
) {

    fun loadSnapshot(userId: Long): Feat414NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat414NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat414UserSummary {
        return Feat414UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat414FeedItem> {
        val result = java.util.ArrayList<Feat414FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat414FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat414UiMapper {

    fun mapToUi(model: List<Feat414FeedItem>): Feat414UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat414UiModel(
            header = UiText("Feat414 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat414UiModel =
        Feat414UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat414UiModel =
        Feat414UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat414UiModel =
        Feat414UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat414Service(
    private val repository: Feat414Repository,
    private val uiMapper: Feat414UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat414UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat414UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat414UserItem1(val user: CoreUser, val label: String)
data class Feat414UserItem2(val user: CoreUser, val label: String)
data class Feat414UserItem3(val user: CoreUser, val label: String)
data class Feat414UserItem4(val user: CoreUser, val label: String)
data class Feat414UserItem5(val user: CoreUser, val label: String)
data class Feat414UserItem6(val user: CoreUser, val label: String)
data class Feat414UserItem7(val user: CoreUser, val label: String)
data class Feat414UserItem8(val user: CoreUser, val label: String)
data class Feat414UserItem9(val user: CoreUser, val label: String)
data class Feat414UserItem10(val user: CoreUser, val label: String)

data class Feat414StateBlock1(val state: Feat414UiModel, val checksum: Int)
data class Feat414StateBlock2(val state: Feat414UiModel, val checksum: Int)
data class Feat414StateBlock3(val state: Feat414UiModel, val checksum: Int)
data class Feat414StateBlock4(val state: Feat414UiModel, val checksum: Int)
data class Feat414StateBlock5(val state: Feat414UiModel, val checksum: Int)
data class Feat414StateBlock6(val state: Feat414UiModel, val checksum: Int)
data class Feat414StateBlock7(val state: Feat414UiModel, val checksum: Int)
data class Feat414StateBlock8(val state: Feat414UiModel, val checksum: Int)
data class Feat414StateBlock9(val state: Feat414UiModel, val checksum: Int)
data class Feat414StateBlock10(val state: Feat414UiModel, val checksum: Int)

fun buildFeat414UserItem(user: CoreUser, index: Int): Feat414UserItem1 {
    return Feat414UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat414StateBlock(model: Feat414UiModel): Feat414StateBlock1 {
    return Feat414StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat414UserSummary> {
    val list = java.util.ArrayList<Feat414UserSummary>(users.size)
    for (user in users) {
        list += Feat414UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat414UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat414UiModel {
    val summaries = (0 until count).map {
        Feat414UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat414UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat414UiModel> {
    val models = java.util.ArrayList<Feat414UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat414AnalyticsEvent1(val name: String, val value: String)
data class Feat414AnalyticsEvent2(val name: String, val value: String)
data class Feat414AnalyticsEvent3(val name: String, val value: String)
data class Feat414AnalyticsEvent4(val name: String, val value: String)
data class Feat414AnalyticsEvent5(val name: String, val value: String)
data class Feat414AnalyticsEvent6(val name: String, val value: String)
data class Feat414AnalyticsEvent7(val name: String, val value: String)
data class Feat414AnalyticsEvent8(val name: String, val value: String)
data class Feat414AnalyticsEvent9(val name: String, val value: String)
data class Feat414AnalyticsEvent10(val name: String, val value: String)

fun logFeat414Event1(event: Feat414AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat414Event2(event: Feat414AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat414Event3(event: Feat414AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat414Event4(event: Feat414AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat414Event5(event: Feat414AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat414Event6(event: Feat414AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat414Event7(event: Feat414AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat414Event8(event: Feat414AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat414Event9(event: Feat414AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat414Event10(event: Feat414AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat414Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat414Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat414Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat414Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat414Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat414Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat414Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat414Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat414Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat414Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat414(u: CoreUser): Feat414Projection1 =
    Feat414Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat414Projection1> {
    val list = java.util.ArrayList<Feat414Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat414(u)
    }
    return list
}
