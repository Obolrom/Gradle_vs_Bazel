package com.romix.feature.feat434

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat434Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat434UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat434FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat434UserSummary
)

data class Feat434UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat434NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat434Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat434Config = Feat434Config()
) {

    fun loadSnapshot(userId: Long): Feat434NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat434NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat434UserSummary {
        return Feat434UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat434FeedItem> {
        val result = java.util.ArrayList<Feat434FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat434FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat434UiMapper {

    fun mapToUi(model: List<Feat434FeedItem>): Feat434UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat434UiModel(
            header = UiText("Feat434 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat434UiModel =
        Feat434UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat434UiModel =
        Feat434UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat434UiModel =
        Feat434UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat434Service(
    private val repository: Feat434Repository,
    private val uiMapper: Feat434UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat434UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat434UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat434UserItem1(val user: CoreUser, val label: String)
data class Feat434UserItem2(val user: CoreUser, val label: String)
data class Feat434UserItem3(val user: CoreUser, val label: String)
data class Feat434UserItem4(val user: CoreUser, val label: String)
data class Feat434UserItem5(val user: CoreUser, val label: String)
data class Feat434UserItem6(val user: CoreUser, val label: String)
data class Feat434UserItem7(val user: CoreUser, val label: String)
data class Feat434UserItem8(val user: CoreUser, val label: String)
data class Feat434UserItem9(val user: CoreUser, val label: String)
data class Feat434UserItem10(val user: CoreUser, val label: String)

data class Feat434StateBlock1(val state: Feat434UiModel, val checksum: Int)
data class Feat434StateBlock2(val state: Feat434UiModel, val checksum: Int)
data class Feat434StateBlock3(val state: Feat434UiModel, val checksum: Int)
data class Feat434StateBlock4(val state: Feat434UiModel, val checksum: Int)
data class Feat434StateBlock5(val state: Feat434UiModel, val checksum: Int)
data class Feat434StateBlock6(val state: Feat434UiModel, val checksum: Int)
data class Feat434StateBlock7(val state: Feat434UiModel, val checksum: Int)
data class Feat434StateBlock8(val state: Feat434UiModel, val checksum: Int)
data class Feat434StateBlock9(val state: Feat434UiModel, val checksum: Int)
data class Feat434StateBlock10(val state: Feat434UiModel, val checksum: Int)

fun buildFeat434UserItem(user: CoreUser, index: Int): Feat434UserItem1 {
    return Feat434UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat434StateBlock(model: Feat434UiModel): Feat434StateBlock1 {
    return Feat434StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat434UserSummary> {
    val list = java.util.ArrayList<Feat434UserSummary>(users.size)
    for (user in users) {
        list += Feat434UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat434UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat434UiModel {
    val summaries = (0 until count).map {
        Feat434UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat434UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat434UiModel> {
    val models = java.util.ArrayList<Feat434UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat434AnalyticsEvent1(val name: String, val value: String)
data class Feat434AnalyticsEvent2(val name: String, val value: String)
data class Feat434AnalyticsEvent3(val name: String, val value: String)
data class Feat434AnalyticsEvent4(val name: String, val value: String)
data class Feat434AnalyticsEvent5(val name: String, val value: String)
data class Feat434AnalyticsEvent6(val name: String, val value: String)
data class Feat434AnalyticsEvent7(val name: String, val value: String)
data class Feat434AnalyticsEvent8(val name: String, val value: String)
data class Feat434AnalyticsEvent9(val name: String, val value: String)
data class Feat434AnalyticsEvent10(val name: String, val value: String)

fun logFeat434Event1(event: Feat434AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat434Event2(event: Feat434AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat434Event3(event: Feat434AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat434Event4(event: Feat434AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat434Event5(event: Feat434AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat434Event6(event: Feat434AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat434Event7(event: Feat434AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat434Event8(event: Feat434AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat434Event9(event: Feat434AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat434Event10(event: Feat434AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat434Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat434Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat434Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat434Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat434Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat434Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat434Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat434Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat434Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat434Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat434(u: CoreUser): Feat434Projection1 =
    Feat434Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat434Projection1> {
    val list = java.util.ArrayList<Feat434Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat434(u)
    }
    return list
}
