package com.romix.feature.feat90

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat90Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat90UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat90FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat90UserSummary
)

data class Feat90UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat90NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat90Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat90Config = Feat90Config()
) {

    fun loadSnapshot(userId: Long): Feat90NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat90NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat90UserSummary {
        return Feat90UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat90FeedItem> {
        val result = java.util.ArrayList<Feat90FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat90FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat90UiMapper {

    fun mapToUi(model: List<Feat90FeedItem>): Feat90UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat90UiModel(
            header = UiText("Feat90 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat90UiModel =
        Feat90UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat90UiModel =
        Feat90UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat90UiModel =
        Feat90UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat90Service(
    private val repository: Feat90Repository,
    private val uiMapper: Feat90UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat90UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat90UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat90UserItem1(val user: CoreUser, val label: String)
data class Feat90UserItem2(val user: CoreUser, val label: String)
data class Feat90UserItem3(val user: CoreUser, val label: String)
data class Feat90UserItem4(val user: CoreUser, val label: String)
data class Feat90UserItem5(val user: CoreUser, val label: String)
data class Feat90UserItem6(val user: CoreUser, val label: String)
data class Feat90UserItem7(val user: CoreUser, val label: String)
data class Feat90UserItem8(val user: CoreUser, val label: String)
data class Feat90UserItem9(val user: CoreUser, val label: String)
data class Feat90UserItem10(val user: CoreUser, val label: String)

data class Feat90StateBlock1(val state: Feat90UiModel, val checksum: Int)
data class Feat90StateBlock2(val state: Feat90UiModel, val checksum: Int)
data class Feat90StateBlock3(val state: Feat90UiModel, val checksum: Int)
data class Feat90StateBlock4(val state: Feat90UiModel, val checksum: Int)
data class Feat90StateBlock5(val state: Feat90UiModel, val checksum: Int)
data class Feat90StateBlock6(val state: Feat90UiModel, val checksum: Int)
data class Feat90StateBlock7(val state: Feat90UiModel, val checksum: Int)
data class Feat90StateBlock8(val state: Feat90UiModel, val checksum: Int)
data class Feat90StateBlock9(val state: Feat90UiModel, val checksum: Int)
data class Feat90StateBlock10(val state: Feat90UiModel, val checksum: Int)

fun buildFeat90UserItem(user: CoreUser, index: Int): Feat90UserItem1 {
    return Feat90UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat90StateBlock(model: Feat90UiModel): Feat90StateBlock1 {
    return Feat90StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat90UserSummary> {
    val list = java.util.ArrayList<Feat90UserSummary>(users.size)
    for (user in users) {
        list += Feat90UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat90UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat90UiModel {
    val summaries = (0 until count).map {
        Feat90UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat90UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat90UiModel> {
    val models = java.util.ArrayList<Feat90UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat90AnalyticsEvent1(val name: String, val value: String)
data class Feat90AnalyticsEvent2(val name: String, val value: String)
data class Feat90AnalyticsEvent3(val name: String, val value: String)
data class Feat90AnalyticsEvent4(val name: String, val value: String)
data class Feat90AnalyticsEvent5(val name: String, val value: String)
data class Feat90AnalyticsEvent6(val name: String, val value: String)
data class Feat90AnalyticsEvent7(val name: String, val value: String)
data class Feat90AnalyticsEvent8(val name: String, val value: String)
data class Feat90AnalyticsEvent9(val name: String, val value: String)
data class Feat90AnalyticsEvent10(val name: String, val value: String)

fun logFeat90Event1(event: Feat90AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat90Event2(event: Feat90AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat90Event3(event: Feat90AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat90Event4(event: Feat90AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat90Event5(event: Feat90AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat90Event6(event: Feat90AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat90Event7(event: Feat90AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat90Event8(event: Feat90AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat90Event9(event: Feat90AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat90Event10(event: Feat90AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat90Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat90Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat90Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat90Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat90Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat90Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat90Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat90Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat90Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat90Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat90(u: CoreUser): Feat90Projection1 =
    Feat90Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat90Projection1> {
    val list = java.util.ArrayList<Feat90Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat90(u)
    }
    return list
}
