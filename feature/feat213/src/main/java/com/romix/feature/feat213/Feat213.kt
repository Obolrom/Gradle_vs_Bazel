package com.romix.feature.feat213

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat213Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat213UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat213FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat213UserSummary
)

data class Feat213UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat213NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat213Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat213Config = Feat213Config()
) {

    fun loadSnapshot(userId: Long): Feat213NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat213NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat213UserSummary {
        return Feat213UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat213FeedItem> {
        val result = java.util.ArrayList<Feat213FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat213FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat213UiMapper {

    fun mapToUi(model: List<Feat213FeedItem>): Feat213UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat213UiModel(
            header = UiText("Feat213 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat213UiModel =
        Feat213UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat213UiModel =
        Feat213UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat213UiModel =
        Feat213UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat213Service(
    private val repository: Feat213Repository,
    private val uiMapper: Feat213UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat213UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat213UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat213UserItem1(val user: CoreUser, val label: String)
data class Feat213UserItem2(val user: CoreUser, val label: String)
data class Feat213UserItem3(val user: CoreUser, val label: String)
data class Feat213UserItem4(val user: CoreUser, val label: String)
data class Feat213UserItem5(val user: CoreUser, val label: String)
data class Feat213UserItem6(val user: CoreUser, val label: String)
data class Feat213UserItem7(val user: CoreUser, val label: String)
data class Feat213UserItem8(val user: CoreUser, val label: String)
data class Feat213UserItem9(val user: CoreUser, val label: String)
data class Feat213UserItem10(val user: CoreUser, val label: String)

data class Feat213StateBlock1(val state: Feat213UiModel, val checksum: Int)
data class Feat213StateBlock2(val state: Feat213UiModel, val checksum: Int)
data class Feat213StateBlock3(val state: Feat213UiModel, val checksum: Int)
data class Feat213StateBlock4(val state: Feat213UiModel, val checksum: Int)
data class Feat213StateBlock5(val state: Feat213UiModel, val checksum: Int)
data class Feat213StateBlock6(val state: Feat213UiModel, val checksum: Int)
data class Feat213StateBlock7(val state: Feat213UiModel, val checksum: Int)
data class Feat213StateBlock8(val state: Feat213UiModel, val checksum: Int)
data class Feat213StateBlock9(val state: Feat213UiModel, val checksum: Int)
data class Feat213StateBlock10(val state: Feat213UiModel, val checksum: Int)

fun buildFeat213UserItem(user: CoreUser, index: Int): Feat213UserItem1 {
    return Feat213UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat213StateBlock(model: Feat213UiModel): Feat213StateBlock1 {
    return Feat213StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat213UserSummary> {
    val list = java.util.ArrayList<Feat213UserSummary>(users.size)
    for (user in users) {
        list += Feat213UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat213UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat213UiModel {
    val summaries = (0 until count).map {
        Feat213UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat213UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat213UiModel> {
    val models = java.util.ArrayList<Feat213UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat213AnalyticsEvent1(val name: String, val value: String)
data class Feat213AnalyticsEvent2(val name: String, val value: String)
data class Feat213AnalyticsEvent3(val name: String, val value: String)
data class Feat213AnalyticsEvent4(val name: String, val value: String)
data class Feat213AnalyticsEvent5(val name: String, val value: String)
data class Feat213AnalyticsEvent6(val name: String, val value: String)
data class Feat213AnalyticsEvent7(val name: String, val value: String)
data class Feat213AnalyticsEvent8(val name: String, val value: String)
data class Feat213AnalyticsEvent9(val name: String, val value: String)
data class Feat213AnalyticsEvent10(val name: String, val value: String)

fun logFeat213Event1(event: Feat213AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat213Event2(event: Feat213AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat213Event3(event: Feat213AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat213Event4(event: Feat213AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat213Event5(event: Feat213AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat213Event6(event: Feat213AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat213Event7(event: Feat213AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat213Event8(event: Feat213AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat213Event9(event: Feat213AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat213Event10(event: Feat213AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat213Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat213Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat213Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat213Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat213Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat213Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat213Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat213Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat213Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat213Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat213(u: CoreUser): Feat213Projection1 =
    Feat213Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat213Projection1> {
    val list = java.util.ArrayList<Feat213Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat213(u)
    }
    return list
}
