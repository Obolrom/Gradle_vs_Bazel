package com.romix.feature.feat681

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat681Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat681UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat681FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat681UserSummary
)

data class Feat681UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat681NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat681Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat681Config = Feat681Config()
) {

    fun loadSnapshot(userId: Long): Feat681NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat681NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat681UserSummary {
        return Feat681UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat681FeedItem> {
        val result = java.util.ArrayList<Feat681FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat681FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat681UiMapper {

    fun mapToUi(model: List<Feat681FeedItem>): Feat681UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat681UiModel(
            header = UiText("Feat681 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat681UiModel =
        Feat681UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat681UiModel =
        Feat681UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat681UiModel =
        Feat681UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat681Service(
    private val repository: Feat681Repository,
    private val uiMapper: Feat681UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat681UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat681UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat681UserItem1(val user: CoreUser, val label: String)
data class Feat681UserItem2(val user: CoreUser, val label: String)
data class Feat681UserItem3(val user: CoreUser, val label: String)
data class Feat681UserItem4(val user: CoreUser, val label: String)
data class Feat681UserItem5(val user: CoreUser, val label: String)
data class Feat681UserItem6(val user: CoreUser, val label: String)
data class Feat681UserItem7(val user: CoreUser, val label: String)
data class Feat681UserItem8(val user: CoreUser, val label: String)
data class Feat681UserItem9(val user: CoreUser, val label: String)
data class Feat681UserItem10(val user: CoreUser, val label: String)

data class Feat681StateBlock1(val state: Feat681UiModel, val checksum: Int)
data class Feat681StateBlock2(val state: Feat681UiModel, val checksum: Int)
data class Feat681StateBlock3(val state: Feat681UiModel, val checksum: Int)
data class Feat681StateBlock4(val state: Feat681UiModel, val checksum: Int)
data class Feat681StateBlock5(val state: Feat681UiModel, val checksum: Int)
data class Feat681StateBlock6(val state: Feat681UiModel, val checksum: Int)
data class Feat681StateBlock7(val state: Feat681UiModel, val checksum: Int)
data class Feat681StateBlock8(val state: Feat681UiModel, val checksum: Int)
data class Feat681StateBlock9(val state: Feat681UiModel, val checksum: Int)
data class Feat681StateBlock10(val state: Feat681UiModel, val checksum: Int)

fun buildFeat681UserItem(user: CoreUser, index: Int): Feat681UserItem1 {
    return Feat681UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat681StateBlock(model: Feat681UiModel): Feat681StateBlock1 {
    return Feat681StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat681UserSummary> {
    val list = java.util.ArrayList<Feat681UserSummary>(users.size)
    for (user in users) {
        list += Feat681UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat681UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat681UiModel {
    val summaries = (0 until count).map {
        Feat681UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat681UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat681UiModel> {
    val models = java.util.ArrayList<Feat681UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat681AnalyticsEvent1(val name: String, val value: String)
data class Feat681AnalyticsEvent2(val name: String, val value: String)
data class Feat681AnalyticsEvent3(val name: String, val value: String)
data class Feat681AnalyticsEvent4(val name: String, val value: String)
data class Feat681AnalyticsEvent5(val name: String, val value: String)
data class Feat681AnalyticsEvent6(val name: String, val value: String)
data class Feat681AnalyticsEvent7(val name: String, val value: String)
data class Feat681AnalyticsEvent8(val name: String, val value: String)
data class Feat681AnalyticsEvent9(val name: String, val value: String)
data class Feat681AnalyticsEvent10(val name: String, val value: String)

fun logFeat681Event1(event: Feat681AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat681Event2(event: Feat681AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat681Event3(event: Feat681AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat681Event4(event: Feat681AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat681Event5(event: Feat681AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat681Event6(event: Feat681AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat681Event7(event: Feat681AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat681Event8(event: Feat681AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat681Event9(event: Feat681AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat681Event10(event: Feat681AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat681Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat681Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat681Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat681Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat681Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat681Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat681Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat681Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat681Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat681Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat681(u: CoreUser): Feat681Projection1 =
    Feat681Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat681Projection1> {
    val list = java.util.ArrayList<Feat681Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat681(u)
    }
    return list
}
