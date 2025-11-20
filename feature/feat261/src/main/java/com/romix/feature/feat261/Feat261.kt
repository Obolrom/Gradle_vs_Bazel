package com.romix.feature.feat261

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat261Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat261UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat261FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat261UserSummary
)

data class Feat261UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat261NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat261Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat261Config = Feat261Config()
) {

    fun loadSnapshot(userId: Long): Feat261NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat261NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat261UserSummary {
        return Feat261UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat261FeedItem> {
        val result = java.util.ArrayList<Feat261FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat261FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat261UiMapper {

    fun mapToUi(model: List<Feat261FeedItem>): Feat261UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat261UiModel(
            header = UiText("Feat261 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat261UiModel =
        Feat261UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat261UiModel =
        Feat261UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat261UiModel =
        Feat261UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat261Service(
    private val repository: Feat261Repository,
    private val uiMapper: Feat261UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat261UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat261UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat261UserItem1(val user: CoreUser, val label: String)
data class Feat261UserItem2(val user: CoreUser, val label: String)
data class Feat261UserItem3(val user: CoreUser, val label: String)
data class Feat261UserItem4(val user: CoreUser, val label: String)
data class Feat261UserItem5(val user: CoreUser, val label: String)
data class Feat261UserItem6(val user: CoreUser, val label: String)
data class Feat261UserItem7(val user: CoreUser, val label: String)
data class Feat261UserItem8(val user: CoreUser, val label: String)
data class Feat261UserItem9(val user: CoreUser, val label: String)
data class Feat261UserItem10(val user: CoreUser, val label: String)

data class Feat261StateBlock1(val state: Feat261UiModel, val checksum: Int)
data class Feat261StateBlock2(val state: Feat261UiModel, val checksum: Int)
data class Feat261StateBlock3(val state: Feat261UiModel, val checksum: Int)
data class Feat261StateBlock4(val state: Feat261UiModel, val checksum: Int)
data class Feat261StateBlock5(val state: Feat261UiModel, val checksum: Int)
data class Feat261StateBlock6(val state: Feat261UiModel, val checksum: Int)
data class Feat261StateBlock7(val state: Feat261UiModel, val checksum: Int)
data class Feat261StateBlock8(val state: Feat261UiModel, val checksum: Int)
data class Feat261StateBlock9(val state: Feat261UiModel, val checksum: Int)
data class Feat261StateBlock10(val state: Feat261UiModel, val checksum: Int)

fun buildFeat261UserItem(user: CoreUser, index: Int): Feat261UserItem1 {
    return Feat261UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat261StateBlock(model: Feat261UiModel): Feat261StateBlock1 {
    return Feat261StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat261UserSummary> {
    val list = java.util.ArrayList<Feat261UserSummary>(users.size)
    for (user in users) {
        list += Feat261UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat261UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat261UiModel {
    val summaries = (0 until count).map {
        Feat261UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat261UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat261UiModel> {
    val models = java.util.ArrayList<Feat261UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat261AnalyticsEvent1(val name: String, val value: String)
data class Feat261AnalyticsEvent2(val name: String, val value: String)
data class Feat261AnalyticsEvent3(val name: String, val value: String)
data class Feat261AnalyticsEvent4(val name: String, val value: String)
data class Feat261AnalyticsEvent5(val name: String, val value: String)
data class Feat261AnalyticsEvent6(val name: String, val value: String)
data class Feat261AnalyticsEvent7(val name: String, val value: String)
data class Feat261AnalyticsEvent8(val name: String, val value: String)
data class Feat261AnalyticsEvent9(val name: String, val value: String)
data class Feat261AnalyticsEvent10(val name: String, val value: String)

fun logFeat261Event1(event: Feat261AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat261Event2(event: Feat261AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat261Event3(event: Feat261AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat261Event4(event: Feat261AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat261Event5(event: Feat261AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat261Event6(event: Feat261AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat261Event7(event: Feat261AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat261Event8(event: Feat261AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat261Event9(event: Feat261AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat261Event10(event: Feat261AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat261Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat261Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat261Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat261Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat261Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat261Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat261Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat261Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat261Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat261Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat261(u: CoreUser): Feat261Projection1 =
    Feat261Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat261Projection1> {
    val list = java.util.ArrayList<Feat261Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat261(u)
    }
    return list
}
