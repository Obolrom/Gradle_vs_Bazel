package com.romix.feature.feat690

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat690Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat690UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat690FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat690UserSummary
)

data class Feat690UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat690NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat690Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat690Config = Feat690Config()
) {

    fun loadSnapshot(userId: Long): Feat690NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat690NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat690UserSummary {
        return Feat690UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat690FeedItem> {
        val result = java.util.ArrayList<Feat690FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat690FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat690UiMapper {

    fun mapToUi(model: List<Feat690FeedItem>): Feat690UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat690UiModel(
            header = UiText("Feat690 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat690UiModel =
        Feat690UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat690UiModel =
        Feat690UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat690UiModel =
        Feat690UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat690Service(
    private val repository: Feat690Repository,
    private val uiMapper: Feat690UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat690UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat690UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat690UserItem1(val user: CoreUser, val label: String)
data class Feat690UserItem2(val user: CoreUser, val label: String)
data class Feat690UserItem3(val user: CoreUser, val label: String)
data class Feat690UserItem4(val user: CoreUser, val label: String)
data class Feat690UserItem5(val user: CoreUser, val label: String)
data class Feat690UserItem6(val user: CoreUser, val label: String)
data class Feat690UserItem7(val user: CoreUser, val label: String)
data class Feat690UserItem8(val user: CoreUser, val label: String)
data class Feat690UserItem9(val user: CoreUser, val label: String)
data class Feat690UserItem10(val user: CoreUser, val label: String)

data class Feat690StateBlock1(val state: Feat690UiModel, val checksum: Int)
data class Feat690StateBlock2(val state: Feat690UiModel, val checksum: Int)
data class Feat690StateBlock3(val state: Feat690UiModel, val checksum: Int)
data class Feat690StateBlock4(val state: Feat690UiModel, val checksum: Int)
data class Feat690StateBlock5(val state: Feat690UiModel, val checksum: Int)
data class Feat690StateBlock6(val state: Feat690UiModel, val checksum: Int)
data class Feat690StateBlock7(val state: Feat690UiModel, val checksum: Int)
data class Feat690StateBlock8(val state: Feat690UiModel, val checksum: Int)
data class Feat690StateBlock9(val state: Feat690UiModel, val checksum: Int)
data class Feat690StateBlock10(val state: Feat690UiModel, val checksum: Int)

fun buildFeat690UserItem(user: CoreUser, index: Int): Feat690UserItem1 {
    return Feat690UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat690StateBlock(model: Feat690UiModel): Feat690StateBlock1 {
    return Feat690StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat690UserSummary> {
    val list = java.util.ArrayList<Feat690UserSummary>(users.size)
    for (user in users) {
        list += Feat690UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat690UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat690UiModel {
    val summaries = (0 until count).map {
        Feat690UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat690UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat690UiModel> {
    val models = java.util.ArrayList<Feat690UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat690AnalyticsEvent1(val name: String, val value: String)
data class Feat690AnalyticsEvent2(val name: String, val value: String)
data class Feat690AnalyticsEvent3(val name: String, val value: String)
data class Feat690AnalyticsEvent4(val name: String, val value: String)
data class Feat690AnalyticsEvent5(val name: String, val value: String)
data class Feat690AnalyticsEvent6(val name: String, val value: String)
data class Feat690AnalyticsEvent7(val name: String, val value: String)
data class Feat690AnalyticsEvent8(val name: String, val value: String)
data class Feat690AnalyticsEvent9(val name: String, val value: String)
data class Feat690AnalyticsEvent10(val name: String, val value: String)

fun logFeat690Event1(event: Feat690AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat690Event2(event: Feat690AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat690Event3(event: Feat690AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat690Event4(event: Feat690AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat690Event5(event: Feat690AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat690Event6(event: Feat690AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat690Event7(event: Feat690AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat690Event8(event: Feat690AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat690Event9(event: Feat690AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat690Event10(event: Feat690AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat690Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat690Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat690Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat690Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat690Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat690Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat690Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat690Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat690Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat690Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat690(u: CoreUser): Feat690Projection1 =
    Feat690Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat690Projection1> {
    val list = java.util.ArrayList<Feat690Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat690(u)
    }
    return list
}
