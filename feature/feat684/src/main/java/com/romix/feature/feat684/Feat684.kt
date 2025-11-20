package com.romix.feature.feat684

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat684Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat684UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat684FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat684UserSummary
)

data class Feat684UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat684NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat684Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat684Config = Feat684Config()
) {

    fun loadSnapshot(userId: Long): Feat684NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat684NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat684UserSummary {
        return Feat684UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat684FeedItem> {
        val result = java.util.ArrayList<Feat684FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat684FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat684UiMapper {

    fun mapToUi(model: List<Feat684FeedItem>): Feat684UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat684UiModel(
            header = UiText("Feat684 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat684UiModel =
        Feat684UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat684UiModel =
        Feat684UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat684UiModel =
        Feat684UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat684Service(
    private val repository: Feat684Repository,
    private val uiMapper: Feat684UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat684UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat684UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat684UserItem1(val user: CoreUser, val label: String)
data class Feat684UserItem2(val user: CoreUser, val label: String)
data class Feat684UserItem3(val user: CoreUser, val label: String)
data class Feat684UserItem4(val user: CoreUser, val label: String)
data class Feat684UserItem5(val user: CoreUser, val label: String)
data class Feat684UserItem6(val user: CoreUser, val label: String)
data class Feat684UserItem7(val user: CoreUser, val label: String)
data class Feat684UserItem8(val user: CoreUser, val label: String)
data class Feat684UserItem9(val user: CoreUser, val label: String)
data class Feat684UserItem10(val user: CoreUser, val label: String)

data class Feat684StateBlock1(val state: Feat684UiModel, val checksum: Int)
data class Feat684StateBlock2(val state: Feat684UiModel, val checksum: Int)
data class Feat684StateBlock3(val state: Feat684UiModel, val checksum: Int)
data class Feat684StateBlock4(val state: Feat684UiModel, val checksum: Int)
data class Feat684StateBlock5(val state: Feat684UiModel, val checksum: Int)
data class Feat684StateBlock6(val state: Feat684UiModel, val checksum: Int)
data class Feat684StateBlock7(val state: Feat684UiModel, val checksum: Int)
data class Feat684StateBlock8(val state: Feat684UiModel, val checksum: Int)
data class Feat684StateBlock9(val state: Feat684UiModel, val checksum: Int)
data class Feat684StateBlock10(val state: Feat684UiModel, val checksum: Int)

fun buildFeat684UserItem(user: CoreUser, index: Int): Feat684UserItem1 {
    return Feat684UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat684StateBlock(model: Feat684UiModel): Feat684StateBlock1 {
    return Feat684StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat684UserSummary> {
    val list = java.util.ArrayList<Feat684UserSummary>(users.size)
    for (user in users) {
        list += Feat684UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat684UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat684UiModel {
    val summaries = (0 until count).map {
        Feat684UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat684UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat684UiModel> {
    val models = java.util.ArrayList<Feat684UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat684AnalyticsEvent1(val name: String, val value: String)
data class Feat684AnalyticsEvent2(val name: String, val value: String)
data class Feat684AnalyticsEvent3(val name: String, val value: String)
data class Feat684AnalyticsEvent4(val name: String, val value: String)
data class Feat684AnalyticsEvent5(val name: String, val value: String)
data class Feat684AnalyticsEvent6(val name: String, val value: String)
data class Feat684AnalyticsEvent7(val name: String, val value: String)
data class Feat684AnalyticsEvent8(val name: String, val value: String)
data class Feat684AnalyticsEvent9(val name: String, val value: String)
data class Feat684AnalyticsEvent10(val name: String, val value: String)

fun logFeat684Event1(event: Feat684AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat684Event2(event: Feat684AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat684Event3(event: Feat684AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat684Event4(event: Feat684AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat684Event5(event: Feat684AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat684Event6(event: Feat684AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat684Event7(event: Feat684AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat684Event8(event: Feat684AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat684Event9(event: Feat684AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat684Event10(event: Feat684AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat684Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat684Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat684Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat684Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat684Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat684Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat684Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat684Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat684Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat684Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat684(u: CoreUser): Feat684Projection1 =
    Feat684Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat684Projection1> {
    val list = java.util.ArrayList<Feat684Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat684(u)
    }
    return list
}
