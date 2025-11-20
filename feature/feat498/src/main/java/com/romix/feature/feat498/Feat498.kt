package com.romix.feature.feat498

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat498Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat498UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat498FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat498UserSummary
)

data class Feat498UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat498NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat498Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat498Config = Feat498Config()
) {

    fun loadSnapshot(userId: Long): Feat498NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat498NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat498UserSummary {
        return Feat498UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat498FeedItem> {
        val result = java.util.ArrayList<Feat498FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat498FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat498UiMapper {

    fun mapToUi(model: List<Feat498FeedItem>): Feat498UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat498UiModel(
            header = UiText("Feat498 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat498UiModel =
        Feat498UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat498UiModel =
        Feat498UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat498UiModel =
        Feat498UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat498Service(
    private val repository: Feat498Repository,
    private val uiMapper: Feat498UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat498UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat498UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat498UserItem1(val user: CoreUser, val label: String)
data class Feat498UserItem2(val user: CoreUser, val label: String)
data class Feat498UserItem3(val user: CoreUser, val label: String)
data class Feat498UserItem4(val user: CoreUser, val label: String)
data class Feat498UserItem5(val user: CoreUser, val label: String)
data class Feat498UserItem6(val user: CoreUser, val label: String)
data class Feat498UserItem7(val user: CoreUser, val label: String)
data class Feat498UserItem8(val user: CoreUser, val label: String)
data class Feat498UserItem9(val user: CoreUser, val label: String)
data class Feat498UserItem10(val user: CoreUser, val label: String)

data class Feat498StateBlock1(val state: Feat498UiModel, val checksum: Int)
data class Feat498StateBlock2(val state: Feat498UiModel, val checksum: Int)
data class Feat498StateBlock3(val state: Feat498UiModel, val checksum: Int)
data class Feat498StateBlock4(val state: Feat498UiModel, val checksum: Int)
data class Feat498StateBlock5(val state: Feat498UiModel, val checksum: Int)
data class Feat498StateBlock6(val state: Feat498UiModel, val checksum: Int)
data class Feat498StateBlock7(val state: Feat498UiModel, val checksum: Int)
data class Feat498StateBlock8(val state: Feat498UiModel, val checksum: Int)
data class Feat498StateBlock9(val state: Feat498UiModel, val checksum: Int)
data class Feat498StateBlock10(val state: Feat498UiModel, val checksum: Int)

fun buildFeat498UserItem(user: CoreUser, index: Int): Feat498UserItem1 {
    return Feat498UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat498StateBlock(model: Feat498UiModel): Feat498StateBlock1 {
    return Feat498StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat498UserSummary> {
    val list = java.util.ArrayList<Feat498UserSummary>(users.size)
    for (user in users) {
        list += Feat498UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat498UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat498UiModel {
    val summaries = (0 until count).map {
        Feat498UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat498UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat498UiModel> {
    val models = java.util.ArrayList<Feat498UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat498AnalyticsEvent1(val name: String, val value: String)
data class Feat498AnalyticsEvent2(val name: String, val value: String)
data class Feat498AnalyticsEvent3(val name: String, val value: String)
data class Feat498AnalyticsEvent4(val name: String, val value: String)
data class Feat498AnalyticsEvent5(val name: String, val value: String)
data class Feat498AnalyticsEvent6(val name: String, val value: String)
data class Feat498AnalyticsEvent7(val name: String, val value: String)
data class Feat498AnalyticsEvent8(val name: String, val value: String)
data class Feat498AnalyticsEvent9(val name: String, val value: String)
data class Feat498AnalyticsEvent10(val name: String, val value: String)

fun logFeat498Event1(event: Feat498AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat498Event2(event: Feat498AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat498Event3(event: Feat498AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat498Event4(event: Feat498AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat498Event5(event: Feat498AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat498Event6(event: Feat498AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat498Event7(event: Feat498AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat498Event8(event: Feat498AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat498Event9(event: Feat498AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat498Event10(event: Feat498AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat498Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat498Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat498Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat498Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat498Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat498Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat498Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat498Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat498Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat498Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat498(u: CoreUser): Feat498Projection1 =
    Feat498Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat498Projection1> {
    val list = java.util.ArrayList<Feat498Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat498(u)
    }
    return list
}
