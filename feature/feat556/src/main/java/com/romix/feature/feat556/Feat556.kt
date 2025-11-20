package com.romix.feature.feat556

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat556Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat556UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat556FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat556UserSummary
)

data class Feat556UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat556NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat556Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat556Config = Feat556Config()
) {

    fun loadSnapshot(userId: Long): Feat556NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat556NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat556UserSummary {
        return Feat556UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat556FeedItem> {
        val result = java.util.ArrayList<Feat556FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat556FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat556UiMapper {

    fun mapToUi(model: List<Feat556FeedItem>): Feat556UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat556UiModel(
            header = UiText("Feat556 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat556UiModel =
        Feat556UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat556UiModel =
        Feat556UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat556UiModel =
        Feat556UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat556Service(
    private val repository: Feat556Repository,
    private val uiMapper: Feat556UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat556UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat556UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat556UserItem1(val user: CoreUser, val label: String)
data class Feat556UserItem2(val user: CoreUser, val label: String)
data class Feat556UserItem3(val user: CoreUser, val label: String)
data class Feat556UserItem4(val user: CoreUser, val label: String)
data class Feat556UserItem5(val user: CoreUser, val label: String)
data class Feat556UserItem6(val user: CoreUser, val label: String)
data class Feat556UserItem7(val user: CoreUser, val label: String)
data class Feat556UserItem8(val user: CoreUser, val label: String)
data class Feat556UserItem9(val user: CoreUser, val label: String)
data class Feat556UserItem10(val user: CoreUser, val label: String)

data class Feat556StateBlock1(val state: Feat556UiModel, val checksum: Int)
data class Feat556StateBlock2(val state: Feat556UiModel, val checksum: Int)
data class Feat556StateBlock3(val state: Feat556UiModel, val checksum: Int)
data class Feat556StateBlock4(val state: Feat556UiModel, val checksum: Int)
data class Feat556StateBlock5(val state: Feat556UiModel, val checksum: Int)
data class Feat556StateBlock6(val state: Feat556UiModel, val checksum: Int)
data class Feat556StateBlock7(val state: Feat556UiModel, val checksum: Int)
data class Feat556StateBlock8(val state: Feat556UiModel, val checksum: Int)
data class Feat556StateBlock9(val state: Feat556UiModel, val checksum: Int)
data class Feat556StateBlock10(val state: Feat556UiModel, val checksum: Int)

fun buildFeat556UserItem(user: CoreUser, index: Int): Feat556UserItem1 {
    return Feat556UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat556StateBlock(model: Feat556UiModel): Feat556StateBlock1 {
    return Feat556StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat556UserSummary> {
    val list = java.util.ArrayList<Feat556UserSummary>(users.size)
    for (user in users) {
        list += Feat556UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat556UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat556UiModel {
    val summaries = (0 until count).map {
        Feat556UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat556UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat556UiModel> {
    val models = java.util.ArrayList<Feat556UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat556AnalyticsEvent1(val name: String, val value: String)
data class Feat556AnalyticsEvent2(val name: String, val value: String)
data class Feat556AnalyticsEvent3(val name: String, val value: String)
data class Feat556AnalyticsEvent4(val name: String, val value: String)
data class Feat556AnalyticsEvent5(val name: String, val value: String)
data class Feat556AnalyticsEvent6(val name: String, val value: String)
data class Feat556AnalyticsEvent7(val name: String, val value: String)
data class Feat556AnalyticsEvent8(val name: String, val value: String)
data class Feat556AnalyticsEvent9(val name: String, val value: String)
data class Feat556AnalyticsEvent10(val name: String, val value: String)

fun logFeat556Event1(event: Feat556AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat556Event2(event: Feat556AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat556Event3(event: Feat556AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat556Event4(event: Feat556AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat556Event5(event: Feat556AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat556Event6(event: Feat556AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat556Event7(event: Feat556AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat556Event8(event: Feat556AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat556Event9(event: Feat556AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat556Event10(event: Feat556AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat556Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat556Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat556Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat556Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat556Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat556Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat556Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat556Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat556Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat556Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat556(u: CoreUser): Feat556Projection1 =
    Feat556Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat556Projection1> {
    val list = java.util.ArrayList<Feat556Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat556(u)
    }
    return list
}
