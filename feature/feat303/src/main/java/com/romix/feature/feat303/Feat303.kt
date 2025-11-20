package com.romix.feature.feat303

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat303Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat303UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat303FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat303UserSummary
)

data class Feat303UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat303NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat303Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat303Config = Feat303Config()
) {

    fun loadSnapshot(userId: Long): Feat303NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat303NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat303UserSummary {
        return Feat303UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat303FeedItem> {
        val result = java.util.ArrayList<Feat303FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat303FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat303UiMapper {

    fun mapToUi(model: List<Feat303FeedItem>): Feat303UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat303UiModel(
            header = UiText("Feat303 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat303UiModel =
        Feat303UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat303UiModel =
        Feat303UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat303UiModel =
        Feat303UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat303Service(
    private val repository: Feat303Repository,
    private val uiMapper: Feat303UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat303UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat303UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat303UserItem1(val user: CoreUser, val label: String)
data class Feat303UserItem2(val user: CoreUser, val label: String)
data class Feat303UserItem3(val user: CoreUser, val label: String)
data class Feat303UserItem4(val user: CoreUser, val label: String)
data class Feat303UserItem5(val user: CoreUser, val label: String)
data class Feat303UserItem6(val user: CoreUser, val label: String)
data class Feat303UserItem7(val user: CoreUser, val label: String)
data class Feat303UserItem8(val user: CoreUser, val label: String)
data class Feat303UserItem9(val user: CoreUser, val label: String)
data class Feat303UserItem10(val user: CoreUser, val label: String)

data class Feat303StateBlock1(val state: Feat303UiModel, val checksum: Int)
data class Feat303StateBlock2(val state: Feat303UiModel, val checksum: Int)
data class Feat303StateBlock3(val state: Feat303UiModel, val checksum: Int)
data class Feat303StateBlock4(val state: Feat303UiModel, val checksum: Int)
data class Feat303StateBlock5(val state: Feat303UiModel, val checksum: Int)
data class Feat303StateBlock6(val state: Feat303UiModel, val checksum: Int)
data class Feat303StateBlock7(val state: Feat303UiModel, val checksum: Int)
data class Feat303StateBlock8(val state: Feat303UiModel, val checksum: Int)
data class Feat303StateBlock9(val state: Feat303UiModel, val checksum: Int)
data class Feat303StateBlock10(val state: Feat303UiModel, val checksum: Int)

fun buildFeat303UserItem(user: CoreUser, index: Int): Feat303UserItem1 {
    return Feat303UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat303StateBlock(model: Feat303UiModel): Feat303StateBlock1 {
    return Feat303StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat303UserSummary> {
    val list = java.util.ArrayList<Feat303UserSummary>(users.size)
    for (user in users) {
        list += Feat303UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat303UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat303UiModel {
    val summaries = (0 until count).map {
        Feat303UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat303UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat303UiModel> {
    val models = java.util.ArrayList<Feat303UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat303AnalyticsEvent1(val name: String, val value: String)
data class Feat303AnalyticsEvent2(val name: String, val value: String)
data class Feat303AnalyticsEvent3(val name: String, val value: String)
data class Feat303AnalyticsEvent4(val name: String, val value: String)
data class Feat303AnalyticsEvent5(val name: String, val value: String)
data class Feat303AnalyticsEvent6(val name: String, val value: String)
data class Feat303AnalyticsEvent7(val name: String, val value: String)
data class Feat303AnalyticsEvent8(val name: String, val value: String)
data class Feat303AnalyticsEvent9(val name: String, val value: String)
data class Feat303AnalyticsEvent10(val name: String, val value: String)

fun logFeat303Event1(event: Feat303AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat303Event2(event: Feat303AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat303Event3(event: Feat303AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat303Event4(event: Feat303AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat303Event5(event: Feat303AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat303Event6(event: Feat303AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat303Event7(event: Feat303AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat303Event8(event: Feat303AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat303Event9(event: Feat303AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat303Event10(event: Feat303AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat303Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat303Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat303Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat303Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat303Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat303Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat303Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat303Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat303Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat303Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat303(u: CoreUser): Feat303Projection1 =
    Feat303Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat303Projection1> {
    val list = java.util.ArrayList<Feat303Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat303(u)
    }
    return list
}
