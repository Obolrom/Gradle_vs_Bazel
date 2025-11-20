package com.romix.feature.feat683

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat683Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat683UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat683FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat683UserSummary
)

data class Feat683UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat683NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat683Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat683Config = Feat683Config()
) {

    fun loadSnapshot(userId: Long): Feat683NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat683NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat683UserSummary {
        return Feat683UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat683FeedItem> {
        val result = java.util.ArrayList<Feat683FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat683FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat683UiMapper {

    fun mapToUi(model: List<Feat683FeedItem>): Feat683UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat683UiModel(
            header = UiText("Feat683 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat683UiModel =
        Feat683UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat683UiModel =
        Feat683UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat683UiModel =
        Feat683UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat683Service(
    private val repository: Feat683Repository,
    private val uiMapper: Feat683UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat683UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat683UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat683UserItem1(val user: CoreUser, val label: String)
data class Feat683UserItem2(val user: CoreUser, val label: String)
data class Feat683UserItem3(val user: CoreUser, val label: String)
data class Feat683UserItem4(val user: CoreUser, val label: String)
data class Feat683UserItem5(val user: CoreUser, val label: String)
data class Feat683UserItem6(val user: CoreUser, val label: String)
data class Feat683UserItem7(val user: CoreUser, val label: String)
data class Feat683UserItem8(val user: CoreUser, val label: String)
data class Feat683UserItem9(val user: CoreUser, val label: String)
data class Feat683UserItem10(val user: CoreUser, val label: String)

data class Feat683StateBlock1(val state: Feat683UiModel, val checksum: Int)
data class Feat683StateBlock2(val state: Feat683UiModel, val checksum: Int)
data class Feat683StateBlock3(val state: Feat683UiModel, val checksum: Int)
data class Feat683StateBlock4(val state: Feat683UiModel, val checksum: Int)
data class Feat683StateBlock5(val state: Feat683UiModel, val checksum: Int)
data class Feat683StateBlock6(val state: Feat683UiModel, val checksum: Int)
data class Feat683StateBlock7(val state: Feat683UiModel, val checksum: Int)
data class Feat683StateBlock8(val state: Feat683UiModel, val checksum: Int)
data class Feat683StateBlock9(val state: Feat683UiModel, val checksum: Int)
data class Feat683StateBlock10(val state: Feat683UiModel, val checksum: Int)

fun buildFeat683UserItem(user: CoreUser, index: Int): Feat683UserItem1 {
    return Feat683UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat683StateBlock(model: Feat683UiModel): Feat683StateBlock1 {
    return Feat683StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat683UserSummary> {
    val list = java.util.ArrayList<Feat683UserSummary>(users.size)
    for (user in users) {
        list += Feat683UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat683UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat683UiModel {
    val summaries = (0 until count).map {
        Feat683UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat683UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat683UiModel> {
    val models = java.util.ArrayList<Feat683UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat683AnalyticsEvent1(val name: String, val value: String)
data class Feat683AnalyticsEvent2(val name: String, val value: String)
data class Feat683AnalyticsEvent3(val name: String, val value: String)
data class Feat683AnalyticsEvent4(val name: String, val value: String)
data class Feat683AnalyticsEvent5(val name: String, val value: String)
data class Feat683AnalyticsEvent6(val name: String, val value: String)
data class Feat683AnalyticsEvent7(val name: String, val value: String)
data class Feat683AnalyticsEvent8(val name: String, val value: String)
data class Feat683AnalyticsEvent9(val name: String, val value: String)
data class Feat683AnalyticsEvent10(val name: String, val value: String)

fun logFeat683Event1(event: Feat683AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat683Event2(event: Feat683AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat683Event3(event: Feat683AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat683Event4(event: Feat683AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat683Event5(event: Feat683AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat683Event6(event: Feat683AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat683Event7(event: Feat683AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat683Event8(event: Feat683AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat683Event9(event: Feat683AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat683Event10(event: Feat683AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat683Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat683Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat683Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat683Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat683Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat683Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat683Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat683Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat683Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat683Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat683(u: CoreUser): Feat683Projection1 =
    Feat683Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat683Projection1> {
    val list = java.util.ArrayList<Feat683Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat683(u)
    }
    return list
}
