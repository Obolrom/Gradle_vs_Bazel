package com.romix.feature.feat236

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat236Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat236UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat236FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat236UserSummary
)

data class Feat236UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat236NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat236Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat236Config = Feat236Config()
) {

    fun loadSnapshot(userId: Long): Feat236NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat236NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat236UserSummary {
        return Feat236UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat236FeedItem> {
        val result = java.util.ArrayList<Feat236FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat236FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat236UiMapper {

    fun mapToUi(model: List<Feat236FeedItem>): Feat236UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat236UiModel(
            header = UiText("Feat236 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat236UiModel =
        Feat236UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat236UiModel =
        Feat236UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat236UiModel =
        Feat236UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat236Service(
    private val repository: Feat236Repository,
    private val uiMapper: Feat236UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat236UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat236UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat236UserItem1(val user: CoreUser, val label: String)
data class Feat236UserItem2(val user: CoreUser, val label: String)
data class Feat236UserItem3(val user: CoreUser, val label: String)
data class Feat236UserItem4(val user: CoreUser, val label: String)
data class Feat236UserItem5(val user: CoreUser, val label: String)
data class Feat236UserItem6(val user: CoreUser, val label: String)
data class Feat236UserItem7(val user: CoreUser, val label: String)
data class Feat236UserItem8(val user: CoreUser, val label: String)
data class Feat236UserItem9(val user: CoreUser, val label: String)
data class Feat236UserItem10(val user: CoreUser, val label: String)

data class Feat236StateBlock1(val state: Feat236UiModel, val checksum: Int)
data class Feat236StateBlock2(val state: Feat236UiModel, val checksum: Int)
data class Feat236StateBlock3(val state: Feat236UiModel, val checksum: Int)
data class Feat236StateBlock4(val state: Feat236UiModel, val checksum: Int)
data class Feat236StateBlock5(val state: Feat236UiModel, val checksum: Int)
data class Feat236StateBlock6(val state: Feat236UiModel, val checksum: Int)
data class Feat236StateBlock7(val state: Feat236UiModel, val checksum: Int)
data class Feat236StateBlock8(val state: Feat236UiModel, val checksum: Int)
data class Feat236StateBlock9(val state: Feat236UiModel, val checksum: Int)
data class Feat236StateBlock10(val state: Feat236UiModel, val checksum: Int)

fun buildFeat236UserItem(user: CoreUser, index: Int): Feat236UserItem1 {
    return Feat236UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat236StateBlock(model: Feat236UiModel): Feat236StateBlock1 {
    return Feat236StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat236UserSummary> {
    val list = java.util.ArrayList<Feat236UserSummary>(users.size)
    for (user in users) {
        list += Feat236UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat236UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat236UiModel {
    val summaries = (0 until count).map {
        Feat236UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat236UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat236UiModel> {
    val models = java.util.ArrayList<Feat236UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat236AnalyticsEvent1(val name: String, val value: String)
data class Feat236AnalyticsEvent2(val name: String, val value: String)
data class Feat236AnalyticsEvent3(val name: String, val value: String)
data class Feat236AnalyticsEvent4(val name: String, val value: String)
data class Feat236AnalyticsEvent5(val name: String, val value: String)
data class Feat236AnalyticsEvent6(val name: String, val value: String)
data class Feat236AnalyticsEvent7(val name: String, val value: String)
data class Feat236AnalyticsEvent8(val name: String, val value: String)
data class Feat236AnalyticsEvent9(val name: String, val value: String)
data class Feat236AnalyticsEvent10(val name: String, val value: String)

fun logFeat236Event1(event: Feat236AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat236Event2(event: Feat236AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat236Event3(event: Feat236AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat236Event4(event: Feat236AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat236Event5(event: Feat236AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat236Event6(event: Feat236AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat236Event7(event: Feat236AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat236Event8(event: Feat236AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat236Event9(event: Feat236AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat236Event10(event: Feat236AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat236Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat236Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat236Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat236Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat236Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat236Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat236Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat236Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat236Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat236Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat236(u: CoreUser): Feat236Projection1 =
    Feat236Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat236Projection1> {
    val list = java.util.ArrayList<Feat236Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat236(u)
    }
    return list
}
