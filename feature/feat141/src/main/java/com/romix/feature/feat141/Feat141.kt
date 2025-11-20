package com.romix.feature.feat141

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat141Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat141UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat141FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat141UserSummary
)

data class Feat141UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat141NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat141Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat141Config = Feat141Config()
) {

    fun loadSnapshot(userId: Long): Feat141NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat141NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat141UserSummary {
        return Feat141UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat141FeedItem> {
        val result = java.util.ArrayList<Feat141FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat141FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat141UiMapper {

    fun mapToUi(model: List<Feat141FeedItem>): Feat141UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat141UiModel(
            header = UiText("Feat141 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat141UiModel =
        Feat141UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat141UiModel =
        Feat141UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat141UiModel =
        Feat141UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat141Service(
    private val repository: Feat141Repository,
    private val uiMapper: Feat141UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat141UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat141UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat141UserItem1(val user: CoreUser, val label: String)
data class Feat141UserItem2(val user: CoreUser, val label: String)
data class Feat141UserItem3(val user: CoreUser, val label: String)
data class Feat141UserItem4(val user: CoreUser, val label: String)
data class Feat141UserItem5(val user: CoreUser, val label: String)
data class Feat141UserItem6(val user: CoreUser, val label: String)
data class Feat141UserItem7(val user: CoreUser, val label: String)
data class Feat141UserItem8(val user: CoreUser, val label: String)
data class Feat141UserItem9(val user: CoreUser, val label: String)
data class Feat141UserItem10(val user: CoreUser, val label: String)

data class Feat141StateBlock1(val state: Feat141UiModel, val checksum: Int)
data class Feat141StateBlock2(val state: Feat141UiModel, val checksum: Int)
data class Feat141StateBlock3(val state: Feat141UiModel, val checksum: Int)
data class Feat141StateBlock4(val state: Feat141UiModel, val checksum: Int)
data class Feat141StateBlock5(val state: Feat141UiModel, val checksum: Int)
data class Feat141StateBlock6(val state: Feat141UiModel, val checksum: Int)
data class Feat141StateBlock7(val state: Feat141UiModel, val checksum: Int)
data class Feat141StateBlock8(val state: Feat141UiModel, val checksum: Int)
data class Feat141StateBlock9(val state: Feat141UiModel, val checksum: Int)
data class Feat141StateBlock10(val state: Feat141UiModel, val checksum: Int)

fun buildFeat141UserItem(user: CoreUser, index: Int): Feat141UserItem1 {
    return Feat141UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat141StateBlock(model: Feat141UiModel): Feat141StateBlock1 {
    return Feat141StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat141UserSummary> {
    val list = java.util.ArrayList<Feat141UserSummary>(users.size)
    for (user in users) {
        list += Feat141UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat141UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat141UiModel {
    val summaries = (0 until count).map {
        Feat141UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat141UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat141UiModel> {
    val models = java.util.ArrayList<Feat141UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat141AnalyticsEvent1(val name: String, val value: String)
data class Feat141AnalyticsEvent2(val name: String, val value: String)
data class Feat141AnalyticsEvent3(val name: String, val value: String)
data class Feat141AnalyticsEvent4(val name: String, val value: String)
data class Feat141AnalyticsEvent5(val name: String, val value: String)
data class Feat141AnalyticsEvent6(val name: String, val value: String)
data class Feat141AnalyticsEvent7(val name: String, val value: String)
data class Feat141AnalyticsEvent8(val name: String, val value: String)
data class Feat141AnalyticsEvent9(val name: String, val value: String)
data class Feat141AnalyticsEvent10(val name: String, val value: String)

fun logFeat141Event1(event: Feat141AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat141Event2(event: Feat141AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat141Event3(event: Feat141AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat141Event4(event: Feat141AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat141Event5(event: Feat141AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat141Event6(event: Feat141AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat141Event7(event: Feat141AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat141Event8(event: Feat141AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat141Event9(event: Feat141AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat141Event10(event: Feat141AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat141Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat141Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat141Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat141Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat141Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat141Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat141Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat141Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat141Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat141Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat141(u: CoreUser): Feat141Projection1 =
    Feat141Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat141Projection1> {
    val list = java.util.ArrayList<Feat141Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat141(u)
    }
    return list
}
