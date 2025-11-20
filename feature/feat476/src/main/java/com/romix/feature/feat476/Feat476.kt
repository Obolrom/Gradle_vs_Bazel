package com.romix.feature.feat476

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat476Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat476UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat476FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat476UserSummary
)

data class Feat476UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat476NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat476Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat476Config = Feat476Config()
) {

    fun loadSnapshot(userId: Long): Feat476NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat476NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat476UserSummary {
        return Feat476UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat476FeedItem> {
        val result = java.util.ArrayList<Feat476FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat476FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat476UiMapper {

    fun mapToUi(model: List<Feat476FeedItem>): Feat476UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat476UiModel(
            header = UiText("Feat476 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat476UiModel =
        Feat476UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat476UiModel =
        Feat476UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat476UiModel =
        Feat476UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat476Service(
    private val repository: Feat476Repository,
    private val uiMapper: Feat476UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat476UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat476UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat476UserItem1(val user: CoreUser, val label: String)
data class Feat476UserItem2(val user: CoreUser, val label: String)
data class Feat476UserItem3(val user: CoreUser, val label: String)
data class Feat476UserItem4(val user: CoreUser, val label: String)
data class Feat476UserItem5(val user: CoreUser, val label: String)
data class Feat476UserItem6(val user: CoreUser, val label: String)
data class Feat476UserItem7(val user: CoreUser, val label: String)
data class Feat476UserItem8(val user: CoreUser, val label: String)
data class Feat476UserItem9(val user: CoreUser, val label: String)
data class Feat476UserItem10(val user: CoreUser, val label: String)

data class Feat476StateBlock1(val state: Feat476UiModel, val checksum: Int)
data class Feat476StateBlock2(val state: Feat476UiModel, val checksum: Int)
data class Feat476StateBlock3(val state: Feat476UiModel, val checksum: Int)
data class Feat476StateBlock4(val state: Feat476UiModel, val checksum: Int)
data class Feat476StateBlock5(val state: Feat476UiModel, val checksum: Int)
data class Feat476StateBlock6(val state: Feat476UiModel, val checksum: Int)
data class Feat476StateBlock7(val state: Feat476UiModel, val checksum: Int)
data class Feat476StateBlock8(val state: Feat476UiModel, val checksum: Int)
data class Feat476StateBlock9(val state: Feat476UiModel, val checksum: Int)
data class Feat476StateBlock10(val state: Feat476UiModel, val checksum: Int)

fun buildFeat476UserItem(user: CoreUser, index: Int): Feat476UserItem1 {
    return Feat476UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat476StateBlock(model: Feat476UiModel): Feat476StateBlock1 {
    return Feat476StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat476UserSummary> {
    val list = java.util.ArrayList<Feat476UserSummary>(users.size)
    for (user in users) {
        list += Feat476UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat476UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat476UiModel {
    val summaries = (0 until count).map {
        Feat476UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat476UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat476UiModel> {
    val models = java.util.ArrayList<Feat476UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat476AnalyticsEvent1(val name: String, val value: String)
data class Feat476AnalyticsEvent2(val name: String, val value: String)
data class Feat476AnalyticsEvent3(val name: String, val value: String)
data class Feat476AnalyticsEvent4(val name: String, val value: String)
data class Feat476AnalyticsEvent5(val name: String, val value: String)
data class Feat476AnalyticsEvent6(val name: String, val value: String)
data class Feat476AnalyticsEvent7(val name: String, val value: String)
data class Feat476AnalyticsEvent8(val name: String, val value: String)
data class Feat476AnalyticsEvent9(val name: String, val value: String)
data class Feat476AnalyticsEvent10(val name: String, val value: String)

fun logFeat476Event1(event: Feat476AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat476Event2(event: Feat476AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat476Event3(event: Feat476AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat476Event4(event: Feat476AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat476Event5(event: Feat476AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat476Event6(event: Feat476AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat476Event7(event: Feat476AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat476Event8(event: Feat476AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat476Event9(event: Feat476AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat476Event10(event: Feat476AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat476Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat476Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat476Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat476Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat476Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat476Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat476Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat476Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat476Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat476Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat476(u: CoreUser): Feat476Projection1 =
    Feat476Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat476Projection1> {
    val list = java.util.ArrayList<Feat476Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat476(u)
    }
    return list
}
