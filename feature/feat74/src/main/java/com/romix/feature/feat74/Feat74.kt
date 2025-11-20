package com.romix.feature.feat74

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat74Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat74UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat74FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat74UserSummary
)

data class Feat74UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat74NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat74Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat74Config = Feat74Config()
) {

    fun loadSnapshot(userId: Long): Feat74NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat74NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat74UserSummary {
        return Feat74UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat74FeedItem> {
        val result = java.util.ArrayList<Feat74FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat74FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat74UiMapper {

    fun mapToUi(model: List<Feat74FeedItem>): Feat74UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat74UiModel(
            header = UiText("Feat74 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat74UiModel =
        Feat74UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat74UiModel =
        Feat74UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat74UiModel =
        Feat74UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat74Service(
    private val repository: Feat74Repository,
    private val uiMapper: Feat74UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat74UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat74UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat74UserItem1(val user: CoreUser, val label: String)
data class Feat74UserItem2(val user: CoreUser, val label: String)
data class Feat74UserItem3(val user: CoreUser, val label: String)
data class Feat74UserItem4(val user: CoreUser, val label: String)
data class Feat74UserItem5(val user: CoreUser, val label: String)
data class Feat74UserItem6(val user: CoreUser, val label: String)
data class Feat74UserItem7(val user: CoreUser, val label: String)
data class Feat74UserItem8(val user: CoreUser, val label: String)
data class Feat74UserItem9(val user: CoreUser, val label: String)
data class Feat74UserItem10(val user: CoreUser, val label: String)

data class Feat74StateBlock1(val state: Feat74UiModel, val checksum: Int)
data class Feat74StateBlock2(val state: Feat74UiModel, val checksum: Int)
data class Feat74StateBlock3(val state: Feat74UiModel, val checksum: Int)
data class Feat74StateBlock4(val state: Feat74UiModel, val checksum: Int)
data class Feat74StateBlock5(val state: Feat74UiModel, val checksum: Int)
data class Feat74StateBlock6(val state: Feat74UiModel, val checksum: Int)
data class Feat74StateBlock7(val state: Feat74UiModel, val checksum: Int)
data class Feat74StateBlock8(val state: Feat74UiModel, val checksum: Int)
data class Feat74StateBlock9(val state: Feat74UiModel, val checksum: Int)
data class Feat74StateBlock10(val state: Feat74UiModel, val checksum: Int)

fun buildFeat74UserItem(user: CoreUser, index: Int): Feat74UserItem1 {
    return Feat74UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat74StateBlock(model: Feat74UiModel): Feat74StateBlock1 {
    return Feat74StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat74UserSummary> {
    val list = java.util.ArrayList<Feat74UserSummary>(users.size)
    for (user in users) {
        list += Feat74UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat74UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat74UiModel {
    val summaries = (0 until count).map {
        Feat74UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat74UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat74UiModel> {
    val models = java.util.ArrayList<Feat74UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat74AnalyticsEvent1(val name: String, val value: String)
data class Feat74AnalyticsEvent2(val name: String, val value: String)
data class Feat74AnalyticsEvent3(val name: String, val value: String)
data class Feat74AnalyticsEvent4(val name: String, val value: String)
data class Feat74AnalyticsEvent5(val name: String, val value: String)
data class Feat74AnalyticsEvent6(val name: String, val value: String)
data class Feat74AnalyticsEvent7(val name: String, val value: String)
data class Feat74AnalyticsEvent8(val name: String, val value: String)
data class Feat74AnalyticsEvent9(val name: String, val value: String)
data class Feat74AnalyticsEvent10(val name: String, val value: String)

fun logFeat74Event1(event: Feat74AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat74Event2(event: Feat74AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat74Event3(event: Feat74AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat74Event4(event: Feat74AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat74Event5(event: Feat74AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat74Event6(event: Feat74AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat74Event7(event: Feat74AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat74Event8(event: Feat74AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat74Event9(event: Feat74AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat74Event10(event: Feat74AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat74Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat74Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat74Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat74Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat74Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat74Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat74Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat74Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat74Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat74Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat74(u: CoreUser): Feat74Projection1 =
    Feat74Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat74Projection1> {
    val list = java.util.ArrayList<Feat74Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat74(u)
    }
    return list
}
