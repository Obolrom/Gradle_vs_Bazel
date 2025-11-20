package com.romix.feature.feat27

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat27Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat27UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat27FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat27UserSummary
)

data class Feat27UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat27NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat27Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat27Config = Feat27Config()
) {

    fun loadSnapshot(userId: Long): Feat27NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat27NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat27UserSummary {
        return Feat27UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat27FeedItem> {
        val result = java.util.ArrayList<Feat27FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat27FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat27UiMapper {

    fun mapToUi(model: List<Feat27FeedItem>): Feat27UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat27UiModel(
            header = UiText("Feat27 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat27UiModel =
        Feat27UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat27UiModel =
        Feat27UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat27UiModel =
        Feat27UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat27Service(
    private val repository: Feat27Repository,
    private val uiMapper: Feat27UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat27UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat27UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat27UserItem1(val user: CoreUser, val label: String)
data class Feat27UserItem2(val user: CoreUser, val label: String)
data class Feat27UserItem3(val user: CoreUser, val label: String)
data class Feat27UserItem4(val user: CoreUser, val label: String)
data class Feat27UserItem5(val user: CoreUser, val label: String)
data class Feat27UserItem6(val user: CoreUser, val label: String)
data class Feat27UserItem7(val user: CoreUser, val label: String)
data class Feat27UserItem8(val user: CoreUser, val label: String)
data class Feat27UserItem9(val user: CoreUser, val label: String)
data class Feat27UserItem10(val user: CoreUser, val label: String)

data class Feat27StateBlock1(val state: Feat27UiModel, val checksum: Int)
data class Feat27StateBlock2(val state: Feat27UiModel, val checksum: Int)
data class Feat27StateBlock3(val state: Feat27UiModel, val checksum: Int)
data class Feat27StateBlock4(val state: Feat27UiModel, val checksum: Int)
data class Feat27StateBlock5(val state: Feat27UiModel, val checksum: Int)
data class Feat27StateBlock6(val state: Feat27UiModel, val checksum: Int)
data class Feat27StateBlock7(val state: Feat27UiModel, val checksum: Int)
data class Feat27StateBlock8(val state: Feat27UiModel, val checksum: Int)
data class Feat27StateBlock9(val state: Feat27UiModel, val checksum: Int)
data class Feat27StateBlock10(val state: Feat27UiModel, val checksum: Int)

fun buildFeat27UserItem(user: CoreUser, index: Int): Feat27UserItem1 {
    return Feat27UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat27StateBlock(model: Feat27UiModel): Feat27StateBlock1 {
    return Feat27StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat27UserSummary> {
    val list = java.util.ArrayList<Feat27UserSummary>(users.size)
    for (user in users) {
        list += Feat27UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat27UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat27UiModel {
    val summaries = (0 until count).map {
        Feat27UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat27UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat27UiModel> {
    val models = java.util.ArrayList<Feat27UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat27AnalyticsEvent1(val name: String, val value: String)
data class Feat27AnalyticsEvent2(val name: String, val value: String)
data class Feat27AnalyticsEvent3(val name: String, val value: String)
data class Feat27AnalyticsEvent4(val name: String, val value: String)
data class Feat27AnalyticsEvent5(val name: String, val value: String)
data class Feat27AnalyticsEvent6(val name: String, val value: String)
data class Feat27AnalyticsEvent7(val name: String, val value: String)
data class Feat27AnalyticsEvent8(val name: String, val value: String)
data class Feat27AnalyticsEvent9(val name: String, val value: String)
data class Feat27AnalyticsEvent10(val name: String, val value: String)

fun logFeat27Event1(event: Feat27AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat27Event2(event: Feat27AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat27Event3(event: Feat27AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat27Event4(event: Feat27AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat27Event5(event: Feat27AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat27Event6(event: Feat27AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat27Event7(event: Feat27AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat27Event8(event: Feat27AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat27Event9(event: Feat27AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat27Event10(event: Feat27AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat27Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat27Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat27Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat27Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat27Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat27Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat27Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat27Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat27Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat27Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat27(u: CoreUser): Feat27Projection1 =
    Feat27Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat27Projection1> {
    val list = java.util.ArrayList<Feat27Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat27(u)
    }
    return list
}
