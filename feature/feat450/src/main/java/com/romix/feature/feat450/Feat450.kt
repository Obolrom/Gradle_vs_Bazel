package com.romix.feature.feat450

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat450Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat450UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat450FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat450UserSummary
)

data class Feat450UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat450NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat450Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat450Config = Feat450Config()
) {

    fun loadSnapshot(userId: Long): Feat450NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat450NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat450UserSummary {
        return Feat450UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat450FeedItem> {
        val result = java.util.ArrayList<Feat450FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat450FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat450UiMapper {

    fun mapToUi(model: List<Feat450FeedItem>): Feat450UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat450UiModel(
            header = UiText("Feat450 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat450UiModel =
        Feat450UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat450UiModel =
        Feat450UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat450UiModel =
        Feat450UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat450Service(
    private val repository: Feat450Repository,
    private val uiMapper: Feat450UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat450UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat450UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat450UserItem1(val user: CoreUser, val label: String)
data class Feat450UserItem2(val user: CoreUser, val label: String)
data class Feat450UserItem3(val user: CoreUser, val label: String)
data class Feat450UserItem4(val user: CoreUser, val label: String)
data class Feat450UserItem5(val user: CoreUser, val label: String)
data class Feat450UserItem6(val user: CoreUser, val label: String)
data class Feat450UserItem7(val user: CoreUser, val label: String)
data class Feat450UserItem8(val user: CoreUser, val label: String)
data class Feat450UserItem9(val user: CoreUser, val label: String)
data class Feat450UserItem10(val user: CoreUser, val label: String)

data class Feat450StateBlock1(val state: Feat450UiModel, val checksum: Int)
data class Feat450StateBlock2(val state: Feat450UiModel, val checksum: Int)
data class Feat450StateBlock3(val state: Feat450UiModel, val checksum: Int)
data class Feat450StateBlock4(val state: Feat450UiModel, val checksum: Int)
data class Feat450StateBlock5(val state: Feat450UiModel, val checksum: Int)
data class Feat450StateBlock6(val state: Feat450UiModel, val checksum: Int)
data class Feat450StateBlock7(val state: Feat450UiModel, val checksum: Int)
data class Feat450StateBlock8(val state: Feat450UiModel, val checksum: Int)
data class Feat450StateBlock9(val state: Feat450UiModel, val checksum: Int)
data class Feat450StateBlock10(val state: Feat450UiModel, val checksum: Int)

fun buildFeat450UserItem(user: CoreUser, index: Int): Feat450UserItem1 {
    return Feat450UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat450StateBlock(model: Feat450UiModel): Feat450StateBlock1 {
    return Feat450StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat450UserSummary> {
    val list = java.util.ArrayList<Feat450UserSummary>(users.size)
    for (user in users) {
        list += Feat450UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat450UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat450UiModel {
    val summaries = (0 until count).map {
        Feat450UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat450UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat450UiModel> {
    val models = java.util.ArrayList<Feat450UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat450AnalyticsEvent1(val name: String, val value: String)
data class Feat450AnalyticsEvent2(val name: String, val value: String)
data class Feat450AnalyticsEvent3(val name: String, val value: String)
data class Feat450AnalyticsEvent4(val name: String, val value: String)
data class Feat450AnalyticsEvent5(val name: String, val value: String)
data class Feat450AnalyticsEvent6(val name: String, val value: String)
data class Feat450AnalyticsEvent7(val name: String, val value: String)
data class Feat450AnalyticsEvent8(val name: String, val value: String)
data class Feat450AnalyticsEvent9(val name: String, val value: String)
data class Feat450AnalyticsEvent10(val name: String, val value: String)

fun logFeat450Event1(event: Feat450AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat450Event2(event: Feat450AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat450Event3(event: Feat450AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat450Event4(event: Feat450AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat450Event5(event: Feat450AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat450Event6(event: Feat450AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat450Event7(event: Feat450AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat450Event8(event: Feat450AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat450Event9(event: Feat450AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat450Event10(event: Feat450AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat450Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat450Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat450Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat450Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat450Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat450Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat450Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat450Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat450Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat450Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat450(u: CoreUser): Feat450Projection1 =
    Feat450Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat450Projection1> {
    val list = java.util.ArrayList<Feat450Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat450(u)
    }
    return list
}
