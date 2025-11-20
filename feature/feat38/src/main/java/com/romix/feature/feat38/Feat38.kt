package com.romix.feature.feat38

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat38Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat38UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat38FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat38UserSummary
)

data class Feat38UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat38NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat38Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat38Config = Feat38Config()
) {

    fun loadSnapshot(userId: Long): Feat38NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat38NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat38UserSummary {
        return Feat38UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat38FeedItem> {
        val result = java.util.ArrayList<Feat38FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat38FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat38UiMapper {

    fun mapToUi(model: List<Feat38FeedItem>): Feat38UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat38UiModel(
            header = UiText("Feat38 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat38UiModel =
        Feat38UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat38UiModel =
        Feat38UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat38UiModel =
        Feat38UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat38Service(
    private val repository: Feat38Repository,
    private val uiMapper: Feat38UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat38UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat38UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat38UserItem1(val user: CoreUser, val label: String)
data class Feat38UserItem2(val user: CoreUser, val label: String)
data class Feat38UserItem3(val user: CoreUser, val label: String)
data class Feat38UserItem4(val user: CoreUser, val label: String)
data class Feat38UserItem5(val user: CoreUser, val label: String)
data class Feat38UserItem6(val user: CoreUser, val label: String)
data class Feat38UserItem7(val user: CoreUser, val label: String)
data class Feat38UserItem8(val user: CoreUser, val label: String)
data class Feat38UserItem9(val user: CoreUser, val label: String)
data class Feat38UserItem10(val user: CoreUser, val label: String)

data class Feat38StateBlock1(val state: Feat38UiModel, val checksum: Int)
data class Feat38StateBlock2(val state: Feat38UiModel, val checksum: Int)
data class Feat38StateBlock3(val state: Feat38UiModel, val checksum: Int)
data class Feat38StateBlock4(val state: Feat38UiModel, val checksum: Int)
data class Feat38StateBlock5(val state: Feat38UiModel, val checksum: Int)
data class Feat38StateBlock6(val state: Feat38UiModel, val checksum: Int)
data class Feat38StateBlock7(val state: Feat38UiModel, val checksum: Int)
data class Feat38StateBlock8(val state: Feat38UiModel, val checksum: Int)
data class Feat38StateBlock9(val state: Feat38UiModel, val checksum: Int)
data class Feat38StateBlock10(val state: Feat38UiModel, val checksum: Int)

fun buildFeat38UserItem(user: CoreUser, index: Int): Feat38UserItem1 {
    return Feat38UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat38StateBlock(model: Feat38UiModel): Feat38StateBlock1 {
    return Feat38StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat38UserSummary> {
    val list = java.util.ArrayList<Feat38UserSummary>(users.size)
    for (user in users) {
        list += Feat38UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat38UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat38UiModel {
    val summaries = (0 until count).map {
        Feat38UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat38UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat38UiModel> {
    val models = java.util.ArrayList<Feat38UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat38AnalyticsEvent1(val name: String, val value: String)
data class Feat38AnalyticsEvent2(val name: String, val value: String)
data class Feat38AnalyticsEvent3(val name: String, val value: String)
data class Feat38AnalyticsEvent4(val name: String, val value: String)
data class Feat38AnalyticsEvent5(val name: String, val value: String)
data class Feat38AnalyticsEvent6(val name: String, val value: String)
data class Feat38AnalyticsEvent7(val name: String, val value: String)
data class Feat38AnalyticsEvent8(val name: String, val value: String)
data class Feat38AnalyticsEvent9(val name: String, val value: String)
data class Feat38AnalyticsEvent10(val name: String, val value: String)

fun logFeat38Event1(event: Feat38AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat38Event2(event: Feat38AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat38Event3(event: Feat38AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat38Event4(event: Feat38AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat38Event5(event: Feat38AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat38Event6(event: Feat38AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat38Event7(event: Feat38AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat38Event8(event: Feat38AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat38Event9(event: Feat38AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat38Event10(event: Feat38AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat38Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat38Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat38Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat38Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat38Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat38Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat38Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat38Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat38Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat38Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat38(u: CoreUser): Feat38Projection1 =
    Feat38Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat38Projection1> {
    val list = java.util.ArrayList<Feat38Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat38(u)
    }
    return list
}
