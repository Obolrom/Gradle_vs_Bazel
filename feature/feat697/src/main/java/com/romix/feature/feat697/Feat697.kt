package com.romix.feature.feat697

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat697Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat697UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat697FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat697UserSummary
)

data class Feat697UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat697NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat697Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat697Config = Feat697Config()
) {

    fun loadSnapshot(userId: Long): Feat697NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat697NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat697UserSummary {
        return Feat697UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat697FeedItem> {
        val result = java.util.ArrayList<Feat697FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat697FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat697UiMapper {

    fun mapToUi(model: List<Feat697FeedItem>): Feat697UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat697UiModel(
            header = UiText("Feat697 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat697UiModel =
        Feat697UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat697UiModel =
        Feat697UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat697UiModel =
        Feat697UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat697Service(
    private val repository: Feat697Repository,
    private val uiMapper: Feat697UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat697UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat697UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat697UserItem1(val user: CoreUser, val label: String)
data class Feat697UserItem2(val user: CoreUser, val label: String)
data class Feat697UserItem3(val user: CoreUser, val label: String)
data class Feat697UserItem4(val user: CoreUser, val label: String)
data class Feat697UserItem5(val user: CoreUser, val label: String)
data class Feat697UserItem6(val user: CoreUser, val label: String)
data class Feat697UserItem7(val user: CoreUser, val label: String)
data class Feat697UserItem8(val user: CoreUser, val label: String)
data class Feat697UserItem9(val user: CoreUser, val label: String)
data class Feat697UserItem10(val user: CoreUser, val label: String)

data class Feat697StateBlock1(val state: Feat697UiModel, val checksum: Int)
data class Feat697StateBlock2(val state: Feat697UiModel, val checksum: Int)
data class Feat697StateBlock3(val state: Feat697UiModel, val checksum: Int)
data class Feat697StateBlock4(val state: Feat697UiModel, val checksum: Int)
data class Feat697StateBlock5(val state: Feat697UiModel, val checksum: Int)
data class Feat697StateBlock6(val state: Feat697UiModel, val checksum: Int)
data class Feat697StateBlock7(val state: Feat697UiModel, val checksum: Int)
data class Feat697StateBlock8(val state: Feat697UiModel, val checksum: Int)
data class Feat697StateBlock9(val state: Feat697UiModel, val checksum: Int)
data class Feat697StateBlock10(val state: Feat697UiModel, val checksum: Int)

fun buildFeat697UserItem(user: CoreUser, index: Int): Feat697UserItem1 {
    return Feat697UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat697StateBlock(model: Feat697UiModel): Feat697StateBlock1 {
    return Feat697StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat697UserSummary> {
    val list = java.util.ArrayList<Feat697UserSummary>(users.size)
    for (user in users) {
        list += Feat697UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat697UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat697UiModel {
    val summaries = (0 until count).map {
        Feat697UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat697UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat697UiModel> {
    val models = java.util.ArrayList<Feat697UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat697AnalyticsEvent1(val name: String, val value: String)
data class Feat697AnalyticsEvent2(val name: String, val value: String)
data class Feat697AnalyticsEvent3(val name: String, val value: String)
data class Feat697AnalyticsEvent4(val name: String, val value: String)
data class Feat697AnalyticsEvent5(val name: String, val value: String)
data class Feat697AnalyticsEvent6(val name: String, val value: String)
data class Feat697AnalyticsEvent7(val name: String, val value: String)
data class Feat697AnalyticsEvent8(val name: String, val value: String)
data class Feat697AnalyticsEvent9(val name: String, val value: String)
data class Feat697AnalyticsEvent10(val name: String, val value: String)

fun logFeat697Event1(event: Feat697AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat697Event2(event: Feat697AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat697Event3(event: Feat697AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat697Event4(event: Feat697AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat697Event5(event: Feat697AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat697Event6(event: Feat697AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat697Event7(event: Feat697AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat697Event8(event: Feat697AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat697Event9(event: Feat697AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat697Event10(event: Feat697AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat697Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat697Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat697Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat697Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat697Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat697Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat697Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat697Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat697Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat697Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat697(u: CoreUser): Feat697Projection1 =
    Feat697Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat697Projection1> {
    val list = java.util.ArrayList<Feat697Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat697(u)
    }
    return list
}
