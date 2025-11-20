package com.romix.feature.feat655

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat655Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat655UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat655FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat655UserSummary
)

data class Feat655UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat655NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat655Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat655Config = Feat655Config()
) {

    fun loadSnapshot(userId: Long): Feat655NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat655NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat655UserSummary {
        return Feat655UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat655FeedItem> {
        val result = java.util.ArrayList<Feat655FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat655FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat655UiMapper {

    fun mapToUi(model: List<Feat655FeedItem>): Feat655UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat655UiModel(
            header = UiText("Feat655 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat655UiModel =
        Feat655UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat655UiModel =
        Feat655UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat655UiModel =
        Feat655UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat655Service(
    private val repository: Feat655Repository,
    private val uiMapper: Feat655UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat655UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat655UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat655UserItem1(val user: CoreUser, val label: String)
data class Feat655UserItem2(val user: CoreUser, val label: String)
data class Feat655UserItem3(val user: CoreUser, val label: String)
data class Feat655UserItem4(val user: CoreUser, val label: String)
data class Feat655UserItem5(val user: CoreUser, val label: String)
data class Feat655UserItem6(val user: CoreUser, val label: String)
data class Feat655UserItem7(val user: CoreUser, val label: String)
data class Feat655UserItem8(val user: CoreUser, val label: String)
data class Feat655UserItem9(val user: CoreUser, val label: String)
data class Feat655UserItem10(val user: CoreUser, val label: String)

data class Feat655StateBlock1(val state: Feat655UiModel, val checksum: Int)
data class Feat655StateBlock2(val state: Feat655UiModel, val checksum: Int)
data class Feat655StateBlock3(val state: Feat655UiModel, val checksum: Int)
data class Feat655StateBlock4(val state: Feat655UiModel, val checksum: Int)
data class Feat655StateBlock5(val state: Feat655UiModel, val checksum: Int)
data class Feat655StateBlock6(val state: Feat655UiModel, val checksum: Int)
data class Feat655StateBlock7(val state: Feat655UiModel, val checksum: Int)
data class Feat655StateBlock8(val state: Feat655UiModel, val checksum: Int)
data class Feat655StateBlock9(val state: Feat655UiModel, val checksum: Int)
data class Feat655StateBlock10(val state: Feat655UiModel, val checksum: Int)

fun buildFeat655UserItem(user: CoreUser, index: Int): Feat655UserItem1 {
    return Feat655UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat655StateBlock(model: Feat655UiModel): Feat655StateBlock1 {
    return Feat655StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat655UserSummary> {
    val list = java.util.ArrayList<Feat655UserSummary>(users.size)
    for (user in users) {
        list += Feat655UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat655UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat655UiModel {
    val summaries = (0 until count).map {
        Feat655UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat655UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat655UiModel> {
    val models = java.util.ArrayList<Feat655UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat655AnalyticsEvent1(val name: String, val value: String)
data class Feat655AnalyticsEvent2(val name: String, val value: String)
data class Feat655AnalyticsEvent3(val name: String, val value: String)
data class Feat655AnalyticsEvent4(val name: String, val value: String)
data class Feat655AnalyticsEvent5(val name: String, val value: String)
data class Feat655AnalyticsEvent6(val name: String, val value: String)
data class Feat655AnalyticsEvent7(val name: String, val value: String)
data class Feat655AnalyticsEvent8(val name: String, val value: String)
data class Feat655AnalyticsEvent9(val name: String, val value: String)
data class Feat655AnalyticsEvent10(val name: String, val value: String)

fun logFeat655Event1(event: Feat655AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat655Event2(event: Feat655AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat655Event3(event: Feat655AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat655Event4(event: Feat655AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat655Event5(event: Feat655AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat655Event6(event: Feat655AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat655Event7(event: Feat655AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat655Event8(event: Feat655AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat655Event9(event: Feat655AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat655Event10(event: Feat655AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat655Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat655Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat655Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat655Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat655Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat655Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat655Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat655Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat655Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat655Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat655(u: CoreUser): Feat655Projection1 =
    Feat655Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat655Projection1> {
    val list = java.util.ArrayList<Feat655Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat655(u)
    }
    return list
}
