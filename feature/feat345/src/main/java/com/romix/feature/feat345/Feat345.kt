package com.romix.feature.feat345

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat345Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat345UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat345FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat345UserSummary
)

data class Feat345UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat345NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat345Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat345Config = Feat345Config()
) {

    fun loadSnapshot(userId: Long): Feat345NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat345NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat345UserSummary {
        return Feat345UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat345FeedItem> {
        val result = java.util.ArrayList<Feat345FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat345FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat345UiMapper {

    fun mapToUi(model: List<Feat345FeedItem>): Feat345UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat345UiModel(
            header = UiText("Feat345 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat345UiModel =
        Feat345UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat345UiModel =
        Feat345UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat345UiModel =
        Feat345UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat345Service(
    private val repository: Feat345Repository,
    private val uiMapper: Feat345UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat345UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat345UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat345UserItem1(val user: CoreUser, val label: String)
data class Feat345UserItem2(val user: CoreUser, val label: String)
data class Feat345UserItem3(val user: CoreUser, val label: String)
data class Feat345UserItem4(val user: CoreUser, val label: String)
data class Feat345UserItem5(val user: CoreUser, val label: String)
data class Feat345UserItem6(val user: CoreUser, val label: String)
data class Feat345UserItem7(val user: CoreUser, val label: String)
data class Feat345UserItem8(val user: CoreUser, val label: String)
data class Feat345UserItem9(val user: CoreUser, val label: String)
data class Feat345UserItem10(val user: CoreUser, val label: String)

data class Feat345StateBlock1(val state: Feat345UiModel, val checksum: Int)
data class Feat345StateBlock2(val state: Feat345UiModel, val checksum: Int)
data class Feat345StateBlock3(val state: Feat345UiModel, val checksum: Int)
data class Feat345StateBlock4(val state: Feat345UiModel, val checksum: Int)
data class Feat345StateBlock5(val state: Feat345UiModel, val checksum: Int)
data class Feat345StateBlock6(val state: Feat345UiModel, val checksum: Int)
data class Feat345StateBlock7(val state: Feat345UiModel, val checksum: Int)
data class Feat345StateBlock8(val state: Feat345UiModel, val checksum: Int)
data class Feat345StateBlock9(val state: Feat345UiModel, val checksum: Int)
data class Feat345StateBlock10(val state: Feat345UiModel, val checksum: Int)

fun buildFeat345UserItem(user: CoreUser, index: Int): Feat345UserItem1 {
    return Feat345UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat345StateBlock(model: Feat345UiModel): Feat345StateBlock1 {
    return Feat345StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat345UserSummary> {
    val list = java.util.ArrayList<Feat345UserSummary>(users.size)
    for (user in users) {
        list += Feat345UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat345UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat345UiModel {
    val summaries = (0 until count).map {
        Feat345UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat345UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat345UiModel> {
    val models = java.util.ArrayList<Feat345UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat345AnalyticsEvent1(val name: String, val value: String)
data class Feat345AnalyticsEvent2(val name: String, val value: String)
data class Feat345AnalyticsEvent3(val name: String, val value: String)
data class Feat345AnalyticsEvent4(val name: String, val value: String)
data class Feat345AnalyticsEvent5(val name: String, val value: String)
data class Feat345AnalyticsEvent6(val name: String, val value: String)
data class Feat345AnalyticsEvent7(val name: String, val value: String)
data class Feat345AnalyticsEvent8(val name: String, val value: String)
data class Feat345AnalyticsEvent9(val name: String, val value: String)
data class Feat345AnalyticsEvent10(val name: String, val value: String)

fun logFeat345Event1(event: Feat345AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat345Event2(event: Feat345AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat345Event3(event: Feat345AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat345Event4(event: Feat345AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat345Event5(event: Feat345AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat345Event6(event: Feat345AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat345Event7(event: Feat345AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat345Event8(event: Feat345AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat345Event9(event: Feat345AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat345Event10(event: Feat345AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat345Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat345Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat345Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat345Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat345Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat345Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat345Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat345Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat345Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat345Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat345(u: CoreUser): Feat345Projection1 =
    Feat345Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat345Projection1> {
    val list = java.util.ArrayList<Feat345Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat345(u)
    }
    return list
}
