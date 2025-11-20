package com.romix.feature.feat649

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat649Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat649UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat649FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat649UserSummary
)

data class Feat649UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat649NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat649Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat649Config = Feat649Config()
) {

    fun loadSnapshot(userId: Long): Feat649NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat649NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat649UserSummary {
        return Feat649UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat649FeedItem> {
        val result = java.util.ArrayList<Feat649FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat649FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat649UiMapper {

    fun mapToUi(model: List<Feat649FeedItem>): Feat649UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat649UiModel(
            header = UiText("Feat649 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat649UiModel =
        Feat649UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat649UiModel =
        Feat649UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat649UiModel =
        Feat649UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat649Service(
    private val repository: Feat649Repository,
    private val uiMapper: Feat649UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat649UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat649UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat649UserItem1(val user: CoreUser, val label: String)
data class Feat649UserItem2(val user: CoreUser, val label: String)
data class Feat649UserItem3(val user: CoreUser, val label: String)
data class Feat649UserItem4(val user: CoreUser, val label: String)
data class Feat649UserItem5(val user: CoreUser, val label: String)
data class Feat649UserItem6(val user: CoreUser, val label: String)
data class Feat649UserItem7(val user: CoreUser, val label: String)
data class Feat649UserItem8(val user: CoreUser, val label: String)
data class Feat649UserItem9(val user: CoreUser, val label: String)
data class Feat649UserItem10(val user: CoreUser, val label: String)

data class Feat649StateBlock1(val state: Feat649UiModel, val checksum: Int)
data class Feat649StateBlock2(val state: Feat649UiModel, val checksum: Int)
data class Feat649StateBlock3(val state: Feat649UiModel, val checksum: Int)
data class Feat649StateBlock4(val state: Feat649UiModel, val checksum: Int)
data class Feat649StateBlock5(val state: Feat649UiModel, val checksum: Int)
data class Feat649StateBlock6(val state: Feat649UiModel, val checksum: Int)
data class Feat649StateBlock7(val state: Feat649UiModel, val checksum: Int)
data class Feat649StateBlock8(val state: Feat649UiModel, val checksum: Int)
data class Feat649StateBlock9(val state: Feat649UiModel, val checksum: Int)
data class Feat649StateBlock10(val state: Feat649UiModel, val checksum: Int)

fun buildFeat649UserItem(user: CoreUser, index: Int): Feat649UserItem1 {
    return Feat649UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat649StateBlock(model: Feat649UiModel): Feat649StateBlock1 {
    return Feat649StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat649UserSummary> {
    val list = java.util.ArrayList<Feat649UserSummary>(users.size)
    for (user in users) {
        list += Feat649UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat649UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat649UiModel {
    val summaries = (0 until count).map {
        Feat649UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat649UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat649UiModel> {
    val models = java.util.ArrayList<Feat649UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat649AnalyticsEvent1(val name: String, val value: String)
data class Feat649AnalyticsEvent2(val name: String, val value: String)
data class Feat649AnalyticsEvent3(val name: String, val value: String)
data class Feat649AnalyticsEvent4(val name: String, val value: String)
data class Feat649AnalyticsEvent5(val name: String, val value: String)
data class Feat649AnalyticsEvent6(val name: String, val value: String)
data class Feat649AnalyticsEvent7(val name: String, val value: String)
data class Feat649AnalyticsEvent8(val name: String, val value: String)
data class Feat649AnalyticsEvent9(val name: String, val value: String)
data class Feat649AnalyticsEvent10(val name: String, val value: String)

fun logFeat649Event1(event: Feat649AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat649Event2(event: Feat649AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat649Event3(event: Feat649AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat649Event4(event: Feat649AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat649Event5(event: Feat649AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat649Event6(event: Feat649AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat649Event7(event: Feat649AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat649Event8(event: Feat649AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat649Event9(event: Feat649AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat649Event10(event: Feat649AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat649Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat649Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat649Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat649Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat649Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat649Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat649Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat649Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat649Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat649Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat649(u: CoreUser): Feat649Projection1 =
    Feat649Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat649Projection1> {
    val list = java.util.ArrayList<Feat649Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat649(u)
    }
    return list
}
