package com.romix.feature.feat33

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat33Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat33UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat33FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat33UserSummary
)

data class Feat33UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat33NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat33Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat33Config = Feat33Config()
) {

    fun loadSnapshot(userId: Long): Feat33NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat33NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat33UserSummary {
        return Feat33UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat33FeedItem> {
        val result = java.util.ArrayList<Feat33FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat33FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat33UiMapper {

    fun mapToUi(model: List<Feat33FeedItem>): Feat33UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat33UiModel(
            header = UiText("Feat33 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat33UiModel =
        Feat33UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat33UiModel =
        Feat33UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat33UiModel =
        Feat33UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat33Service(
    private val repository: Feat33Repository,
    private val uiMapper: Feat33UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat33UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat33UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat33UserItem1(val user: CoreUser, val label: String)
data class Feat33UserItem2(val user: CoreUser, val label: String)
data class Feat33UserItem3(val user: CoreUser, val label: String)
data class Feat33UserItem4(val user: CoreUser, val label: String)
data class Feat33UserItem5(val user: CoreUser, val label: String)
data class Feat33UserItem6(val user: CoreUser, val label: String)
data class Feat33UserItem7(val user: CoreUser, val label: String)
data class Feat33UserItem8(val user: CoreUser, val label: String)
data class Feat33UserItem9(val user: CoreUser, val label: String)
data class Feat33UserItem10(val user: CoreUser, val label: String)

data class Feat33StateBlock1(val state: Feat33UiModel, val checksum: Int)
data class Feat33StateBlock2(val state: Feat33UiModel, val checksum: Int)
data class Feat33StateBlock3(val state: Feat33UiModel, val checksum: Int)
data class Feat33StateBlock4(val state: Feat33UiModel, val checksum: Int)
data class Feat33StateBlock5(val state: Feat33UiModel, val checksum: Int)
data class Feat33StateBlock6(val state: Feat33UiModel, val checksum: Int)
data class Feat33StateBlock7(val state: Feat33UiModel, val checksum: Int)
data class Feat33StateBlock8(val state: Feat33UiModel, val checksum: Int)
data class Feat33StateBlock9(val state: Feat33UiModel, val checksum: Int)
data class Feat33StateBlock10(val state: Feat33UiModel, val checksum: Int)

fun buildFeat33UserItem(user: CoreUser, index: Int): Feat33UserItem1 {
    return Feat33UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat33StateBlock(model: Feat33UiModel): Feat33StateBlock1 {
    return Feat33StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat33UserSummary> {
    val list = java.util.ArrayList<Feat33UserSummary>(users.size)
    for (user in users) {
        list += Feat33UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat33UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat33UiModel {
    val summaries = (0 until count).map {
        Feat33UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat33UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat33UiModel> {
    val models = java.util.ArrayList<Feat33UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat33AnalyticsEvent1(val name: String, val value: String)
data class Feat33AnalyticsEvent2(val name: String, val value: String)
data class Feat33AnalyticsEvent3(val name: String, val value: String)
data class Feat33AnalyticsEvent4(val name: String, val value: String)
data class Feat33AnalyticsEvent5(val name: String, val value: String)
data class Feat33AnalyticsEvent6(val name: String, val value: String)
data class Feat33AnalyticsEvent7(val name: String, val value: String)
data class Feat33AnalyticsEvent8(val name: String, val value: String)
data class Feat33AnalyticsEvent9(val name: String, val value: String)
data class Feat33AnalyticsEvent10(val name: String, val value: String)

fun logFeat33Event1(event: Feat33AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat33Event2(event: Feat33AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat33Event3(event: Feat33AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat33Event4(event: Feat33AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat33Event5(event: Feat33AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat33Event6(event: Feat33AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat33Event7(event: Feat33AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat33Event8(event: Feat33AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat33Event9(event: Feat33AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat33Event10(event: Feat33AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat33Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat33Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat33Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat33Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat33Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat33Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat33Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat33Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat33Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat33Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat33(u: CoreUser): Feat33Projection1 =
    Feat33Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat33Projection1> {
    val list = java.util.ArrayList<Feat33Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat33(u)
    }
    return list
}
