package com.romix.feature.feat313

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat313Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat313UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat313FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat313UserSummary
)

data class Feat313UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat313NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat313Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat313Config = Feat313Config()
) {

    fun loadSnapshot(userId: Long): Feat313NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat313NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat313UserSummary {
        return Feat313UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat313FeedItem> {
        val result = java.util.ArrayList<Feat313FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat313FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat313UiMapper {

    fun mapToUi(model: List<Feat313FeedItem>): Feat313UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat313UiModel(
            header = UiText("Feat313 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat313UiModel =
        Feat313UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat313UiModel =
        Feat313UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat313UiModel =
        Feat313UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat313Service(
    private val repository: Feat313Repository,
    private val uiMapper: Feat313UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat313UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat313UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat313UserItem1(val user: CoreUser, val label: String)
data class Feat313UserItem2(val user: CoreUser, val label: String)
data class Feat313UserItem3(val user: CoreUser, val label: String)
data class Feat313UserItem4(val user: CoreUser, val label: String)
data class Feat313UserItem5(val user: CoreUser, val label: String)
data class Feat313UserItem6(val user: CoreUser, val label: String)
data class Feat313UserItem7(val user: CoreUser, val label: String)
data class Feat313UserItem8(val user: CoreUser, val label: String)
data class Feat313UserItem9(val user: CoreUser, val label: String)
data class Feat313UserItem10(val user: CoreUser, val label: String)

data class Feat313StateBlock1(val state: Feat313UiModel, val checksum: Int)
data class Feat313StateBlock2(val state: Feat313UiModel, val checksum: Int)
data class Feat313StateBlock3(val state: Feat313UiModel, val checksum: Int)
data class Feat313StateBlock4(val state: Feat313UiModel, val checksum: Int)
data class Feat313StateBlock5(val state: Feat313UiModel, val checksum: Int)
data class Feat313StateBlock6(val state: Feat313UiModel, val checksum: Int)
data class Feat313StateBlock7(val state: Feat313UiModel, val checksum: Int)
data class Feat313StateBlock8(val state: Feat313UiModel, val checksum: Int)
data class Feat313StateBlock9(val state: Feat313UiModel, val checksum: Int)
data class Feat313StateBlock10(val state: Feat313UiModel, val checksum: Int)

fun buildFeat313UserItem(user: CoreUser, index: Int): Feat313UserItem1 {
    return Feat313UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat313StateBlock(model: Feat313UiModel): Feat313StateBlock1 {
    return Feat313StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat313UserSummary> {
    val list = java.util.ArrayList<Feat313UserSummary>(users.size)
    for (user in users) {
        list += Feat313UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat313UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat313UiModel {
    val summaries = (0 until count).map {
        Feat313UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat313UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat313UiModel> {
    val models = java.util.ArrayList<Feat313UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat313AnalyticsEvent1(val name: String, val value: String)
data class Feat313AnalyticsEvent2(val name: String, val value: String)
data class Feat313AnalyticsEvent3(val name: String, val value: String)
data class Feat313AnalyticsEvent4(val name: String, val value: String)
data class Feat313AnalyticsEvent5(val name: String, val value: String)
data class Feat313AnalyticsEvent6(val name: String, val value: String)
data class Feat313AnalyticsEvent7(val name: String, val value: String)
data class Feat313AnalyticsEvent8(val name: String, val value: String)
data class Feat313AnalyticsEvent9(val name: String, val value: String)
data class Feat313AnalyticsEvent10(val name: String, val value: String)

fun logFeat313Event1(event: Feat313AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat313Event2(event: Feat313AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat313Event3(event: Feat313AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat313Event4(event: Feat313AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat313Event5(event: Feat313AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat313Event6(event: Feat313AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat313Event7(event: Feat313AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat313Event8(event: Feat313AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat313Event9(event: Feat313AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat313Event10(event: Feat313AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat313Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat313Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat313Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat313Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat313Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat313Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat313Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat313Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat313Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat313Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat313(u: CoreUser): Feat313Projection1 =
    Feat313Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat313Projection1> {
    val list = java.util.ArrayList<Feat313Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat313(u)
    }
    return list
}
