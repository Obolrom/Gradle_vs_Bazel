package com.romix.feature.feat97

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat97Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat97UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat97FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat97UserSummary
)

data class Feat97UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat97NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat97Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat97Config = Feat97Config()
) {

    fun loadSnapshot(userId: Long): Feat97NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat97NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat97UserSummary {
        return Feat97UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat97FeedItem> {
        val result = java.util.ArrayList<Feat97FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat97FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat97UiMapper {

    fun mapToUi(model: List<Feat97FeedItem>): Feat97UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat97UiModel(
            header = UiText("Feat97 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat97UiModel =
        Feat97UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat97UiModel =
        Feat97UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat97UiModel =
        Feat97UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat97Service(
    private val repository: Feat97Repository,
    private val uiMapper: Feat97UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat97UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat97UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat97UserItem1(val user: CoreUser, val label: String)
data class Feat97UserItem2(val user: CoreUser, val label: String)
data class Feat97UserItem3(val user: CoreUser, val label: String)
data class Feat97UserItem4(val user: CoreUser, val label: String)
data class Feat97UserItem5(val user: CoreUser, val label: String)
data class Feat97UserItem6(val user: CoreUser, val label: String)
data class Feat97UserItem7(val user: CoreUser, val label: String)
data class Feat97UserItem8(val user: CoreUser, val label: String)
data class Feat97UserItem9(val user: CoreUser, val label: String)
data class Feat97UserItem10(val user: CoreUser, val label: String)

data class Feat97StateBlock1(val state: Feat97UiModel, val checksum: Int)
data class Feat97StateBlock2(val state: Feat97UiModel, val checksum: Int)
data class Feat97StateBlock3(val state: Feat97UiModel, val checksum: Int)
data class Feat97StateBlock4(val state: Feat97UiModel, val checksum: Int)
data class Feat97StateBlock5(val state: Feat97UiModel, val checksum: Int)
data class Feat97StateBlock6(val state: Feat97UiModel, val checksum: Int)
data class Feat97StateBlock7(val state: Feat97UiModel, val checksum: Int)
data class Feat97StateBlock8(val state: Feat97UiModel, val checksum: Int)
data class Feat97StateBlock9(val state: Feat97UiModel, val checksum: Int)
data class Feat97StateBlock10(val state: Feat97UiModel, val checksum: Int)

fun buildFeat97UserItem(user: CoreUser, index: Int): Feat97UserItem1 {
    return Feat97UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat97StateBlock(model: Feat97UiModel): Feat97StateBlock1 {
    return Feat97StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat97UserSummary> {
    val list = java.util.ArrayList<Feat97UserSummary>(users.size)
    for (user in users) {
        list += Feat97UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat97UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat97UiModel {
    val summaries = (0 until count).map {
        Feat97UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat97UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat97UiModel> {
    val models = java.util.ArrayList<Feat97UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat97AnalyticsEvent1(val name: String, val value: String)
data class Feat97AnalyticsEvent2(val name: String, val value: String)
data class Feat97AnalyticsEvent3(val name: String, val value: String)
data class Feat97AnalyticsEvent4(val name: String, val value: String)
data class Feat97AnalyticsEvent5(val name: String, val value: String)
data class Feat97AnalyticsEvent6(val name: String, val value: String)
data class Feat97AnalyticsEvent7(val name: String, val value: String)
data class Feat97AnalyticsEvent8(val name: String, val value: String)
data class Feat97AnalyticsEvent9(val name: String, val value: String)
data class Feat97AnalyticsEvent10(val name: String, val value: String)

fun logFeat97Event1(event: Feat97AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat97Event2(event: Feat97AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat97Event3(event: Feat97AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat97Event4(event: Feat97AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat97Event5(event: Feat97AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat97Event6(event: Feat97AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat97Event7(event: Feat97AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat97Event8(event: Feat97AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat97Event9(event: Feat97AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat97Event10(event: Feat97AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat97Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat97Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat97Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat97Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat97Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat97Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat97Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat97Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat97Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat97Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat97(u: CoreUser): Feat97Projection1 =
    Feat97Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat97Projection1> {
    val list = java.util.ArrayList<Feat97Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat97(u)
    }
    return list
}
