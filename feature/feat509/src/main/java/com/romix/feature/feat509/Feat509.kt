package com.romix.feature.feat509

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat509Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat509UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat509FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat509UserSummary
)

data class Feat509UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat509NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat509Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat509Config = Feat509Config()
) {

    fun loadSnapshot(userId: Long): Feat509NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat509NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat509UserSummary {
        return Feat509UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat509FeedItem> {
        val result = java.util.ArrayList<Feat509FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat509FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat509UiMapper {

    fun mapToUi(model: List<Feat509FeedItem>): Feat509UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat509UiModel(
            header = UiText("Feat509 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat509UiModel =
        Feat509UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat509UiModel =
        Feat509UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat509UiModel =
        Feat509UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat509Service(
    private val repository: Feat509Repository,
    private val uiMapper: Feat509UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat509UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat509UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat509UserItem1(val user: CoreUser, val label: String)
data class Feat509UserItem2(val user: CoreUser, val label: String)
data class Feat509UserItem3(val user: CoreUser, val label: String)
data class Feat509UserItem4(val user: CoreUser, val label: String)
data class Feat509UserItem5(val user: CoreUser, val label: String)
data class Feat509UserItem6(val user: CoreUser, val label: String)
data class Feat509UserItem7(val user: CoreUser, val label: String)
data class Feat509UserItem8(val user: CoreUser, val label: String)
data class Feat509UserItem9(val user: CoreUser, val label: String)
data class Feat509UserItem10(val user: CoreUser, val label: String)

data class Feat509StateBlock1(val state: Feat509UiModel, val checksum: Int)
data class Feat509StateBlock2(val state: Feat509UiModel, val checksum: Int)
data class Feat509StateBlock3(val state: Feat509UiModel, val checksum: Int)
data class Feat509StateBlock4(val state: Feat509UiModel, val checksum: Int)
data class Feat509StateBlock5(val state: Feat509UiModel, val checksum: Int)
data class Feat509StateBlock6(val state: Feat509UiModel, val checksum: Int)
data class Feat509StateBlock7(val state: Feat509UiModel, val checksum: Int)
data class Feat509StateBlock8(val state: Feat509UiModel, val checksum: Int)
data class Feat509StateBlock9(val state: Feat509UiModel, val checksum: Int)
data class Feat509StateBlock10(val state: Feat509UiModel, val checksum: Int)

fun buildFeat509UserItem(user: CoreUser, index: Int): Feat509UserItem1 {
    return Feat509UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat509StateBlock(model: Feat509UiModel): Feat509StateBlock1 {
    return Feat509StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat509UserSummary> {
    val list = java.util.ArrayList<Feat509UserSummary>(users.size)
    for (user in users) {
        list += Feat509UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat509UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat509UiModel {
    val summaries = (0 until count).map {
        Feat509UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat509UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat509UiModel> {
    val models = java.util.ArrayList<Feat509UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat509AnalyticsEvent1(val name: String, val value: String)
data class Feat509AnalyticsEvent2(val name: String, val value: String)
data class Feat509AnalyticsEvent3(val name: String, val value: String)
data class Feat509AnalyticsEvent4(val name: String, val value: String)
data class Feat509AnalyticsEvent5(val name: String, val value: String)
data class Feat509AnalyticsEvent6(val name: String, val value: String)
data class Feat509AnalyticsEvent7(val name: String, val value: String)
data class Feat509AnalyticsEvent8(val name: String, val value: String)
data class Feat509AnalyticsEvent9(val name: String, val value: String)
data class Feat509AnalyticsEvent10(val name: String, val value: String)

fun logFeat509Event1(event: Feat509AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat509Event2(event: Feat509AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat509Event3(event: Feat509AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat509Event4(event: Feat509AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat509Event5(event: Feat509AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat509Event6(event: Feat509AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat509Event7(event: Feat509AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat509Event8(event: Feat509AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat509Event9(event: Feat509AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat509Event10(event: Feat509AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat509Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat509Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat509Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat509Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat509Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat509Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat509Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat509Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat509Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat509Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat509(u: CoreUser): Feat509Projection1 =
    Feat509Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat509Projection1> {
    val list = java.util.ArrayList<Feat509Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat509(u)
    }
    return list
}
