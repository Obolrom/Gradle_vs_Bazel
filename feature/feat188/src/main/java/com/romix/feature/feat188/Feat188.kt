package com.romix.feature.feat188

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat188Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat188UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat188FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat188UserSummary
)

data class Feat188UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat188NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat188Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat188Config = Feat188Config()
) {

    fun loadSnapshot(userId: Long): Feat188NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat188NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat188UserSummary {
        return Feat188UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat188FeedItem> {
        val result = java.util.ArrayList<Feat188FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat188FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat188UiMapper {

    fun mapToUi(model: List<Feat188FeedItem>): Feat188UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat188UiModel(
            header = UiText("Feat188 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat188UiModel =
        Feat188UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat188UiModel =
        Feat188UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat188UiModel =
        Feat188UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat188Service(
    private val repository: Feat188Repository,
    private val uiMapper: Feat188UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat188UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat188UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat188UserItem1(val user: CoreUser, val label: String)
data class Feat188UserItem2(val user: CoreUser, val label: String)
data class Feat188UserItem3(val user: CoreUser, val label: String)
data class Feat188UserItem4(val user: CoreUser, val label: String)
data class Feat188UserItem5(val user: CoreUser, val label: String)
data class Feat188UserItem6(val user: CoreUser, val label: String)
data class Feat188UserItem7(val user: CoreUser, val label: String)
data class Feat188UserItem8(val user: CoreUser, val label: String)
data class Feat188UserItem9(val user: CoreUser, val label: String)
data class Feat188UserItem10(val user: CoreUser, val label: String)

data class Feat188StateBlock1(val state: Feat188UiModel, val checksum: Int)
data class Feat188StateBlock2(val state: Feat188UiModel, val checksum: Int)
data class Feat188StateBlock3(val state: Feat188UiModel, val checksum: Int)
data class Feat188StateBlock4(val state: Feat188UiModel, val checksum: Int)
data class Feat188StateBlock5(val state: Feat188UiModel, val checksum: Int)
data class Feat188StateBlock6(val state: Feat188UiModel, val checksum: Int)
data class Feat188StateBlock7(val state: Feat188UiModel, val checksum: Int)
data class Feat188StateBlock8(val state: Feat188UiModel, val checksum: Int)
data class Feat188StateBlock9(val state: Feat188UiModel, val checksum: Int)
data class Feat188StateBlock10(val state: Feat188UiModel, val checksum: Int)

fun buildFeat188UserItem(user: CoreUser, index: Int): Feat188UserItem1 {
    return Feat188UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat188StateBlock(model: Feat188UiModel): Feat188StateBlock1 {
    return Feat188StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat188UserSummary> {
    val list = java.util.ArrayList<Feat188UserSummary>(users.size)
    for (user in users) {
        list += Feat188UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat188UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat188UiModel {
    val summaries = (0 until count).map {
        Feat188UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat188UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat188UiModel> {
    val models = java.util.ArrayList<Feat188UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat188AnalyticsEvent1(val name: String, val value: String)
data class Feat188AnalyticsEvent2(val name: String, val value: String)
data class Feat188AnalyticsEvent3(val name: String, val value: String)
data class Feat188AnalyticsEvent4(val name: String, val value: String)
data class Feat188AnalyticsEvent5(val name: String, val value: String)
data class Feat188AnalyticsEvent6(val name: String, val value: String)
data class Feat188AnalyticsEvent7(val name: String, val value: String)
data class Feat188AnalyticsEvent8(val name: String, val value: String)
data class Feat188AnalyticsEvent9(val name: String, val value: String)
data class Feat188AnalyticsEvent10(val name: String, val value: String)

fun logFeat188Event1(event: Feat188AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat188Event2(event: Feat188AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat188Event3(event: Feat188AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat188Event4(event: Feat188AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat188Event5(event: Feat188AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat188Event6(event: Feat188AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat188Event7(event: Feat188AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat188Event8(event: Feat188AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat188Event9(event: Feat188AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat188Event10(event: Feat188AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat188Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat188Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat188Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat188Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat188Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat188Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat188Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat188Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat188Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat188Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat188(u: CoreUser): Feat188Projection1 =
    Feat188Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat188Projection1> {
    val list = java.util.ArrayList<Feat188Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat188(u)
    }
    return list
}
