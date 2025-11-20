package com.romix.feature.feat80

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat80Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat80UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat80FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat80UserSummary
)

data class Feat80UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat80NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat80Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat80Config = Feat80Config()
) {

    fun loadSnapshot(userId: Long): Feat80NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat80NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat80UserSummary {
        return Feat80UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat80FeedItem> {
        val result = java.util.ArrayList<Feat80FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat80FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat80UiMapper {

    fun mapToUi(model: List<Feat80FeedItem>): Feat80UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat80UiModel(
            header = UiText("Feat80 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat80UiModel =
        Feat80UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat80UiModel =
        Feat80UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat80UiModel =
        Feat80UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat80Service(
    private val repository: Feat80Repository,
    private val uiMapper: Feat80UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat80UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat80UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat80UserItem1(val user: CoreUser, val label: String)
data class Feat80UserItem2(val user: CoreUser, val label: String)
data class Feat80UserItem3(val user: CoreUser, val label: String)
data class Feat80UserItem4(val user: CoreUser, val label: String)
data class Feat80UserItem5(val user: CoreUser, val label: String)
data class Feat80UserItem6(val user: CoreUser, val label: String)
data class Feat80UserItem7(val user: CoreUser, val label: String)
data class Feat80UserItem8(val user: CoreUser, val label: String)
data class Feat80UserItem9(val user: CoreUser, val label: String)
data class Feat80UserItem10(val user: CoreUser, val label: String)

data class Feat80StateBlock1(val state: Feat80UiModel, val checksum: Int)
data class Feat80StateBlock2(val state: Feat80UiModel, val checksum: Int)
data class Feat80StateBlock3(val state: Feat80UiModel, val checksum: Int)
data class Feat80StateBlock4(val state: Feat80UiModel, val checksum: Int)
data class Feat80StateBlock5(val state: Feat80UiModel, val checksum: Int)
data class Feat80StateBlock6(val state: Feat80UiModel, val checksum: Int)
data class Feat80StateBlock7(val state: Feat80UiModel, val checksum: Int)
data class Feat80StateBlock8(val state: Feat80UiModel, val checksum: Int)
data class Feat80StateBlock9(val state: Feat80UiModel, val checksum: Int)
data class Feat80StateBlock10(val state: Feat80UiModel, val checksum: Int)

fun buildFeat80UserItem(user: CoreUser, index: Int): Feat80UserItem1 {
    return Feat80UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat80StateBlock(model: Feat80UiModel): Feat80StateBlock1 {
    return Feat80StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat80UserSummary> {
    val list = java.util.ArrayList<Feat80UserSummary>(users.size)
    for (user in users) {
        list += Feat80UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat80UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat80UiModel {
    val summaries = (0 until count).map {
        Feat80UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat80UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat80UiModel> {
    val models = java.util.ArrayList<Feat80UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat80AnalyticsEvent1(val name: String, val value: String)
data class Feat80AnalyticsEvent2(val name: String, val value: String)
data class Feat80AnalyticsEvent3(val name: String, val value: String)
data class Feat80AnalyticsEvent4(val name: String, val value: String)
data class Feat80AnalyticsEvent5(val name: String, val value: String)
data class Feat80AnalyticsEvent6(val name: String, val value: String)
data class Feat80AnalyticsEvent7(val name: String, val value: String)
data class Feat80AnalyticsEvent8(val name: String, val value: String)
data class Feat80AnalyticsEvent9(val name: String, val value: String)
data class Feat80AnalyticsEvent10(val name: String, val value: String)

fun logFeat80Event1(event: Feat80AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat80Event2(event: Feat80AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat80Event3(event: Feat80AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat80Event4(event: Feat80AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat80Event5(event: Feat80AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat80Event6(event: Feat80AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat80Event7(event: Feat80AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat80Event8(event: Feat80AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat80Event9(event: Feat80AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat80Event10(event: Feat80AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat80Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat80Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat80Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat80Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat80Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat80Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat80Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat80Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat80Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat80Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat80(u: CoreUser): Feat80Projection1 =
    Feat80Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat80Projection1> {
    val list = java.util.ArrayList<Feat80Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat80(u)
    }
    return list
}
