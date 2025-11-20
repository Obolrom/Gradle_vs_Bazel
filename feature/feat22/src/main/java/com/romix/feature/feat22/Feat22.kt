package com.romix.feature.feat22

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat22Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat22UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat22FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat22UserSummary
)

data class Feat22UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat22NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat22Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat22Config = Feat22Config()
) {

    fun loadSnapshot(userId: Long): Feat22NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat22NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat22UserSummary {
        return Feat22UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat22FeedItem> {
        val result = java.util.ArrayList<Feat22FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat22FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat22UiMapper {

    fun mapToUi(model: List<Feat22FeedItem>): Feat22UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat22UiModel(
            header = UiText("Feat22 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat22UiModel =
        Feat22UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat22UiModel =
        Feat22UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat22UiModel =
        Feat22UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat22Service(
    private val repository: Feat22Repository,
    private val uiMapper: Feat22UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat22UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat22UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat22UserItem1(val user: CoreUser, val label: String)
data class Feat22UserItem2(val user: CoreUser, val label: String)
data class Feat22UserItem3(val user: CoreUser, val label: String)
data class Feat22UserItem4(val user: CoreUser, val label: String)
data class Feat22UserItem5(val user: CoreUser, val label: String)
data class Feat22UserItem6(val user: CoreUser, val label: String)
data class Feat22UserItem7(val user: CoreUser, val label: String)
data class Feat22UserItem8(val user: CoreUser, val label: String)
data class Feat22UserItem9(val user: CoreUser, val label: String)
data class Feat22UserItem10(val user: CoreUser, val label: String)

data class Feat22StateBlock1(val state: Feat22UiModel, val checksum: Int)
data class Feat22StateBlock2(val state: Feat22UiModel, val checksum: Int)
data class Feat22StateBlock3(val state: Feat22UiModel, val checksum: Int)
data class Feat22StateBlock4(val state: Feat22UiModel, val checksum: Int)
data class Feat22StateBlock5(val state: Feat22UiModel, val checksum: Int)
data class Feat22StateBlock6(val state: Feat22UiModel, val checksum: Int)
data class Feat22StateBlock7(val state: Feat22UiModel, val checksum: Int)
data class Feat22StateBlock8(val state: Feat22UiModel, val checksum: Int)
data class Feat22StateBlock9(val state: Feat22UiModel, val checksum: Int)
data class Feat22StateBlock10(val state: Feat22UiModel, val checksum: Int)

fun buildFeat22UserItem(user: CoreUser, index: Int): Feat22UserItem1 {
    return Feat22UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat22StateBlock(model: Feat22UiModel): Feat22StateBlock1 {
    return Feat22StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat22UserSummary> {
    val list = java.util.ArrayList<Feat22UserSummary>(users.size)
    for (user in users) {
        list += Feat22UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat22UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat22UiModel {
    val summaries = (0 until count).map {
        Feat22UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat22UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat22UiModel> {
    val models = java.util.ArrayList<Feat22UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat22AnalyticsEvent1(val name: String, val value: String)
data class Feat22AnalyticsEvent2(val name: String, val value: String)
data class Feat22AnalyticsEvent3(val name: String, val value: String)
data class Feat22AnalyticsEvent4(val name: String, val value: String)
data class Feat22AnalyticsEvent5(val name: String, val value: String)
data class Feat22AnalyticsEvent6(val name: String, val value: String)
data class Feat22AnalyticsEvent7(val name: String, val value: String)
data class Feat22AnalyticsEvent8(val name: String, val value: String)
data class Feat22AnalyticsEvent9(val name: String, val value: String)
data class Feat22AnalyticsEvent10(val name: String, val value: String)

fun logFeat22Event1(event: Feat22AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat22Event2(event: Feat22AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat22Event3(event: Feat22AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat22Event4(event: Feat22AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat22Event5(event: Feat22AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat22Event6(event: Feat22AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat22Event7(event: Feat22AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat22Event8(event: Feat22AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat22Event9(event: Feat22AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat22Event10(event: Feat22AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat22Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat22Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat22Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat22Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat22Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat22Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat22Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat22Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat22Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat22Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat22(u: CoreUser): Feat22Projection1 =
    Feat22Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat22Projection1> {
    val list = java.util.ArrayList<Feat22Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat22(u)
    }
    return list
}
