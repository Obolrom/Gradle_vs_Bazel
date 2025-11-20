package com.romix.feature.feat57

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat57Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat57UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat57FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat57UserSummary
)

data class Feat57UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat57NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat57Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat57Config = Feat57Config()
) {

    fun loadSnapshot(userId: Long): Feat57NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat57NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat57UserSummary {
        return Feat57UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat57FeedItem> {
        val result = java.util.ArrayList<Feat57FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat57FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat57UiMapper {

    fun mapToUi(model: List<Feat57FeedItem>): Feat57UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat57UiModel(
            header = UiText("Feat57 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat57UiModel =
        Feat57UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat57UiModel =
        Feat57UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat57UiModel =
        Feat57UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat57Service(
    private val repository: Feat57Repository,
    private val uiMapper: Feat57UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat57UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat57UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat57UserItem1(val user: CoreUser, val label: String)
data class Feat57UserItem2(val user: CoreUser, val label: String)
data class Feat57UserItem3(val user: CoreUser, val label: String)
data class Feat57UserItem4(val user: CoreUser, val label: String)
data class Feat57UserItem5(val user: CoreUser, val label: String)
data class Feat57UserItem6(val user: CoreUser, val label: String)
data class Feat57UserItem7(val user: CoreUser, val label: String)
data class Feat57UserItem8(val user: CoreUser, val label: String)
data class Feat57UserItem9(val user: CoreUser, val label: String)
data class Feat57UserItem10(val user: CoreUser, val label: String)

data class Feat57StateBlock1(val state: Feat57UiModel, val checksum: Int)
data class Feat57StateBlock2(val state: Feat57UiModel, val checksum: Int)
data class Feat57StateBlock3(val state: Feat57UiModel, val checksum: Int)
data class Feat57StateBlock4(val state: Feat57UiModel, val checksum: Int)
data class Feat57StateBlock5(val state: Feat57UiModel, val checksum: Int)
data class Feat57StateBlock6(val state: Feat57UiModel, val checksum: Int)
data class Feat57StateBlock7(val state: Feat57UiModel, val checksum: Int)
data class Feat57StateBlock8(val state: Feat57UiModel, val checksum: Int)
data class Feat57StateBlock9(val state: Feat57UiModel, val checksum: Int)
data class Feat57StateBlock10(val state: Feat57UiModel, val checksum: Int)

fun buildFeat57UserItem(user: CoreUser, index: Int): Feat57UserItem1 {
    return Feat57UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat57StateBlock(model: Feat57UiModel): Feat57StateBlock1 {
    return Feat57StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat57UserSummary> {
    val list = java.util.ArrayList<Feat57UserSummary>(users.size)
    for (user in users) {
        list += Feat57UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat57UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat57UiModel {
    val summaries = (0 until count).map {
        Feat57UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat57UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat57UiModel> {
    val models = java.util.ArrayList<Feat57UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat57AnalyticsEvent1(val name: String, val value: String)
data class Feat57AnalyticsEvent2(val name: String, val value: String)
data class Feat57AnalyticsEvent3(val name: String, val value: String)
data class Feat57AnalyticsEvent4(val name: String, val value: String)
data class Feat57AnalyticsEvent5(val name: String, val value: String)
data class Feat57AnalyticsEvent6(val name: String, val value: String)
data class Feat57AnalyticsEvent7(val name: String, val value: String)
data class Feat57AnalyticsEvent8(val name: String, val value: String)
data class Feat57AnalyticsEvent9(val name: String, val value: String)
data class Feat57AnalyticsEvent10(val name: String, val value: String)

fun logFeat57Event1(event: Feat57AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat57Event2(event: Feat57AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat57Event3(event: Feat57AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat57Event4(event: Feat57AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat57Event5(event: Feat57AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat57Event6(event: Feat57AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat57Event7(event: Feat57AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat57Event8(event: Feat57AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat57Event9(event: Feat57AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat57Event10(event: Feat57AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat57Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat57Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat57Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat57Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat57Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat57Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat57Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat57Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat57Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat57Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat57(u: CoreUser): Feat57Projection1 =
    Feat57Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat57Projection1> {
    val list = java.util.ArrayList<Feat57Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat57(u)
    }
    return list
}
