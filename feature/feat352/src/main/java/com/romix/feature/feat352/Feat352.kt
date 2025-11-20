package com.romix.feature.feat352

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat352Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat352UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat352FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat352UserSummary
)

data class Feat352UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat352NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat352Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat352Config = Feat352Config()
) {

    fun loadSnapshot(userId: Long): Feat352NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat352NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat352UserSummary {
        return Feat352UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat352FeedItem> {
        val result = java.util.ArrayList<Feat352FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat352FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat352UiMapper {

    fun mapToUi(model: List<Feat352FeedItem>): Feat352UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat352UiModel(
            header = UiText("Feat352 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat352UiModel =
        Feat352UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat352UiModel =
        Feat352UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat352UiModel =
        Feat352UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat352Service(
    private val repository: Feat352Repository,
    private val uiMapper: Feat352UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat352UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat352UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat352UserItem1(val user: CoreUser, val label: String)
data class Feat352UserItem2(val user: CoreUser, val label: String)
data class Feat352UserItem3(val user: CoreUser, val label: String)
data class Feat352UserItem4(val user: CoreUser, val label: String)
data class Feat352UserItem5(val user: CoreUser, val label: String)
data class Feat352UserItem6(val user: CoreUser, val label: String)
data class Feat352UserItem7(val user: CoreUser, val label: String)
data class Feat352UserItem8(val user: CoreUser, val label: String)
data class Feat352UserItem9(val user: CoreUser, val label: String)
data class Feat352UserItem10(val user: CoreUser, val label: String)

data class Feat352StateBlock1(val state: Feat352UiModel, val checksum: Int)
data class Feat352StateBlock2(val state: Feat352UiModel, val checksum: Int)
data class Feat352StateBlock3(val state: Feat352UiModel, val checksum: Int)
data class Feat352StateBlock4(val state: Feat352UiModel, val checksum: Int)
data class Feat352StateBlock5(val state: Feat352UiModel, val checksum: Int)
data class Feat352StateBlock6(val state: Feat352UiModel, val checksum: Int)
data class Feat352StateBlock7(val state: Feat352UiModel, val checksum: Int)
data class Feat352StateBlock8(val state: Feat352UiModel, val checksum: Int)
data class Feat352StateBlock9(val state: Feat352UiModel, val checksum: Int)
data class Feat352StateBlock10(val state: Feat352UiModel, val checksum: Int)

fun buildFeat352UserItem(user: CoreUser, index: Int): Feat352UserItem1 {
    return Feat352UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat352StateBlock(model: Feat352UiModel): Feat352StateBlock1 {
    return Feat352StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat352UserSummary> {
    val list = java.util.ArrayList<Feat352UserSummary>(users.size)
    for (user in users) {
        list += Feat352UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat352UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat352UiModel {
    val summaries = (0 until count).map {
        Feat352UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat352UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat352UiModel> {
    val models = java.util.ArrayList<Feat352UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat352AnalyticsEvent1(val name: String, val value: String)
data class Feat352AnalyticsEvent2(val name: String, val value: String)
data class Feat352AnalyticsEvent3(val name: String, val value: String)
data class Feat352AnalyticsEvent4(val name: String, val value: String)
data class Feat352AnalyticsEvent5(val name: String, val value: String)
data class Feat352AnalyticsEvent6(val name: String, val value: String)
data class Feat352AnalyticsEvent7(val name: String, val value: String)
data class Feat352AnalyticsEvent8(val name: String, val value: String)
data class Feat352AnalyticsEvent9(val name: String, val value: String)
data class Feat352AnalyticsEvent10(val name: String, val value: String)

fun logFeat352Event1(event: Feat352AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat352Event2(event: Feat352AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat352Event3(event: Feat352AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat352Event4(event: Feat352AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat352Event5(event: Feat352AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat352Event6(event: Feat352AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat352Event7(event: Feat352AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat352Event8(event: Feat352AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat352Event9(event: Feat352AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat352Event10(event: Feat352AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat352Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat352Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat352Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat352Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat352Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat352Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat352Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat352Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat352Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat352Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat352(u: CoreUser): Feat352Projection1 =
    Feat352Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat352Projection1> {
    val list = java.util.ArrayList<Feat352Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat352(u)
    }
    return list
}
