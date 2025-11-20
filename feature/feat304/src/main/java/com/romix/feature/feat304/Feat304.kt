package com.romix.feature.feat304

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat304Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat304UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat304FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat304UserSummary
)

data class Feat304UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat304NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat304Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat304Config = Feat304Config()
) {

    fun loadSnapshot(userId: Long): Feat304NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat304NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat304UserSummary {
        return Feat304UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat304FeedItem> {
        val result = java.util.ArrayList<Feat304FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat304FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat304UiMapper {

    fun mapToUi(model: List<Feat304FeedItem>): Feat304UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat304UiModel(
            header = UiText("Feat304 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat304UiModel =
        Feat304UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat304UiModel =
        Feat304UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat304UiModel =
        Feat304UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat304Service(
    private val repository: Feat304Repository,
    private val uiMapper: Feat304UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat304UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat304UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat304UserItem1(val user: CoreUser, val label: String)
data class Feat304UserItem2(val user: CoreUser, val label: String)
data class Feat304UserItem3(val user: CoreUser, val label: String)
data class Feat304UserItem4(val user: CoreUser, val label: String)
data class Feat304UserItem5(val user: CoreUser, val label: String)
data class Feat304UserItem6(val user: CoreUser, val label: String)
data class Feat304UserItem7(val user: CoreUser, val label: String)
data class Feat304UserItem8(val user: CoreUser, val label: String)
data class Feat304UserItem9(val user: CoreUser, val label: String)
data class Feat304UserItem10(val user: CoreUser, val label: String)

data class Feat304StateBlock1(val state: Feat304UiModel, val checksum: Int)
data class Feat304StateBlock2(val state: Feat304UiModel, val checksum: Int)
data class Feat304StateBlock3(val state: Feat304UiModel, val checksum: Int)
data class Feat304StateBlock4(val state: Feat304UiModel, val checksum: Int)
data class Feat304StateBlock5(val state: Feat304UiModel, val checksum: Int)
data class Feat304StateBlock6(val state: Feat304UiModel, val checksum: Int)
data class Feat304StateBlock7(val state: Feat304UiModel, val checksum: Int)
data class Feat304StateBlock8(val state: Feat304UiModel, val checksum: Int)
data class Feat304StateBlock9(val state: Feat304UiModel, val checksum: Int)
data class Feat304StateBlock10(val state: Feat304UiModel, val checksum: Int)

fun buildFeat304UserItem(user: CoreUser, index: Int): Feat304UserItem1 {
    return Feat304UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat304StateBlock(model: Feat304UiModel): Feat304StateBlock1 {
    return Feat304StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat304UserSummary> {
    val list = java.util.ArrayList<Feat304UserSummary>(users.size)
    for (user in users) {
        list += Feat304UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat304UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat304UiModel {
    val summaries = (0 until count).map {
        Feat304UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat304UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat304UiModel> {
    val models = java.util.ArrayList<Feat304UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat304AnalyticsEvent1(val name: String, val value: String)
data class Feat304AnalyticsEvent2(val name: String, val value: String)
data class Feat304AnalyticsEvent3(val name: String, val value: String)
data class Feat304AnalyticsEvent4(val name: String, val value: String)
data class Feat304AnalyticsEvent5(val name: String, val value: String)
data class Feat304AnalyticsEvent6(val name: String, val value: String)
data class Feat304AnalyticsEvent7(val name: String, val value: String)
data class Feat304AnalyticsEvent8(val name: String, val value: String)
data class Feat304AnalyticsEvent9(val name: String, val value: String)
data class Feat304AnalyticsEvent10(val name: String, val value: String)

fun logFeat304Event1(event: Feat304AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat304Event2(event: Feat304AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat304Event3(event: Feat304AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat304Event4(event: Feat304AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat304Event5(event: Feat304AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat304Event6(event: Feat304AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat304Event7(event: Feat304AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat304Event8(event: Feat304AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat304Event9(event: Feat304AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat304Event10(event: Feat304AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat304Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat304Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat304Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat304Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat304Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat304Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat304Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat304Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat304Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat304Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat304(u: CoreUser): Feat304Projection1 =
    Feat304Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat304Projection1> {
    val list = java.util.ArrayList<Feat304Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat304(u)
    }
    return list
}
