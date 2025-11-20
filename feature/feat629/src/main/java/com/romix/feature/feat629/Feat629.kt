package com.romix.feature.feat629

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat629Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat629UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat629FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat629UserSummary
)

data class Feat629UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat629NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat629Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat629Config = Feat629Config()
) {

    fun loadSnapshot(userId: Long): Feat629NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat629NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat629UserSummary {
        return Feat629UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat629FeedItem> {
        val result = java.util.ArrayList<Feat629FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat629FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat629UiMapper {

    fun mapToUi(model: List<Feat629FeedItem>): Feat629UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat629UiModel(
            header = UiText("Feat629 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat629UiModel =
        Feat629UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat629UiModel =
        Feat629UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat629UiModel =
        Feat629UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat629Service(
    private val repository: Feat629Repository,
    private val uiMapper: Feat629UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat629UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat629UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat629UserItem1(val user: CoreUser, val label: String)
data class Feat629UserItem2(val user: CoreUser, val label: String)
data class Feat629UserItem3(val user: CoreUser, val label: String)
data class Feat629UserItem4(val user: CoreUser, val label: String)
data class Feat629UserItem5(val user: CoreUser, val label: String)
data class Feat629UserItem6(val user: CoreUser, val label: String)
data class Feat629UserItem7(val user: CoreUser, val label: String)
data class Feat629UserItem8(val user: CoreUser, val label: String)
data class Feat629UserItem9(val user: CoreUser, val label: String)
data class Feat629UserItem10(val user: CoreUser, val label: String)

data class Feat629StateBlock1(val state: Feat629UiModel, val checksum: Int)
data class Feat629StateBlock2(val state: Feat629UiModel, val checksum: Int)
data class Feat629StateBlock3(val state: Feat629UiModel, val checksum: Int)
data class Feat629StateBlock4(val state: Feat629UiModel, val checksum: Int)
data class Feat629StateBlock5(val state: Feat629UiModel, val checksum: Int)
data class Feat629StateBlock6(val state: Feat629UiModel, val checksum: Int)
data class Feat629StateBlock7(val state: Feat629UiModel, val checksum: Int)
data class Feat629StateBlock8(val state: Feat629UiModel, val checksum: Int)
data class Feat629StateBlock9(val state: Feat629UiModel, val checksum: Int)
data class Feat629StateBlock10(val state: Feat629UiModel, val checksum: Int)

fun buildFeat629UserItem(user: CoreUser, index: Int): Feat629UserItem1 {
    return Feat629UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat629StateBlock(model: Feat629UiModel): Feat629StateBlock1 {
    return Feat629StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat629UserSummary> {
    val list = java.util.ArrayList<Feat629UserSummary>(users.size)
    for (user in users) {
        list += Feat629UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat629UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat629UiModel {
    val summaries = (0 until count).map {
        Feat629UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat629UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat629UiModel> {
    val models = java.util.ArrayList<Feat629UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat629AnalyticsEvent1(val name: String, val value: String)
data class Feat629AnalyticsEvent2(val name: String, val value: String)
data class Feat629AnalyticsEvent3(val name: String, val value: String)
data class Feat629AnalyticsEvent4(val name: String, val value: String)
data class Feat629AnalyticsEvent5(val name: String, val value: String)
data class Feat629AnalyticsEvent6(val name: String, val value: String)
data class Feat629AnalyticsEvent7(val name: String, val value: String)
data class Feat629AnalyticsEvent8(val name: String, val value: String)
data class Feat629AnalyticsEvent9(val name: String, val value: String)
data class Feat629AnalyticsEvent10(val name: String, val value: String)

fun logFeat629Event1(event: Feat629AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat629Event2(event: Feat629AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat629Event3(event: Feat629AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat629Event4(event: Feat629AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat629Event5(event: Feat629AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat629Event6(event: Feat629AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat629Event7(event: Feat629AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat629Event8(event: Feat629AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat629Event9(event: Feat629AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat629Event10(event: Feat629AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat629Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat629Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat629Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat629Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat629Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat629Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat629Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat629Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat629Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat629Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat629(u: CoreUser): Feat629Projection1 =
    Feat629Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat629Projection1> {
    val list = java.util.ArrayList<Feat629Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat629(u)
    }
    return list
}
