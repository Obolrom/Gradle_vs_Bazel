package com.romix.feature.feat18

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat18Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat18UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat18FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat18UserSummary
)

data class Feat18UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat18NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat18Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat18Config = Feat18Config()
) {

    fun loadSnapshot(userId: Long): Feat18NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat18NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat18UserSummary {
        return Feat18UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat18FeedItem> {
        val result = java.util.ArrayList<Feat18FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat18FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat18UiMapper {

    fun mapToUi(model: List<Feat18FeedItem>): Feat18UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat18UiModel(
            header = UiText("Feat18 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat18UiModel =
        Feat18UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat18UiModel =
        Feat18UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat18UiModel =
        Feat18UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat18Service(
    private val repository: Feat18Repository,
    private val uiMapper: Feat18UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat18UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat18UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat18UserItem1(val user: CoreUser, val label: String)
data class Feat18UserItem2(val user: CoreUser, val label: String)
data class Feat18UserItem3(val user: CoreUser, val label: String)
data class Feat18UserItem4(val user: CoreUser, val label: String)
data class Feat18UserItem5(val user: CoreUser, val label: String)
data class Feat18UserItem6(val user: CoreUser, val label: String)
data class Feat18UserItem7(val user: CoreUser, val label: String)
data class Feat18UserItem8(val user: CoreUser, val label: String)
data class Feat18UserItem9(val user: CoreUser, val label: String)
data class Feat18UserItem10(val user: CoreUser, val label: String)

data class Feat18StateBlock1(val state: Feat18UiModel, val checksum: Int)
data class Feat18StateBlock2(val state: Feat18UiModel, val checksum: Int)
data class Feat18StateBlock3(val state: Feat18UiModel, val checksum: Int)
data class Feat18StateBlock4(val state: Feat18UiModel, val checksum: Int)
data class Feat18StateBlock5(val state: Feat18UiModel, val checksum: Int)
data class Feat18StateBlock6(val state: Feat18UiModel, val checksum: Int)
data class Feat18StateBlock7(val state: Feat18UiModel, val checksum: Int)
data class Feat18StateBlock8(val state: Feat18UiModel, val checksum: Int)
data class Feat18StateBlock9(val state: Feat18UiModel, val checksum: Int)
data class Feat18StateBlock10(val state: Feat18UiModel, val checksum: Int)

fun buildFeat18UserItem(user: CoreUser, index: Int): Feat18UserItem1 {
    return Feat18UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat18StateBlock(model: Feat18UiModel): Feat18StateBlock1 {
    return Feat18StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat18UserSummary> {
    val list = java.util.ArrayList<Feat18UserSummary>(users.size)
    for (user in users) {
        list += Feat18UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat18UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat18UiModel {
    val summaries = (0 until count).map {
        Feat18UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat18UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat18UiModel> {
    val models = java.util.ArrayList<Feat18UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat18AnalyticsEvent1(val name: String, val value: String)
data class Feat18AnalyticsEvent2(val name: String, val value: String)
data class Feat18AnalyticsEvent3(val name: String, val value: String)
data class Feat18AnalyticsEvent4(val name: String, val value: String)
data class Feat18AnalyticsEvent5(val name: String, val value: String)
data class Feat18AnalyticsEvent6(val name: String, val value: String)
data class Feat18AnalyticsEvent7(val name: String, val value: String)
data class Feat18AnalyticsEvent8(val name: String, val value: String)
data class Feat18AnalyticsEvent9(val name: String, val value: String)
data class Feat18AnalyticsEvent10(val name: String, val value: String)

fun logFeat18Event1(event: Feat18AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat18Event2(event: Feat18AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat18Event3(event: Feat18AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat18Event4(event: Feat18AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat18Event5(event: Feat18AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat18Event6(event: Feat18AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat18Event7(event: Feat18AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat18Event8(event: Feat18AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat18Event9(event: Feat18AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat18Event10(event: Feat18AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat18Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat18Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat18Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat18Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat18Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat18Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat18Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat18Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat18Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat18Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat18(u: CoreUser): Feat18Projection1 =
    Feat18Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat18Projection1> {
    val list = java.util.ArrayList<Feat18Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat18(u)
    }
    return list
}
