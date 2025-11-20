package com.romix.feature.feat185

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat185Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat185UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat185FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat185UserSummary
)

data class Feat185UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat185NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat185Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat185Config = Feat185Config()
) {

    fun loadSnapshot(userId: Long): Feat185NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat185NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat185UserSummary {
        return Feat185UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat185FeedItem> {
        val result = java.util.ArrayList<Feat185FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat185FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat185UiMapper {

    fun mapToUi(model: List<Feat185FeedItem>): Feat185UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat185UiModel(
            header = UiText("Feat185 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat185UiModel =
        Feat185UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat185UiModel =
        Feat185UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat185UiModel =
        Feat185UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat185Service(
    private val repository: Feat185Repository,
    private val uiMapper: Feat185UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat185UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat185UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat185UserItem1(val user: CoreUser, val label: String)
data class Feat185UserItem2(val user: CoreUser, val label: String)
data class Feat185UserItem3(val user: CoreUser, val label: String)
data class Feat185UserItem4(val user: CoreUser, val label: String)
data class Feat185UserItem5(val user: CoreUser, val label: String)
data class Feat185UserItem6(val user: CoreUser, val label: String)
data class Feat185UserItem7(val user: CoreUser, val label: String)
data class Feat185UserItem8(val user: CoreUser, val label: String)
data class Feat185UserItem9(val user: CoreUser, val label: String)
data class Feat185UserItem10(val user: CoreUser, val label: String)

data class Feat185StateBlock1(val state: Feat185UiModel, val checksum: Int)
data class Feat185StateBlock2(val state: Feat185UiModel, val checksum: Int)
data class Feat185StateBlock3(val state: Feat185UiModel, val checksum: Int)
data class Feat185StateBlock4(val state: Feat185UiModel, val checksum: Int)
data class Feat185StateBlock5(val state: Feat185UiModel, val checksum: Int)
data class Feat185StateBlock6(val state: Feat185UiModel, val checksum: Int)
data class Feat185StateBlock7(val state: Feat185UiModel, val checksum: Int)
data class Feat185StateBlock8(val state: Feat185UiModel, val checksum: Int)
data class Feat185StateBlock9(val state: Feat185UiModel, val checksum: Int)
data class Feat185StateBlock10(val state: Feat185UiModel, val checksum: Int)

fun buildFeat185UserItem(user: CoreUser, index: Int): Feat185UserItem1 {
    return Feat185UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat185StateBlock(model: Feat185UiModel): Feat185StateBlock1 {
    return Feat185StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat185UserSummary> {
    val list = java.util.ArrayList<Feat185UserSummary>(users.size)
    for (user in users) {
        list += Feat185UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat185UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat185UiModel {
    val summaries = (0 until count).map {
        Feat185UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat185UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat185UiModel> {
    val models = java.util.ArrayList<Feat185UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat185AnalyticsEvent1(val name: String, val value: String)
data class Feat185AnalyticsEvent2(val name: String, val value: String)
data class Feat185AnalyticsEvent3(val name: String, val value: String)
data class Feat185AnalyticsEvent4(val name: String, val value: String)
data class Feat185AnalyticsEvent5(val name: String, val value: String)
data class Feat185AnalyticsEvent6(val name: String, val value: String)
data class Feat185AnalyticsEvent7(val name: String, val value: String)
data class Feat185AnalyticsEvent8(val name: String, val value: String)
data class Feat185AnalyticsEvent9(val name: String, val value: String)
data class Feat185AnalyticsEvent10(val name: String, val value: String)

fun logFeat185Event1(event: Feat185AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat185Event2(event: Feat185AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat185Event3(event: Feat185AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat185Event4(event: Feat185AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat185Event5(event: Feat185AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat185Event6(event: Feat185AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat185Event7(event: Feat185AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat185Event8(event: Feat185AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat185Event9(event: Feat185AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat185Event10(event: Feat185AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat185Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat185Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat185Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat185Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat185Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat185Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat185Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat185Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat185Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat185Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat185(u: CoreUser): Feat185Projection1 =
    Feat185Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat185Projection1> {
    val list = java.util.ArrayList<Feat185Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat185(u)
    }
    return list
}
