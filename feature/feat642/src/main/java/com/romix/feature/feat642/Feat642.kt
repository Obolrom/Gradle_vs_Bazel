package com.romix.feature.feat642

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat642Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat642UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat642FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat642UserSummary
)

data class Feat642UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat642NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat642Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat642Config = Feat642Config()
) {

    fun loadSnapshot(userId: Long): Feat642NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat642NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat642UserSummary {
        return Feat642UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat642FeedItem> {
        val result = java.util.ArrayList<Feat642FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat642FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat642UiMapper {

    fun mapToUi(model: List<Feat642FeedItem>): Feat642UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat642UiModel(
            header = UiText("Feat642 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat642UiModel =
        Feat642UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat642UiModel =
        Feat642UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat642UiModel =
        Feat642UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat642Service(
    private val repository: Feat642Repository,
    private val uiMapper: Feat642UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat642UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat642UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat642UserItem1(val user: CoreUser, val label: String)
data class Feat642UserItem2(val user: CoreUser, val label: String)
data class Feat642UserItem3(val user: CoreUser, val label: String)
data class Feat642UserItem4(val user: CoreUser, val label: String)
data class Feat642UserItem5(val user: CoreUser, val label: String)
data class Feat642UserItem6(val user: CoreUser, val label: String)
data class Feat642UserItem7(val user: CoreUser, val label: String)
data class Feat642UserItem8(val user: CoreUser, val label: String)
data class Feat642UserItem9(val user: CoreUser, val label: String)
data class Feat642UserItem10(val user: CoreUser, val label: String)

data class Feat642StateBlock1(val state: Feat642UiModel, val checksum: Int)
data class Feat642StateBlock2(val state: Feat642UiModel, val checksum: Int)
data class Feat642StateBlock3(val state: Feat642UiModel, val checksum: Int)
data class Feat642StateBlock4(val state: Feat642UiModel, val checksum: Int)
data class Feat642StateBlock5(val state: Feat642UiModel, val checksum: Int)
data class Feat642StateBlock6(val state: Feat642UiModel, val checksum: Int)
data class Feat642StateBlock7(val state: Feat642UiModel, val checksum: Int)
data class Feat642StateBlock8(val state: Feat642UiModel, val checksum: Int)
data class Feat642StateBlock9(val state: Feat642UiModel, val checksum: Int)
data class Feat642StateBlock10(val state: Feat642UiModel, val checksum: Int)

fun buildFeat642UserItem(user: CoreUser, index: Int): Feat642UserItem1 {
    return Feat642UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat642StateBlock(model: Feat642UiModel): Feat642StateBlock1 {
    return Feat642StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat642UserSummary> {
    val list = java.util.ArrayList<Feat642UserSummary>(users.size)
    for (user in users) {
        list += Feat642UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat642UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat642UiModel {
    val summaries = (0 until count).map {
        Feat642UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat642UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat642UiModel> {
    val models = java.util.ArrayList<Feat642UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat642AnalyticsEvent1(val name: String, val value: String)
data class Feat642AnalyticsEvent2(val name: String, val value: String)
data class Feat642AnalyticsEvent3(val name: String, val value: String)
data class Feat642AnalyticsEvent4(val name: String, val value: String)
data class Feat642AnalyticsEvent5(val name: String, val value: String)
data class Feat642AnalyticsEvent6(val name: String, val value: String)
data class Feat642AnalyticsEvent7(val name: String, val value: String)
data class Feat642AnalyticsEvent8(val name: String, val value: String)
data class Feat642AnalyticsEvent9(val name: String, val value: String)
data class Feat642AnalyticsEvent10(val name: String, val value: String)

fun logFeat642Event1(event: Feat642AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat642Event2(event: Feat642AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat642Event3(event: Feat642AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat642Event4(event: Feat642AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat642Event5(event: Feat642AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat642Event6(event: Feat642AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat642Event7(event: Feat642AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat642Event8(event: Feat642AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat642Event9(event: Feat642AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat642Event10(event: Feat642AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat642Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat642Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat642Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat642Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat642Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat642Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat642Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat642Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat642Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat642Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat642(u: CoreUser): Feat642Projection1 =
    Feat642Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat642Projection1> {
    val list = java.util.ArrayList<Feat642Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat642(u)
    }
    return list
}
