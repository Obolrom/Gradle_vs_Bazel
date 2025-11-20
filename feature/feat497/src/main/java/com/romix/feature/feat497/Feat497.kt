package com.romix.feature.feat497

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat497Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat497UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat497FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat497UserSummary
)

data class Feat497UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat497NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat497Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat497Config = Feat497Config()
) {

    fun loadSnapshot(userId: Long): Feat497NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat497NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat497UserSummary {
        return Feat497UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat497FeedItem> {
        val result = java.util.ArrayList<Feat497FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat497FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat497UiMapper {

    fun mapToUi(model: List<Feat497FeedItem>): Feat497UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat497UiModel(
            header = UiText("Feat497 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat497UiModel =
        Feat497UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat497UiModel =
        Feat497UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat497UiModel =
        Feat497UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat497Service(
    private val repository: Feat497Repository,
    private val uiMapper: Feat497UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat497UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat497UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat497UserItem1(val user: CoreUser, val label: String)
data class Feat497UserItem2(val user: CoreUser, val label: String)
data class Feat497UserItem3(val user: CoreUser, val label: String)
data class Feat497UserItem4(val user: CoreUser, val label: String)
data class Feat497UserItem5(val user: CoreUser, val label: String)
data class Feat497UserItem6(val user: CoreUser, val label: String)
data class Feat497UserItem7(val user: CoreUser, val label: String)
data class Feat497UserItem8(val user: CoreUser, val label: String)
data class Feat497UserItem9(val user: CoreUser, val label: String)
data class Feat497UserItem10(val user: CoreUser, val label: String)

data class Feat497StateBlock1(val state: Feat497UiModel, val checksum: Int)
data class Feat497StateBlock2(val state: Feat497UiModel, val checksum: Int)
data class Feat497StateBlock3(val state: Feat497UiModel, val checksum: Int)
data class Feat497StateBlock4(val state: Feat497UiModel, val checksum: Int)
data class Feat497StateBlock5(val state: Feat497UiModel, val checksum: Int)
data class Feat497StateBlock6(val state: Feat497UiModel, val checksum: Int)
data class Feat497StateBlock7(val state: Feat497UiModel, val checksum: Int)
data class Feat497StateBlock8(val state: Feat497UiModel, val checksum: Int)
data class Feat497StateBlock9(val state: Feat497UiModel, val checksum: Int)
data class Feat497StateBlock10(val state: Feat497UiModel, val checksum: Int)

fun buildFeat497UserItem(user: CoreUser, index: Int): Feat497UserItem1 {
    return Feat497UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat497StateBlock(model: Feat497UiModel): Feat497StateBlock1 {
    return Feat497StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat497UserSummary> {
    val list = java.util.ArrayList<Feat497UserSummary>(users.size)
    for (user in users) {
        list += Feat497UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat497UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat497UiModel {
    val summaries = (0 until count).map {
        Feat497UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat497UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat497UiModel> {
    val models = java.util.ArrayList<Feat497UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat497AnalyticsEvent1(val name: String, val value: String)
data class Feat497AnalyticsEvent2(val name: String, val value: String)
data class Feat497AnalyticsEvent3(val name: String, val value: String)
data class Feat497AnalyticsEvent4(val name: String, val value: String)
data class Feat497AnalyticsEvent5(val name: String, val value: String)
data class Feat497AnalyticsEvent6(val name: String, val value: String)
data class Feat497AnalyticsEvent7(val name: String, val value: String)
data class Feat497AnalyticsEvent8(val name: String, val value: String)
data class Feat497AnalyticsEvent9(val name: String, val value: String)
data class Feat497AnalyticsEvent10(val name: String, val value: String)

fun logFeat497Event1(event: Feat497AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat497Event2(event: Feat497AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat497Event3(event: Feat497AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat497Event4(event: Feat497AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat497Event5(event: Feat497AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat497Event6(event: Feat497AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat497Event7(event: Feat497AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat497Event8(event: Feat497AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat497Event9(event: Feat497AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat497Event10(event: Feat497AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat497Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat497Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat497Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat497Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat497Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat497Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat497Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat497Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat497Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat497Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat497(u: CoreUser): Feat497Projection1 =
    Feat497Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat497Projection1> {
    val list = java.util.ArrayList<Feat497Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat497(u)
    }
    return list
}
