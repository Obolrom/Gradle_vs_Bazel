package com.romix.feature.feat699

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat699Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat699UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat699FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat699UserSummary
)

data class Feat699UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat699NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat699Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat699Config = Feat699Config()
) {

    fun loadSnapshot(userId: Long): Feat699NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat699NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat699UserSummary {
        return Feat699UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat699FeedItem> {
        val result = java.util.ArrayList<Feat699FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat699FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat699UiMapper {

    fun mapToUi(model: List<Feat699FeedItem>): Feat699UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat699UiModel(
            header = UiText("Feat699 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat699UiModel =
        Feat699UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat699UiModel =
        Feat699UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat699UiModel =
        Feat699UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat699Service(
    private val repository: Feat699Repository,
    private val uiMapper: Feat699UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat699UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat699UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat699UserItem1(val user: CoreUser, val label: String)
data class Feat699UserItem2(val user: CoreUser, val label: String)
data class Feat699UserItem3(val user: CoreUser, val label: String)
data class Feat699UserItem4(val user: CoreUser, val label: String)
data class Feat699UserItem5(val user: CoreUser, val label: String)
data class Feat699UserItem6(val user: CoreUser, val label: String)
data class Feat699UserItem7(val user: CoreUser, val label: String)
data class Feat699UserItem8(val user: CoreUser, val label: String)
data class Feat699UserItem9(val user: CoreUser, val label: String)
data class Feat699UserItem10(val user: CoreUser, val label: String)

data class Feat699StateBlock1(val state: Feat699UiModel, val checksum: Int)
data class Feat699StateBlock2(val state: Feat699UiModel, val checksum: Int)
data class Feat699StateBlock3(val state: Feat699UiModel, val checksum: Int)
data class Feat699StateBlock4(val state: Feat699UiModel, val checksum: Int)
data class Feat699StateBlock5(val state: Feat699UiModel, val checksum: Int)
data class Feat699StateBlock6(val state: Feat699UiModel, val checksum: Int)
data class Feat699StateBlock7(val state: Feat699UiModel, val checksum: Int)
data class Feat699StateBlock8(val state: Feat699UiModel, val checksum: Int)
data class Feat699StateBlock9(val state: Feat699UiModel, val checksum: Int)
data class Feat699StateBlock10(val state: Feat699UiModel, val checksum: Int)

fun buildFeat699UserItem(user: CoreUser, index: Int): Feat699UserItem1 {
    return Feat699UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat699StateBlock(model: Feat699UiModel): Feat699StateBlock1 {
    return Feat699StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat699UserSummary> {
    val list = java.util.ArrayList<Feat699UserSummary>(users.size)
    for (user in users) {
        list += Feat699UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat699UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat699UiModel {
    val summaries = (0 until count).map {
        Feat699UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat699UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat699UiModel> {
    val models = java.util.ArrayList<Feat699UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat699AnalyticsEvent1(val name: String, val value: String)
data class Feat699AnalyticsEvent2(val name: String, val value: String)
data class Feat699AnalyticsEvent3(val name: String, val value: String)
data class Feat699AnalyticsEvent4(val name: String, val value: String)
data class Feat699AnalyticsEvent5(val name: String, val value: String)
data class Feat699AnalyticsEvent6(val name: String, val value: String)
data class Feat699AnalyticsEvent7(val name: String, val value: String)
data class Feat699AnalyticsEvent8(val name: String, val value: String)
data class Feat699AnalyticsEvent9(val name: String, val value: String)
data class Feat699AnalyticsEvent10(val name: String, val value: String)

fun logFeat699Event1(event: Feat699AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat699Event2(event: Feat699AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat699Event3(event: Feat699AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat699Event4(event: Feat699AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat699Event5(event: Feat699AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat699Event6(event: Feat699AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat699Event7(event: Feat699AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat699Event8(event: Feat699AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat699Event9(event: Feat699AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat699Event10(event: Feat699AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat699Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat699Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat699Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat699Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat699Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat699Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat699Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat699Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat699Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat699Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat699(u: CoreUser): Feat699Projection1 =
    Feat699Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat699Projection1> {
    val list = java.util.ArrayList<Feat699Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat699(u)
    }
    return list
}
