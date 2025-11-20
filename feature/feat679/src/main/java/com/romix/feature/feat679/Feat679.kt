package com.romix.feature.feat679

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat679Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat679UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat679FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat679UserSummary
)

data class Feat679UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat679NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat679Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat679Config = Feat679Config()
) {

    fun loadSnapshot(userId: Long): Feat679NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat679NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat679UserSummary {
        return Feat679UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat679FeedItem> {
        val result = java.util.ArrayList<Feat679FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat679FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat679UiMapper {

    fun mapToUi(model: List<Feat679FeedItem>): Feat679UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat679UiModel(
            header = UiText("Feat679 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat679UiModel =
        Feat679UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat679UiModel =
        Feat679UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat679UiModel =
        Feat679UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat679Service(
    private val repository: Feat679Repository,
    private val uiMapper: Feat679UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat679UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat679UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat679UserItem1(val user: CoreUser, val label: String)
data class Feat679UserItem2(val user: CoreUser, val label: String)
data class Feat679UserItem3(val user: CoreUser, val label: String)
data class Feat679UserItem4(val user: CoreUser, val label: String)
data class Feat679UserItem5(val user: CoreUser, val label: String)
data class Feat679UserItem6(val user: CoreUser, val label: String)
data class Feat679UserItem7(val user: CoreUser, val label: String)
data class Feat679UserItem8(val user: CoreUser, val label: String)
data class Feat679UserItem9(val user: CoreUser, val label: String)
data class Feat679UserItem10(val user: CoreUser, val label: String)

data class Feat679StateBlock1(val state: Feat679UiModel, val checksum: Int)
data class Feat679StateBlock2(val state: Feat679UiModel, val checksum: Int)
data class Feat679StateBlock3(val state: Feat679UiModel, val checksum: Int)
data class Feat679StateBlock4(val state: Feat679UiModel, val checksum: Int)
data class Feat679StateBlock5(val state: Feat679UiModel, val checksum: Int)
data class Feat679StateBlock6(val state: Feat679UiModel, val checksum: Int)
data class Feat679StateBlock7(val state: Feat679UiModel, val checksum: Int)
data class Feat679StateBlock8(val state: Feat679UiModel, val checksum: Int)
data class Feat679StateBlock9(val state: Feat679UiModel, val checksum: Int)
data class Feat679StateBlock10(val state: Feat679UiModel, val checksum: Int)

fun buildFeat679UserItem(user: CoreUser, index: Int): Feat679UserItem1 {
    return Feat679UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat679StateBlock(model: Feat679UiModel): Feat679StateBlock1 {
    return Feat679StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat679UserSummary> {
    val list = java.util.ArrayList<Feat679UserSummary>(users.size)
    for (user in users) {
        list += Feat679UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat679UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat679UiModel {
    val summaries = (0 until count).map {
        Feat679UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat679UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat679UiModel> {
    val models = java.util.ArrayList<Feat679UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat679AnalyticsEvent1(val name: String, val value: String)
data class Feat679AnalyticsEvent2(val name: String, val value: String)
data class Feat679AnalyticsEvent3(val name: String, val value: String)
data class Feat679AnalyticsEvent4(val name: String, val value: String)
data class Feat679AnalyticsEvent5(val name: String, val value: String)
data class Feat679AnalyticsEvent6(val name: String, val value: String)
data class Feat679AnalyticsEvent7(val name: String, val value: String)
data class Feat679AnalyticsEvent8(val name: String, val value: String)
data class Feat679AnalyticsEvent9(val name: String, val value: String)
data class Feat679AnalyticsEvent10(val name: String, val value: String)

fun logFeat679Event1(event: Feat679AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat679Event2(event: Feat679AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat679Event3(event: Feat679AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat679Event4(event: Feat679AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat679Event5(event: Feat679AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat679Event6(event: Feat679AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat679Event7(event: Feat679AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat679Event8(event: Feat679AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat679Event9(event: Feat679AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat679Event10(event: Feat679AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat679Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat679Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat679Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat679Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat679Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat679Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat679Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat679Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat679Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat679Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat679(u: CoreUser): Feat679Projection1 =
    Feat679Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat679Projection1> {
    val list = java.util.ArrayList<Feat679Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat679(u)
    }
    return list
}
