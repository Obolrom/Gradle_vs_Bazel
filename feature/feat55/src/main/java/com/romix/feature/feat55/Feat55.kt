package com.romix.feature.feat55

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat55Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat55UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat55FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat55UserSummary
)

data class Feat55UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat55NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat55Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat55Config = Feat55Config()
) {

    fun loadSnapshot(userId: Long): Feat55NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat55NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat55UserSummary {
        return Feat55UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat55FeedItem> {
        val result = java.util.ArrayList<Feat55FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat55FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat55UiMapper {

    fun mapToUi(model: List<Feat55FeedItem>): Feat55UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat55UiModel(
            header = UiText("Feat55 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat55UiModel =
        Feat55UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat55UiModel =
        Feat55UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat55UiModel =
        Feat55UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat55Service(
    private val repository: Feat55Repository,
    private val uiMapper: Feat55UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat55UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat55UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat55UserItem1(val user: CoreUser, val label: String)
data class Feat55UserItem2(val user: CoreUser, val label: String)
data class Feat55UserItem3(val user: CoreUser, val label: String)
data class Feat55UserItem4(val user: CoreUser, val label: String)
data class Feat55UserItem5(val user: CoreUser, val label: String)
data class Feat55UserItem6(val user: CoreUser, val label: String)
data class Feat55UserItem7(val user: CoreUser, val label: String)
data class Feat55UserItem8(val user: CoreUser, val label: String)
data class Feat55UserItem9(val user: CoreUser, val label: String)
data class Feat55UserItem10(val user: CoreUser, val label: String)

data class Feat55StateBlock1(val state: Feat55UiModel, val checksum: Int)
data class Feat55StateBlock2(val state: Feat55UiModel, val checksum: Int)
data class Feat55StateBlock3(val state: Feat55UiModel, val checksum: Int)
data class Feat55StateBlock4(val state: Feat55UiModel, val checksum: Int)
data class Feat55StateBlock5(val state: Feat55UiModel, val checksum: Int)
data class Feat55StateBlock6(val state: Feat55UiModel, val checksum: Int)
data class Feat55StateBlock7(val state: Feat55UiModel, val checksum: Int)
data class Feat55StateBlock8(val state: Feat55UiModel, val checksum: Int)
data class Feat55StateBlock9(val state: Feat55UiModel, val checksum: Int)
data class Feat55StateBlock10(val state: Feat55UiModel, val checksum: Int)

fun buildFeat55UserItem(user: CoreUser, index: Int): Feat55UserItem1 {
    return Feat55UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat55StateBlock(model: Feat55UiModel): Feat55StateBlock1 {
    return Feat55StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat55UserSummary> {
    val list = java.util.ArrayList<Feat55UserSummary>(users.size)
    for (user in users) {
        list += Feat55UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat55UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat55UiModel {
    val summaries = (0 until count).map {
        Feat55UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat55UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat55UiModel> {
    val models = java.util.ArrayList<Feat55UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat55AnalyticsEvent1(val name: String, val value: String)
data class Feat55AnalyticsEvent2(val name: String, val value: String)
data class Feat55AnalyticsEvent3(val name: String, val value: String)
data class Feat55AnalyticsEvent4(val name: String, val value: String)
data class Feat55AnalyticsEvent5(val name: String, val value: String)
data class Feat55AnalyticsEvent6(val name: String, val value: String)
data class Feat55AnalyticsEvent7(val name: String, val value: String)
data class Feat55AnalyticsEvent8(val name: String, val value: String)
data class Feat55AnalyticsEvent9(val name: String, val value: String)
data class Feat55AnalyticsEvent10(val name: String, val value: String)

fun logFeat55Event1(event: Feat55AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat55Event2(event: Feat55AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat55Event3(event: Feat55AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat55Event4(event: Feat55AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat55Event5(event: Feat55AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat55Event6(event: Feat55AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat55Event7(event: Feat55AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat55Event8(event: Feat55AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat55Event9(event: Feat55AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat55Event10(event: Feat55AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat55Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat55Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat55Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat55Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat55Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat55Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat55Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat55Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat55Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat55Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat55(u: CoreUser): Feat55Projection1 =
    Feat55Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat55Projection1> {
    val list = java.util.ArrayList<Feat55Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat55(u)
    }
    return list
}
