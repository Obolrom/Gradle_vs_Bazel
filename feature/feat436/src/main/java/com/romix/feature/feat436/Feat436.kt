package com.romix.feature.feat436

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat436Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat436UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat436FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat436UserSummary
)

data class Feat436UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat436NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat436Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat436Config = Feat436Config()
) {

    fun loadSnapshot(userId: Long): Feat436NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat436NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat436UserSummary {
        return Feat436UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat436FeedItem> {
        val result = java.util.ArrayList<Feat436FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat436FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat436UiMapper {

    fun mapToUi(model: List<Feat436FeedItem>): Feat436UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat436UiModel(
            header = UiText("Feat436 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat436UiModel =
        Feat436UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat436UiModel =
        Feat436UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat436UiModel =
        Feat436UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat436Service(
    private val repository: Feat436Repository,
    private val uiMapper: Feat436UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat436UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat436UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat436UserItem1(val user: CoreUser, val label: String)
data class Feat436UserItem2(val user: CoreUser, val label: String)
data class Feat436UserItem3(val user: CoreUser, val label: String)
data class Feat436UserItem4(val user: CoreUser, val label: String)
data class Feat436UserItem5(val user: CoreUser, val label: String)
data class Feat436UserItem6(val user: CoreUser, val label: String)
data class Feat436UserItem7(val user: CoreUser, val label: String)
data class Feat436UserItem8(val user: CoreUser, val label: String)
data class Feat436UserItem9(val user: CoreUser, val label: String)
data class Feat436UserItem10(val user: CoreUser, val label: String)

data class Feat436StateBlock1(val state: Feat436UiModel, val checksum: Int)
data class Feat436StateBlock2(val state: Feat436UiModel, val checksum: Int)
data class Feat436StateBlock3(val state: Feat436UiModel, val checksum: Int)
data class Feat436StateBlock4(val state: Feat436UiModel, val checksum: Int)
data class Feat436StateBlock5(val state: Feat436UiModel, val checksum: Int)
data class Feat436StateBlock6(val state: Feat436UiModel, val checksum: Int)
data class Feat436StateBlock7(val state: Feat436UiModel, val checksum: Int)
data class Feat436StateBlock8(val state: Feat436UiModel, val checksum: Int)
data class Feat436StateBlock9(val state: Feat436UiModel, val checksum: Int)
data class Feat436StateBlock10(val state: Feat436UiModel, val checksum: Int)

fun buildFeat436UserItem(user: CoreUser, index: Int): Feat436UserItem1 {
    return Feat436UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat436StateBlock(model: Feat436UiModel): Feat436StateBlock1 {
    return Feat436StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat436UserSummary> {
    val list = java.util.ArrayList<Feat436UserSummary>(users.size)
    for (user in users) {
        list += Feat436UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat436UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat436UiModel {
    val summaries = (0 until count).map {
        Feat436UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat436UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat436UiModel> {
    val models = java.util.ArrayList<Feat436UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat436AnalyticsEvent1(val name: String, val value: String)
data class Feat436AnalyticsEvent2(val name: String, val value: String)
data class Feat436AnalyticsEvent3(val name: String, val value: String)
data class Feat436AnalyticsEvent4(val name: String, val value: String)
data class Feat436AnalyticsEvent5(val name: String, val value: String)
data class Feat436AnalyticsEvent6(val name: String, val value: String)
data class Feat436AnalyticsEvent7(val name: String, val value: String)
data class Feat436AnalyticsEvent8(val name: String, val value: String)
data class Feat436AnalyticsEvent9(val name: String, val value: String)
data class Feat436AnalyticsEvent10(val name: String, val value: String)

fun logFeat436Event1(event: Feat436AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat436Event2(event: Feat436AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat436Event3(event: Feat436AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat436Event4(event: Feat436AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat436Event5(event: Feat436AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat436Event6(event: Feat436AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat436Event7(event: Feat436AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat436Event8(event: Feat436AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat436Event9(event: Feat436AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat436Event10(event: Feat436AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat436Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat436Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat436Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat436Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat436Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat436Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat436Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat436Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat436Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat436Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat436(u: CoreUser): Feat436Projection1 =
    Feat436Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat436Projection1> {
    val list = java.util.ArrayList<Feat436Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat436(u)
    }
    return list
}
