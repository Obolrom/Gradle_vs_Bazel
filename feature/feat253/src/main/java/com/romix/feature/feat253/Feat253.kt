package com.romix.feature.feat253

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat253Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat253UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat253FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat253UserSummary
)

data class Feat253UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat253NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat253Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat253Config = Feat253Config()
) {

    fun loadSnapshot(userId: Long): Feat253NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat253NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat253UserSummary {
        return Feat253UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat253FeedItem> {
        val result = java.util.ArrayList<Feat253FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat253FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat253UiMapper {

    fun mapToUi(model: List<Feat253FeedItem>): Feat253UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat253UiModel(
            header = UiText("Feat253 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat253UiModel =
        Feat253UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat253UiModel =
        Feat253UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat253UiModel =
        Feat253UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat253Service(
    private val repository: Feat253Repository,
    private val uiMapper: Feat253UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat253UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat253UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat253UserItem1(val user: CoreUser, val label: String)
data class Feat253UserItem2(val user: CoreUser, val label: String)
data class Feat253UserItem3(val user: CoreUser, val label: String)
data class Feat253UserItem4(val user: CoreUser, val label: String)
data class Feat253UserItem5(val user: CoreUser, val label: String)
data class Feat253UserItem6(val user: CoreUser, val label: String)
data class Feat253UserItem7(val user: CoreUser, val label: String)
data class Feat253UserItem8(val user: CoreUser, val label: String)
data class Feat253UserItem9(val user: CoreUser, val label: String)
data class Feat253UserItem10(val user: CoreUser, val label: String)

data class Feat253StateBlock1(val state: Feat253UiModel, val checksum: Int)
data class Feat253StateBlock2(val state: Feat253UiModel, val checksum: Int)
data class Feat253StateBlock3(val state: Feat253UiModel, val checksum: Int)
data class Feat253StateBlock4(val state: Feat253UiModel, val checksum: Int)
data class Feat253StateBlock5(val state: Feat253UiModel, val checksum: Int)
data class Feat253StateBlock6(val state: Feat253UiModel, val checksum: Int)
data class Feat253StateBlock7(val state: Feat253UiModel, val checksum: Int)
data class Feat253StateBlock8(val state: Feat253UiModel, val checksum: Int)
data class Feat253StateBlock9(val state: Feat253UiModel, val checksum: Int)
data class Feat253StateBlock10(val state: Feat253UiModel, val checksum: Int)

fun buildFeat253UserItem(user: CoreUser, index: Int): Feat253UserItem1 {
    return Feat253UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat253StateBlock(model: Feat253UiModel): Feat253StateBlock1 {
    return Feat253StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat253UserSummary> {
    val list = java.util.ArrayList<Feat253UserSummary>(users.size)
    for (user in users) {
        list += Feat253UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat253UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat253UiModel {
    val summaries = (0 until count).map {
        Feat253UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat253UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat253UiModel> {
    val models = java.util.ArrayList<Feat253UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat253AnalyticsEvent1(val name: String, val value: String)
data class Feat253AnalyticsEvent2(val name: String, val value: String)
data class Feat253AnalyticsEvent3(val name: String, val value: String)
data class Feat253AnalyticsEvent4(val name: String, val value: String)
data class Feat253AnalyticsEvent5(val name: String, val value: String)
data class Feat253AnalyticsEvent6(val name: String, val value: String)
data class Feat253AnalyticsEvent7(val name: String, val value: String)
data class Feat253AnalyticsEvent8(val name: String, val value: String)
data class Feat253AnalyticsEvent9(val name: String, val value: String)
data class Feat253AnalyticsEvent10(val name: String, val value: String)

fun logFeat253Event1(event: Feat253AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat253Event2(event: Feat253AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat253Event3(event: Feat253AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat253Event4(event: Feat253AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat253Event5(event: Feat253AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat253Event6(event: Feat253AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat253Event7(event: Feat253AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat253Event8(event: Feat253AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat253Event9(event: Feat253AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat253Event10(event: Feat253AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat253Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat253Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat253Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat253Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat253Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat253Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat253Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat253Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat253Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat253Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat253(u: CoreUser): Feat253Projection1 =
    Feat253Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat253Projection1> {
    val list = java.util.ArrayList<Feat253Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat253(u)
    }
    return list
}
