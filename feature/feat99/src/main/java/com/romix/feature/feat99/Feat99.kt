package com.romix.feature.feat99

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat99Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat99UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat99FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat99UserSummary
)

data class Feat99UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat99NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat99Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat99Config = Feat99Config()
) {

    fun loadSnapshot(userId: Long): Feat99NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat99NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat99UserSummary {
        return Feat99UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat99FeedItem> {
        val result = java.util.ArrayList<Feat99FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat99FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat99UiMapper {

    fun mapToUi(model: List<Feat99FeedItem>): Feat99UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat99UiModel(
            header = UiText("Feat99 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat99UiModel =
        Feat99UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat99UiModel =
        Feat99UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat99UiModel =
        Feat99UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat99Service(
    private val repository: Feat99Repository,
    private val uiMapper: Feat99UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat99UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat99UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat99UserItem1(val user: CoreUser, val label: String)
data class Feat99UserItem2(val user: CoreUser, val label: String)
data class Feat99UserItem3(val user: CoreUser, val label: String)
data class Feat99UserItem4(val user: CoreUser, val label: String)
data class Feat99UserItem5(val user: CoreUser, val label: String)
data class Feat99UserItem6(val user: CoreUser, val label: String)
data class Feat99UserItem7(val user: CoreUser, val label: String)
data class Feat99UserItem8(val user: CoreUser, val label: String)
data class Feat99UserItem9(val user: CoreUser, val label: String)
data class Feat99UserItem10(val user: CoreUser, val label: String)

data class Feat99StateBlock1(val state: Feat99UiModel, val checksum: Int)
data class Feat99StateBlock2(val state: Feat99UiModel, val checksum: Int)
data class Feat99StateBlock3(val state: Feat99UiModel, val checksum: Int)
data class Feat99StateBlock4(val state: Feat99UiModel, val checksum: Int)
data class Feat99StateBlock5(val state: Feat99UiModel, val checksum: Int)
data class Feat99StateBlock6(val state: Feat99UiModel, val checksum: Int)
data class Feat99StateBlock7(val state: Feat99UiModel, val checksum: Int)
data class Feat99StateBlock8(val state: Feat99UiModel, val checksum: Int)
data class Feat99StateBlock9(val state: Feat99UiModel, val checksum: Int)
data class Feat99StateBlock10(val state: Feat99UiModel, val checksum: Int)

fun buildFeat99UserItem(user: CoreUser, index: Int): Feat99UserItem1 {
    return Feat99UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat99StateBlock(model: Feat99UiModel): Feat99StateBlock1 {
    return Feat99StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat99UserSummary> {
    val list = java.util.ArrayList<Feat99UserSummary>(users.size)
    for (user in users) {
        list += Feat99UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat99UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat99UiModel {
    val summaries = (0 until count).map {
        Feat99UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat99UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat99UiModel> {
    val models = java.util.ArrayList<Feat99UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat99AnalyticsEvent1(val name: String, val value: String)
data class Feat99AnalyticsEvent2(val name: String, val value: String)
data class Feat99AnalyticsEvent3(val name: String, val value: String)
data class Feat99AnalyticsEvent4(val name: String, val value: String)
data class Feat99AnalyticsEvent5(val name: String, val value: String)
data class Feat99AnalyticsEvent6(val name: String, val value: String)
data class Feat99AnalyticsEvent7(val name: String, val value: String)
data class Feat99AnalyticsEvent8(val name: String, val value: String)
data class Feat99AnalyticsEvent9(val name: String, val value: String)
data class Feat99AnalyticsEvent10(val name: String, val value: String)

fun logFeat99Event1(event: Feat99AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat99Event2(event: Feat99AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat99Event3(event: Feat99AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat99Event4(event: Feat99AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat99Event5(event: Feat99AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat99Event6(event: Feat99AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat99Event7(event: Feat99AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat99Event8(event: Feat99AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat99Event9(event: Feat99AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat99Event10(event: Feat99AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat99Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat99Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat99Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat99Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat99Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat99Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat99Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat99Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat99Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat99Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat99(u: CoreUser): Feat99Projection1 =
    Feat99Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat99Projection1> {
    val list = java.util.ArrayList<Feat99Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat99(u)
    }
    return list
}
