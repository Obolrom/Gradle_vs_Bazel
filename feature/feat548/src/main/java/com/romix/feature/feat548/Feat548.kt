package com.romix.feature.feat548

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat548Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat548UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat548FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat548UserSummary
)

data class Feat548UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat548NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat548Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat548Config = Feat548Config()
) {

    fun loadSnapshot(userId: Long): Feat548NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat548NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat548UserSummary {
        return Feat548UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat548FeedItem> {
        val result = java.util.ArrayList<Feat548FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat548FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat548UiMapper {

    fun mapToUi(model: List<Feat548FeedItem>): Feat548UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat548UiModel(
            header = UiText("Feat548 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat548UiModel =
        Feat548UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat548UiModel =
        Feat548UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat548UiModel =
        Feat548UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat548Service(
    private val repository: Feat548Repository,
    private val uiMapper: Feat548UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat548UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat548UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat548UserItem1(val user: CoreUser, val label: String)
data class Feat548UserItem2(val user: CoreUser, val label: String)
data class Feat548UserItem3(val user: CoreUser, val label: String)
data class Feat548UserItem4(val user: CoreUser, val label: String)
data class Feat548UserItem5(val user: CoreUser, val label: String)
data class Feat548UserItem6(val user: CoreUser, val label: String)
data class Feat548UserItem7(val user: CoreUser, val label: String)
data class Feat548UserItem8(val user: CoreUser, val label: String)
data class Feat548UserItem9(val user: CoreUser, val label: String)
data class Feat548UserItem10(val user: CoreUser, val label: String)

data class Feat548StateBlock1(val state: Feat548UiModel, val checksum: Int)
data class Feat548StateBlock2(val state: Feat548UiModel, val checksum: Int)
data class Feat548StateBlock3(val state: Feat548UiModel, val checksum: Int)
data class Feat548StateBlock4(val state: Feat548UiModel, val checksum: Int)
data class Feat548StateBlock5(val state: Feat548UiModel, val checksum: Int)
data class Feat548StateBlock6(val state: Feat548UiModel, val checksum: Int)
data class Feat548StateBlock7(val state: Feat548UiModel, val checksum: Int)
data class Feat548StateBlock8(val state: Feat548UiModel, val checksum: Int)
data class Feat548StateBlock9(val state: Feat548UiModel, val checksum: Int)
data class Feat548StateBlock10(val state: Feat548UiModel, val checksum: Int)

fun buildFeat548UserItem(user: CoreUser, index: Int): Feat548UserItem1 {
    return Feat548UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat548StateBlock(model: Feat548UiModel): Feat548StateBlock1 {
    return Feat548StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat548UserSummary> {
    val list = java.util.ArrayList<Feat548UserSummary>(users.size)
    for (user in users) {
        list += Feat548UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat548UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat548UiModel {
    val summaries = (0 until count).map {
        Feat548UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat548UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat548UiModel> {
    val models = java.util.ArrayList<Feat548UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat548AnalyticsEvent1(val name: String, val value: String)
data class Feat548AnalyticsEvent2(val name: String, val value: String)
data class Feat548AnalyticsEvent3(val name: String, val value: String)
data class Feat548AnalyticsEvent4(val name: String, val value: String)
data class Feat548AnalyticsEvent5(val name: String, val value: String)
data class Feat548AnalyticsEvent6(val name: String, val value: String)
data class Feat548AnalyticsEvent7(val name: String, val value: String)
data class Feat548AnalyticsEvent8(val name: String, val value: String)
data class Feat548AnalyticsEvent9(val name: String, val value: String)
data class Feat548AnalyticsEvent10(val name: String, val value: String)

fun logFeat548Event1(event: Feat548AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat548Event2(event: Feat548AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat548Event3(event: Feat548AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat548Event4(event: Feat548AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat548Event5(event: Feat548AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat548Event6(event: Feat548AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat548Event7(event: Feat548AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat548Event8(event: Feat548AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat548Event9(event: Feat548AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat548Event10(event: Feat548AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat548Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat548Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat548Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat548Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat548Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat548Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat548Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat548Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat548Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat548Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat548(u: CoreUser): Feat548Projection1 =
    Feat548Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat548Projection1> {
    val list = java.util.ArrayList<Feat548Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat548(u)
    }
    return list
}
