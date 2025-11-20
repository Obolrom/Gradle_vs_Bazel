package com.romix.feature.feat643

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat643Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat643UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat643FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat643UserSummary
)

data class Feat643UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat643NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat643Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat643Config = Feat643Config()
) {

    fun loadSnapshot(userId: Long): Feat643NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat643NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat643UserSummary {
        return Feat643UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat643FeedItem> {
        val result = java.util.ArrayList<Feat643FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat643FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat643UiMapper {

    fun mapToUi(model: List<Feat643FeedItem>): Feat643UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat643UiModel(
            header = UiText("Feat643 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat643UiModel =
        Feat643UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat643UiModel =
        Feat643UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat643UiModel =
        Feat643UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat643Service(
    private val repository: Feat643Repository,
    private val uiMapper: Feat643UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat643UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat643UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat643UserItem1(val user: CoreUser, val label: String)
data class Feat643UserItem2(val user: CoreUser, val label: String)
data class Feat643UserItem3(val user: CoreUser, val label: String)
data class Feat643UserItem4(val user: CoreUser, val label: String)
data class Feat643UserItem5(val user: CoreUser, val label: String)
data class Feat643UserItem6(val user: CoreUser, val label: String)
data class Feat643UserItem7(val user: CoreUser, val label: String)
data class Feat643UserItem8(val user: CoreUser, val label: String)
data class Feat643UserItem9(val user: CoreUser, val label: String)
data class Feat643UserItem10(val user: CoreUser, val label: String)

data class Feat643StateBlock1(val state: Feat643UiModel, val checksum: Int)
data class Feat643StateBlock2(val state: Feat643UiModel, val checksum: Int)
data class Feat643StateBlock3(val state: Feat643UiModel, val checksum: Int)
data class Feat643StateBlock4(val state: Feat643UiModel, val checksum: Int)
data class Feat643StateBlock5(val state: Feat643UiModel, val checksum: Int)
data class Feat643StateBlock6(val state: Feat643UiModel, val checksum: Int)
data class Feat643StateBlock7(val state: Feat643UiModel, val checksum: Int)
data class Feat643StateBlock8(val state: Feat643UiModel, val checksum: Int)
data class Feat643StateBlock9(val state: Feat643UiModel, val checksum: Int)
data class Feat643StateBlock10(val state: Feat643UiModel, val checksum: Int)

fun buildFeat643UserItem(user: CoreUser, index: Int): Feat643UserItem1 {
    return Feat643UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat643StateBlock(model: Feat643UiModel): Feat643StateBlock1 {
    return Feat643StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat643UserSummary> {
    val list = java.util.ArrayList<Feat643UserSummary>(users.size)
    for (user in users) {
        list += Feat643UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat643UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat643UiModel {
    val summaries = (0 until count).map {
        Feat643UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat643UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat643UiModel> {
    val models = java.util.ArrayList<Feat643UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat643AnalyticsEvent1(val name: String, val value: String)
data class Feat643AnalyticsEvent2(val name: String, val value: String)
data class Feat643AnalyticsEvent3(val name: String, val value: String)
data class Feat643AnalyticsEvent4(val name: String, val value: String)
data class Feat643AnalyticsEvent5(val name: String, val value: String)
data class Feat643AnalyticsEvent6(val name: String, val value: String)
data class Feat643AnalyticsEvent7(val name: String, val value: String)
data class Feat643AnalyticsEvent8(val name: String, val value: String)
data class Feat643AnalyticsEvent9(val name: String, val value: String)
data class Feat643AnalyticsEvent10(val name: String, val value: String)

fun logFeat643Event1(event: Feat643AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat643Event2(event: Feat643AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat643Event3(event: Feat643AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat643Event4(event: Feat643AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat643Event5(event: Feat643AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat643Event6(event: Feat643AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat643Event7(event: Feat643AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat643Event8(event: Feat643AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat643Event9(event: Feat643AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat643Event10(event: Feat643AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat643Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat643Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat643Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat643Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat643Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat643Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat643Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat643Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat643Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat643Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat643(u: CoreUser): Feat643Projection1 =
    Feat643Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat643Projection1> {
    val list = java.util.ArrayList<Feat643Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat643(u)
    }
    return list
}
