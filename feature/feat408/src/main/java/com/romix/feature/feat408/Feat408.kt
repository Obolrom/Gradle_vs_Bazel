package com.romix.feature.feat408

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat408Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat408UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat408FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat408UserSummary
)

data class Feat408UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat408NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat408Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat408Config = Feat408Config()
) {

    fun loadSnapshot(userId: Long): Feat408NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat408NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat408UserSummary {
        return Feat408UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat408FeedItem> {
        val result = java.util.ArrayList<Feat408FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat408FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat408UiMapper {

    fun mapToUi(model: List<Feat408FeedItem>): Feat408UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat408UiModel(
            header = UiText("Feat408 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat408UiModel =
        Feat408UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat408UiModel =
        Feat408UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat408UiModel =
        Feat408UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat408Service(
    private val repository: Feat408Repository,
    private val uiMapper: Feat408UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat408UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat408UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat408UserItem1(val user: CoreUser, val label: String)
data class Feat408UserItem2(val user: CoreUser, val label: String)
data class Feat408UserItem3(val user: CoreUser, val label: String)
data class Feat408UserItem4(val user: CoreUser, val label: String)
data class Feat408UserItem5(val user: CoreUser, val label: String)
data class Feat408UserItem6(val user: CoreUser, val label: String)
data class Feat408UserItem7(val user: CoreUser, val label: String)
data class Feat408UserItem8(val user: CoreUser, val label: String)
data class Feat408UserItem9(val user: CoreUser, val label: String)
data class Feat408UserItem10(val user: CoreUser, val label: String)

data class Feat408StateBlock1(val state: Feat408UiModel, val checksum: Int)
data class Feat408StateBlock2(val state: Feat408UiModel, val checksum: Int)
data class Feat408StateBlock3(val state: Feat408UiModel, val checksum: Int)
data class Feat408StateBlock4(val state: Feat408UiModel, val checksum: Int)
data class Feat408StateBlock5(val state: Feat408UiModel, val checksum: Int)
data class Feat408StateBlock6(val state: Feat408UiModel, val checksum: Int)
data class Feat408StateBlock7(val state: Feat408UiModel, val checksum: Int)
data class Feat408StateBlock8(val state: Feat408UiModel, val checksum: Int)
data class Feat408StateBlock9(val state: Feat408UiModel, val checksum: Int)
data class Feat408StateBlock10(val state: Feat408UiModel, val checksum: Int)

fun buildFeat408UserItem(user: CoreUser, index: Int): Feat408UserItem1 {
    return Feat408UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat408StateBlock(model: Feat408UiModel): Feat408StateBlock1 {
    return Feat408StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat408UserSummary> {
    val list = java.util.ArrayList<Feat408UserSummary>(users.size)
    for (user in users) {
        list += Feat408UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat408UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat408UiModel {
    val summaries = (0 until count).map {
        Feat408UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat408UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat408UiModel> {
    val models = java.util.ArrayList<Feat408UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat408AnalyticsEvent1(val name: String, val value: String)
data class Feat408AnalyticsEvent2(val name: String, val value: String)
data class Feat408AnalyticsEvent3(val name: String, val value: String)
data class Feat408AnalyticsEvent4(val name: String, val value: String)
data class Feat408AnalyticsEvent5(val name: String, val value: String)
data class Feat408AnalyticsEvent6(val name: String, val value: String)
data class Feat408AnalyticsEvent7(val name: String, val value: String)
data class Feat408AnalyticsEvent8(val name: String, val value: String)
data class Feat408AnalyticsEvent9(val name: String, val value: String)
data class Feat408AnalyticsEvent10(val name: String, val value: String)

fun logFeat408Event1(event: Feat408AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat408Event2(event: Feat408AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat408Event3(event: Feat408AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat408Event4(event: Feat408AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat408Event5(event: Feat408AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat408Event6(event: Feat408AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat408Event7(event: Feat408AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat408Event8(event: Feat408AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat408Event9(event: Feat408AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat408Event10(event: Feat408AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat408Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat408Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat408Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat408Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat408Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat408Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat408Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat408Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat408Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat408Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat408(u: CoreUser): Feat408Projection1 =
    Feat408Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat408Projection1> {
    val list = java.util.ArrayList<Feat408Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat408(u)
    }
    return list
}
