package com.romix.feature.feat472

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat472Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat472UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat472FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat472UserSummary
)

data class Feat472UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat472NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat472Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat472Config = Feat472Config()
) {

    fun loadSnapshot(userId: Long): Feat472NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat472NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat472UserSummary {
        return Feat472UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat472FeedItem> {
        val result = java.util.ArrayList<Feat472FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat472FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat472UiMapper {

    fun mapToUi(model: List<Feat472FeedItem>): Feat472UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat472UiModel(
            header = UiText("Feat472 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat472UiModel =
        Feat472UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat472UiModel =
        Feat472UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat472UiModel =
        Feat472UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat472Service(
    private val repository: Feat472Repository,
    private val uiMapper: Feat472UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat472UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat472UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat472UserItem1(val user: CoreUser, val label: String)
data class Feat472UserItem2(val user: CoreUser, val label: String)
data class Feat472UserItem3(val user: CoreUser, val label: String)
data class Feat472UserItem4(val user: CoreUser, val label: String)
data class Feat472UserItem5(val user: CoreUser, val label: String)
data class Feat472UserItem6(val user: CoreUser, val label: String)
data class Feat472UserItem7(val user: CoreUser, val label: String)
data class Feat472UserItem8(val user: CoreUser, val label: String)
data class Feat472UserItem9(val user: CoreUser, val label: String)
data class Feat472UserItem10(val user: CoreUser, val label: String)

data class Feat472StateBlock1(val state: Feat472UiModel, val checksum: Int)
data class Feat472StateBlock2(val state: Feat472UiModel, val checksum: Int)
data class Feat472StateBlock3(val state: Feat472UiModel, val checksum: Int)
data class Feat472StateBlock4(val state: Feat472UiModel, val checksum: Int)
data class Feat472StateBlock5(val state: Feat472UiModel, val checksum: Int)
data class Feat472StateBlock6(val state: Feat472UiModel, val checksum: Int)
data class Feat472StateBlock7(val state: Feat472UiModel, val checksum: Int)
data class Feat472StateBlock8(val state: Feat472UiModel, val checksum: Int)
data class Feat472StateBlock9(val state: Feat472UiModel, val checksum: Int)
data class Feat472StateBlock10(val state: Feat472UiModel, val checksum: Int)

fun buildFeat472UserItem(user: CoreUser, index: Int): Feat472UserItem1 {
    return Feat472UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat472StateBlock(model: Feat472UiModel): Feat472StateBlock1 {
    return Feat472StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat472UserSummary> {
    val list = java.util.ArrayList<Feat472UserSummary>(users.size)
    for (user in users) {
        list += Feat472UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat472UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat472UiModel {
    val summaries = (0 until count).map {
        Feat472UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat472UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat472UiModel> {
    val models = java.util.ArrayList<Feat472UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat472AnalyticsEvent1(val name: String, val value: String)
data class Feat472AnalyticsEvent2(val name: String, val value: String)
data class Feat472AnalyticsEvent3(val name: String, val value: String)
data class Feat472AnalyticsEvent4(val name: String, val value: String)
data class Feat472AnalyticsEvent5(val name: String, val value: String)
data class Feat472AnalyticsEvent6(val name: String, val value: String)
data class Feat472AnalyticsEvent7(val name: String, val value: String)
data class Feat472AnalyticsEvent8(val name: String, val value: String)
data class Feat472AnalyticsEvent9(val name: String, val value: String)
data class Feat472AnalyticsEvent10(val name: String, val value: String)

fun logFeat472Event1(event: Feat472AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat472Event2(event: Feat472AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat472Event3(event: Feat472AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat472Event4(event: Feat472AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat472Event5(event: Feat472AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat472Event6(event: Feat472AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat472Event7(event: Feat472AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat472Event8(event: Feat472AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat472Event9(event: Feat472AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat472Event10(event: Feat472AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat472Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat472Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat472Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat472Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat472Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat472Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat472Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat472Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat472Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat472Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat472(u: CoreUser): Feat472Projection1 =
    Feat472Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat472Projection1> {
    val list = java.util.ArrayList<Feat472Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat472(u)
    }
    return list
}
