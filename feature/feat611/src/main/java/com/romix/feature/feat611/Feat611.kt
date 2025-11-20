package com.romix.feature.feat611

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat611Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat611UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat611FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat611UserSummary
)

data class Feat611UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat611NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat611Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat611Config = Feat611Config()
) {

    fun loadSnapshot(userId: Long): Feat611NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat611NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat611UserSummary {
        return Feat611UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat611FeedItem> {
        val result = java.util.ArrayList<Feat611FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat611FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat611UiMapper {

    fun mapToUi(model: List<Feat611FeedItem>): Feat611UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat611UiModel(
            header = UiText("Feat611 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat611UiModel =
        Feat611UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat611UiModel =
        Feat611UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat611UiModel =
        Feat611UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat611Service(
    private val repository: Feat611Repository,
    private val uiMapper: Feat611UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat611UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat611UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat611UserItem1(val user: CoreUser, val label: String)
data class Feat611UserItem2(val user: CoreUser, val label: String)
data class Feat611UserItem3(val user: CoreUser, val label: String)
data class Feat611UserItem4(val user: CoreUser, val label: String)
data class Feat611UserItem5(val user: CoreUser, val label: String)
data class Feat611UserItem6(val user: CoreUser, val label: String)
data class Feat611UserItem7(val user: CoreUser, val label: String)
data class Feat611UserItem8(val user: CoreUser, val label: String)
data class Feat611UserItem9(val user: CoreUser, val label: String)
data class Feat611UserItem10(val user: CoreUser, val label: String)

data class Feat611StateBlock1(val state: Feat611UiModel, val checksum: Int)
data class Feat611StateBlock2(val state: Feat611UiModel, val checksum: Int)
data class Feat611StateBlock3(val state: Feat611UiModel, val checksum: Int)
data class Feat611StateBlock4(val state: Feat611UiModel, val checksum: Int)
data class Feat611StateBlock5(val state: Feat611UiModel, val checksum: Int)
data class Feat611StateBlock6(val state: Feat611UiModel, val checksum: Int)
data class Feat611StateBlock7(val state: Feat611UiModel, val checksum: Int)
data class Feat611StateBlock8(val state: Feat611UiModel, val checksum: Int)
data class Feat611StateBlock9(val state: Feat611UiModel, val checksum: Int)
data class Feat611StateBlock10(val state: Feat611UiModel, val checksum: Int)

fun buildFeat611UserItem(user: CoreUser, index: Int): Feat611UserItem1 {
    return Feat611UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat611StateBlock(model: Feat611UiModel): Feat611StateBlock1 {
    return Feat611StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat611UserSummary> {
    val list = java.util.ArrayList<Feat611UserSummary>(users.size)
    for (user in users) {
        list += Feat611UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat611UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat611UiModel {
    val summaries = (0 until count).map {
        Feat611UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat611UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat611UiModel> {
    val models = java.util.ArrayList<Feat611UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat611AnalyticsEvent1(val name: String, val value: String)
data class Feat611AnalyticsEvent2(val name: String, val value: String)
data class Feat611AnalyticsEvent3(val name: String, val value: String)
data class Feat611AnalyticsEvent4(val name: String, val value: String)
data class Feat611AnalyticsEvent5(val name: String, val value: String)
data class Feat611AnalyticsEvent6(val name: String, val value: String)
data class Feat611AnalyticsEvent7(val name: String, val value: String)
data class Feat611AnalyticsEvent8(val name: String, val value: String)
data class Feat611AnalyticsEvent9(val name: String, val value: String)
data class Feat611AnalyticsEvent10(val name: String, val value: String)

fun logFeat611Event1(event: Feat611AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat611Event2(event: Feat611AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat611Event3(event: Feat611AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat611Event4(event: Feat611AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat611Event5(event: Feat611AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat611Event6(event: Feat611AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat611Event7(event: Feat611AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat611Event8(event: Feat611AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat611Event9(event: Feat611AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat611Event10(event: Feat611AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat611Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat611Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat611Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat611Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat611Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat611Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat611Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat611Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat611Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat611Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat611(u: CoreUser): Feat611Projection1 =
    Feat611Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat611Projection1> {
    val list = java.util.ArrayList<Feat611Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat611(u)
    }
    return list
}
