package com.romix.feature.feat199

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat199Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat199UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat199FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat199UserSummary
)

data class Feat199UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat199NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat199Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat199Config = Feat199Config()
) {

    fun loadSnapshot(userId: Long): Feat199NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat199NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat199UserSummary {
        return Feat199UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat199FeedItem> {
        val result = java.util.ArrayList<Feat199FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat199FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat199UiMapper {

    fun mapToUi(model: List<Feat199FeedItem>): Feat199UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat199UiModel(
            header = UiText("Feat199 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat199UiModel =
        Feat199UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat199UiModel =
        Feat199UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat199UiModel =
        Feat199UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat199Service(
    private val repository: Feat199Repository,
    private val uiMapper: Feat199UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat199UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat199UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat199UserItem1(val user: CoreUser, val label: String)
data class Feat199UserItem2(val user: CoreUser, val label: String)
data class Feat199UserItem3(val user: CoreUser, val label: String)
data class Feat199UserItem4(val user: CoreUser, val label: String)
data class Feat199UserItem5(val user: CoreUser, val label: String)
data class Feat199UserItem6(val user: CoreUser, val label: String)
data class Feat199UserItem7(val user: CoreUser, val label: String)
data class Feat199UserItem8(val user: CoreUser, val label: String)
data class Feat199UserItem9(val user: CoreUser, val label: String)
data class Feat199UserItem10(val user: CoreUser, val label: String)

data class Feat199StateBlock1(val state: Feat199UiModel, val checksum: Int)
data class Feat199StateBlock2(val state: Feat199UiModel, val checksum: Int)
data class Feat199StateBlock3(val state: Feat199UiModel, val checksum: Int)
data class Feat199StateBlock4(val state: Feat199UiModel, val checksum: Int)
data class Feat199StateBlock5(val state: Feat199UiModel, val checksum: Int)
data class Feat199StateBlock6(val state: Feat199UiModel, val checksum: Int)
data class Feat199StateBlock7(val state: Feat199UiModel, val checksum: Int)
data class Feat199StateBlock8(val state: Feat199UiModel, val checksum: Int)
data class Feat199StateBlock9(val state: Feat199UiModel, val checksum: Int)
data class Feat199StateBlock10(val state: Feat199UiModel, val checksum: Int)

fun buildFeat199UserItem(user: CoreUser, index: Int): Feat199UserItem1 {
    return Feat199UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat199StateBlock(model: Feat199UiModel): Feat199StateBlock1 {
    return Feat199StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat199UserSummary> {
    val list = java.util.ArrayList<Feat199UserSummary>(users.size)
    for (user in users) {
        list += Feat199UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat199UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat199UiModel {
    val summaries = (0 until count).map {
        Feat199UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat199UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat199UiModel> {
    val models = java.util.ArrayList<Feat199UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat199AnalyticsEvent1(val name: String, val value: String)
data class Feat199AnalyticsEvent2(val name: String, val value: String)
data class Feat199AnalyticsEvent3(val name: String, val value: String)
data class Feat199AnalyticsEvent4(val name: String, val value: String)
data class Feat199AnalyticsEvent5(val name: String, val value: String)
data class Feat199AnalyticsEvent6(val name: String, val value: String)
data class Feat199AnalyticsEvent7(val name: String, val value: String)
data class Feat199AnalyticsEvent8(val name: String, val value: String)
data class Feat199AnalyticsEvent9(val name: String, val value: String)
data class Feat199AnalyticsEvent10(val name: String, val value: String)

fun logFeat199Event1(event: Feat199AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat199Event2(event: Feat199AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat199Event3(event: Feat199AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat199Event4(event: Feat199AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat199Event5(event: Feat199AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat199Event6(event: Feat199AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat199Event7(event: Feat199AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat199Event8(event: Feat199AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat199Event9(event: Feat199AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat199Event10(event: Feat199AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat199Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat199Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat199Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat199Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat199Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat199Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat199Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat199Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat199Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat199Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat199(u: CoreUser): Feat199Projection1 =
    Feat199Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat199Projection1> {
    val list = java.util.ArrayList<Feat199Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat199(u)
    }
    return list
}
