package com.romix.feature.feat506

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat506Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat506UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat506FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat506UserSummary
)

data class Feat506UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat506NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat506Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat506Config = Feat506Config()
) {

    fun loadSnapshot(userId: Long): Feat506NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat506NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat506UserSummary {
        return Feat506UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat506FeedItem> {
        val result = java.util.ArrayList<Feat506FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat506FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat506UiMapper {

    fun mapToUi(model: List<Feat506FeedItem>): Feat506UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat506UiModel(
            header = UiText("Feat506 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat506UiModel =
        Feat506UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat506UiModel =
        Feat506UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat506UiModel =
        Feat506UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat506Service(
    private val repository: Feat506Repository,
    private val uiMapper: Feat506UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat506UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat506UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat506UserItem1(val user: CoreUser, val label: String)
data class Feat506UserItem2(val user: CoreUser, val label: String)
data class Feat506UserItem3(val user: CoreUser, val label: String)
data class Feat506UserItem4(val user: CoreUser, val label: String)
data class Feat506UserItem5(val user: CoreUser, val label: String)
data class Feat506UserItem6(val user: CoreUser, val label: String)
data class Feat506UserItem7(val user: CoreUser, val label: String)
data class Feat506UserItem8(val user: CoreUser, val label: String)
data class Feat506UserItem9(val user: CoreUser, val label: String)
data class Feat506UserItem10(val user: CoreUser, val label: String)

data class Feat506StateBlock1(val state: Feat506UiModel, val checksum: Int)
data class Feat506StateBlock2(val state: Feat506UiModel, val checksum: Int)
data class Feat506StateBlock3(val state: Feat506UiModel, val checksum: Int)
data class Feat506StateBlock4(val state: Feat506UiModel, val checksum: Int)
data class Feat506StateBlock5(val state: Feat506UiModel, val checksum: Int)
data class Feat506StateBlock6(val state: Feat506UiModel, val checksum: Int)
data class Feat506StateBlock7(val state: Feat506UiModel, val checksum: Int)
data class Feat506StateBlock8(val state: Feat506UiModel, val checksum: Int)
data class Feat506StateBlock9(val state: Feat506UiModel, val checksum: Int)
data class Feat506StateBlock10(val state: Feat506UiModel, val checksum: Int)

fun buildFeat506UserItem(user: CoreUser, index: Int): Feat506UserItem1 {
    return Feat506UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat506StateBlock(model: Feat506UiModel): Feat506StateBlock1 {
    return Feat506StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat506UserSummary> {
    val list = java.util.ArrayList<Feat506UserSummary>(users.size)
    for (user in users) {
        list += Feat506UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat506UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat506UiModel {
    val summaries = (0 until count).map {
        Feat506UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat506UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat506UiModel> {
    val models = java.util.ArrayList<Feat506UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat506AnalyticsEvent1(val name: String, val value: String)
data class Feat506AnalyticsEvent2(val name: String, val value: String)
data class Feat506AnalyticsEvent3(val name: String, val value: String)
data class Feat506AnalyticsEvent4(val name: String, val value: String)
data class Feat506AnalyticsEvent5(val name: String, val value: String)
data class Feat506AnalyticsEvent6(val name: String, val value: String)
data class Feat506AnalyticsEvent7(val name: String, val value: String)
data class Feat506AnalyticsEvent8(val name: String, val value: String)
data class Feat506AnalyticsEvent9(val name: String, val value: String)
data class Feat506AnalyticsEvent10(val name: String, val value: String)

fun logFeat506Event1(event: Feat506AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat506Event2(event: Feat506AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat506Event3(event: Feat506AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat506Event4(event: Feat506AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat506Event5(event: Feat506AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat506Event6(event: Feat506AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat506Event7(event: Feat506AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat506Event8(event: Feat506AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat506Event9(event: Feat506AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat506Event10(event: Feat506AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat506Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat506Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat506Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat506Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat506Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat506Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat506Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat506Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat506Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat506Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat506(u: CoreUser): Feat506Projection1 =
    Feat506Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat506Projection1> {
    val list = java.util.ArrayList<Feat506Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat506(u)
    }
    return list
}
