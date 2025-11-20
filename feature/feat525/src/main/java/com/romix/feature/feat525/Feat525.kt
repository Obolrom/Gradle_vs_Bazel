package com.romix.feature.feat525

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat525Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat525UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat525FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat525UserSummary
)

data class Feat525UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat525NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat525Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat525Config = Feat525Config()
) {

    fun loadSnapshot(userId: Long): Feat525NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat525NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat525UserSummary {
        return Feat525UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat525FeedItem> {
        val result = java.util.ArrayList<Feat525FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat525FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat525UiMapper {

    fun mapToUi(model: List<Feat525FeedItem>): Feat525UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat525UiModel(
            header = UiText("Feat525 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat525UiModel =
        Feat525UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat525UiModel =
        Feat525UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat525UiModel =
        Feat525UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat525Service(
    private val repository: Feat525Repository,
    private val uiMapper: Feat525UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat525UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat525UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat525UserItem1(val user: CoreUser, val label: String)
data class Feat525UserItem2(val user: CoreUser, val label: String)
data class Feat525UserItem3(val user: CoreUser, val label: String)
data class Feat525UserItem4(val user: CoreUser, val label: String)
data class Feat525UserItem5(val user: CoreUser, val label: String)
data class Feat525UserItem6(val user: CoreUser, val label: String)
data class Feat525UserItem7(val user: CoreUser, val label: String)
data class Feat525UserItem8(val user: CoreUser, val label: String)
data class Feat525UserItem9(val user: CoreUser, val label: String)
data class Feat525UserItem10(val user: CoreUser, val label: String)

data class Feat525StateBlock1(val state: Feat525UiModel, val checksum: Int)
data class Feat525StateBlock2(val state: Feat525UiModel, val checksum: Int)
data class Feat525StateBlock3(val state: Feat525UiModel, val checksum: Int)
data class Feat525StateBlock4(val state: Feat525UiModel, val checksum: Int)
data class Feat525StateBlock5(val state: Feat525UiModel, val checksum: Int)
data class Feat525StateBlock6(val state: Feat525UiModel, val checksum: Int)
data class Feat525StateBlock7(val state: Feat525UiModel, val checksum: Int)
data class Feat525StateBlock8(val state: Feat525UiModel, val checksum: Int)
data class Feat525StateBlock9(val state: Feat525UiModel, val checksum: Int)
data class Feat525StateBlock10(val state: Feat525UiModel, val checksum: Int)

fun buildFeat525UserItem(user: CoreUser, index: Int): Feat525UserItem1 {
    return Feat525UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat525StateBlock(model: Feat525UiModel): Feat525StateBlock1 {
    return Feat525StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat525UserSummary> {
    val list = java.util.ArrayList<Feat525UserSummary>(users.size)
    for (user in users) {
        list += Feat525UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat525UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat525UiModel {
    val summaries = (0 until count).map {
        Feat525UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat525UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat525UiModel> {
    val models = java.util.ArrayList<Feat525UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat525AnalyticsEvent1(val name: String, val value: String)
data class Feat525AnalyticsEvent2(val name: String, val value: String)
data class Feat525AnalyticsEvent3(val name: String, val value: String)
data class Feat525AnalyticsEvent4(val name: String, val value: String)
data class Feat525AnalyticsEvent5(val name: String, val value: String)
data class Feat525AnalyticsEvent6(val name: String, val value: String)
data class Feat525AnalyticsEvent7(val name: String, val value: String)
data class Feat525AnalyticsEvent8(val name: String, val value: String)
data class Feat525AnalyticsEvent9(val name: String, val value: String)
data class Feat525AnalyticsEvent10(val name: String, val value: String)

fun logFeat525Event1(event: Feat525AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat525Event2(event: Feat525AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat525Event3(event: Feat525AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat525Event4(event: Feat525AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat525Event5(event: Feat525AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat525Event6(event: Feat525AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat525Event7(event: Feat525AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat525Event8(event: Feat525AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat525Event9(event: Feat525AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat525Event10(event: Feat525AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat525Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat525Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat525Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat525Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat525Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat525Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat525Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat525Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat525Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat525Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat525(u: CoreUser): Feat525Projection1 =
    Feat525Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat525Projection1> {
    val list = java.util.ArrayList<Feat525Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat525(u)
    }
    return list
}
