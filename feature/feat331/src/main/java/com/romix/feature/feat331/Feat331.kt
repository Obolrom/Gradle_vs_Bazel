package com.romix.feature.feat331

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat331Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat331UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat331FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat331UserSummary
)

data class Feat331UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat331NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat331Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat331Config = Feat331Config()
) {

    fun loadSnapshot(userId: Long): Feat331NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat331NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat331UserSummary {
        return Feat331UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat331FeedItem> {
        val result = java.util.ArrayList<Feat331FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat331FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat331UiMapper {

    fun mapToUi(model: List<Feat331FeedItem>): Feat331UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat331UiModel(
            header = UiText("Feat331 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat331UiModel =
        Feat331UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat331UiModel =
        Feat331UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat331UiModel =
        Feat331UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat331Service(
    private val repository: Feat331Repository,
    private val uiMapper: Feat331UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat331UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat331UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat331UserItem1(val user: CoreUser, val label: String)
data class Feat331UserItem2(val user: CoreUser, val label: String)
data class Feat331UserItem3(val user: CoreUser, val label: String)
data class Feat331UserItem4(val user: CoreUser, val label: String)
data class Feat331UserItem5(val user: CoreUser, val label: String)
data class Feat331UserItem6(val user: CoreUser, val label: String)
data class Feat331UserItem7(val user: CoreUser, val label: String)
data class Feat331UserItem8(val user: CoreUser, val label: String)
data class Feat331UserItem9(val user: CoreUser, val label: String)
data class Feat331UserItem10(val user: CoreUser, val label: String)

data class Feat331StateBlock1(val state: Feat331UiModel, val checksum: Int)
data class Feat331StateBlock2(val state: Feat331UiModel, val checksum: Int)
data class Feat331StateBlock3(val state: Feat331UiModel, val checksum: Int)
data class Feat331StateBlock4(val state: Feat331UiModel, val checksum: Int)
data class Feat331StateBlock5(val state: Feat331UiModel, val checksum: Int)
data class Feat331StateBlock6(val state: Feat331UiModel, val checksum: Int)
data class Feat331StateBlock7(val state: Feat331UiModel, val checksum: Int)
data class Feat331StateBlock8(val state: Feat331UiModel, val checksum: Int)
data class Feat331StateBlock9(val state: Feat331UiModel, val checksum: Int)
data class Feat331StateBlock10(val state: Feat331UiModel, val checksum: Int)

fun buildFeat331UserItem(user: CoreUser, index: Int): Feat331UserItem1 {
    return Feat331UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat331StateBlock(model: Feat331UiModel): Feat331StateBlock1 {
    return Feat331StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat331UserSummary> {
    val list = java.util.ArrayList<Feat331UserSummary>(users.size)
    for (user in users) {
        list += Feat331UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat331UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat331UiModel {
    val summaries = (0 until count).map {
        Feat331UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat331UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat331UiModel> {
    val models = java.util.ArrayList<Feat331UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat331AnalyticsEvent1(val name: String, val value: String)
data class Feat331AnalyticsEvent2(val name: String, val value: String)
data class Feat331AnalyticsEvent3(val name: String, val value: String)
data class Feat331AnalyticsEvent4(val name: String, val value: String)
data class Feat331AnalyticsEvent5(val name: String, val value: String)
data class Feat331AnalyticsEvent6(val name: String, val value: String)
data class Feat331AnalyticsEvent7(val name: String, val value: String)
data class Feat331AnalyticsEvent8(val name: String, val value: String)
data class Feat331AnalyticsEvent9(val name: String, val value: String)
data class Feat331AnalyticsEvent10(val name: String, val value: String)

fun logFeat331Event1(event: Feat331AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat331Event2(event: Feat331AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat331Event3(event: Feat331AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat331Event4(event: Feat331AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat331Event5(event: Feat331AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat331Event6(event: Feat331AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat331Event7(event: Feat331AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat331Event8(event: Feat331AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat331Event9(event: Feat331AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat331Event10(event: Feat331AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat331Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat331Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat331Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat331Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat331Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat331Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat331Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat331Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat331Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat331Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat331(u: CoreUser): Feat331Projection1 =
    Feat331Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat331Projection1> {
    val list = java.util.ArrayList<Feat331Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat331(u)
    }
    return list
}
