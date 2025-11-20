package com.romix.feature.feat485

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat485Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat485UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat485FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat485UserSummary
)

data class Feat485UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat485NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat485Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat485Config = Feat485Config()
) {

    fun loadSnapshot(userId: Long): Feat485NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat485NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat485UserSummary {
        return Feat485UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat485FeedItem> {
        val result = java.util.ArrayList<Feat485FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat485FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat485UiMapper {

    fun mapToUi(model: List<Feat485FeedItem>): Feat485UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat485UiModel(
            header = UiText("Feat485 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat485UiModel =
        Feat485UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat485UiModel =
        Feat485UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat485UiModel =
        Feat485UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat485Service(
    private val repository: Feat485Repository,
    private val uiMapper: Feat485UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat485UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat485UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat485UserItem1(val user: CoreUser, val label: String)
data class Feat485UserItem2(val user: CoreUser, val label: String)
data class Feat485UserItem3(val user: CoreUser, val label: String)
data class Feat485UserItem4(val user: CoreUser, val label: String)
data class Feat485UserItem5(val user: CoreUser, val label: String)
data class Feat485UserItem6(val user: CoreUser, val label: String)
data class Feat485UserItem7(val user: CoreUser, val label: String)
data class Feat485UserItem8(val user: CoreUser, val label: String)
data class Feat485UserItem9(val user: CoreUser, val label: String)
data class Feat485UserItem10(val user: CoreUser, val label: String)

data class Feat485StateBlock1(val state: Feat485UiModel, val checksum: Int)
data class Feat485StateBlock2(val state: Feat485UiModel, val checksum: Int)
data class Feat485StateBlock3(val state: Feat485UiModel, val checksum: Int)
data class Feat485StateBlock4(val state: Feat485UiModel, val checksum: Int)
data class Feat485StateBlock5(val state: Feat485UiModel, val checksum: Int)
data class Feat485StateBlock6(val state: Feat485UiModel, val checksum: Int)
data class Feat485StateBlock7(val state: Feat485UiModel, val checksum: Int)
data class Feat485StateBlock8(val state: Feat485UiModel, val checksum: Int)
data class Feat485StateBlock9(val state: Feat485UiModel, val checksum: Int)
data class Feat485StateBlock10(val state: Feat485UiModel, val checksum: Int)

fun buildFeat485UserItem(user: CoreUser, index: Int): Feat485UserItem1 {
    return Feat485UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat485StateBlock(model: Feat485UiModel): Feat485StateBlock1 {
    return Feat485StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat485UserSummary> {
    val list = java.util.ArrayList<Feat485UserSummary>(users.size)
    for (user in users) {
        list += Feat485UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat485UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat485UiModel {
    val summaries = (0 until count).map {
        Feat485UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat485UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat485UiModel> {
    val models = java.util.ArrayList<Feat485UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat485AnalyticsEvent1(val name: String, val value: String)
data class Feat485AnalyticsEvent2(val name: String, val value: String)
data class Feat485AnalyticsEvent3(val name: String, val value: String)
data class Feat485AnalyticsEvent4(val name: String, val value: String)
data class Feat485AnalyticsEvent5(val name: String, val value: String)
data class Feat485AnalyticsEvent6(val name: String, val value: String)
data class Feat485AnalyticsEvent7(val name: String, val value: String)
data class Feat485AnalyticsEvent8(val name: String, val value: String)
data class Feat485AnalyticsEvent9(val name: String, val value: String)
data class Feat485AnalyticsEvent10(val name: String, val value: String)

fun logFeat485Event1(event: Feat485AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat485Event2(event: Feat485AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat485Event3(event: Feat485AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat485Event4(event: Feat485AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat485Event5(event: Feat485AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat485Event6(event: Feat485AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat485Event7(event: Feat485AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat485Event8(event: Feat485AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat485Event9(event: Feat485AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat485Event10(event: Feat485AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat485Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat485Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat485Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat485Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat485Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat485Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat485Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat485Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat485Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat485Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat485(u: CoreUser): Feat485Projection1 =
    Feat485Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat485Projection1> {
    val list = java.util.ArrayList<Feat485Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat485(u)
    }
    return list
}
