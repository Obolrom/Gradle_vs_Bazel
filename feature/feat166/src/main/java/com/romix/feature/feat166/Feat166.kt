package com.romix.feature.feat166

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat166Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat166UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat166FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat166UserSummary
)

data class Feat166UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat166NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat166Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat166Config = Feat166Config()
) {

    fun loadSnapshot(userId: Long): Feat166NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat166NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat166UserSummary {
        return Feat166UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat166FeedItem> {
        val result = java.util.ArrayList<Feat166FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat166FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat166UiMapper {

    fun mapToUi(model: List<Feat166FeedItem>): Feat166UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat166UiModel(
            header = UiText("Feat166 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat166UiModel =
        Feat166UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat166UiModel =
        Feat166UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat166UiModel =
        Feat166UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat166Service(
    private val repository: Feat166Repository,
    private val uiMapper: Feat166UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat166UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat166UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat166UserItem1(val user: CoreUser, val label: String)
data class Feat166UserItem2(val user: CoreUser, val label: String)
data class Feat166UserItem3(val user: CoreUser, val label: String)
data class Feat166UserItem4(val user: CoreUser, val label: String)
data class Feat166UserItem5(val user: CoreUser, val label: String)
data class Feat166UserItem6(val user: CoreUser, val label: String)
data class Feat166UserItem7(val user: CoreUser, val label: String)
data class Feat166UserItem8(val user: CoreUser, val label: String)
data class Feat166UserItem9(val user: CoreUser, val label: String)
data class Feat166UserItem10(val user: CoreUser, val label: String)

data class Feat166StateBlock1(val state: Feat166UiModel, val checksum: Int)
data class Feat166StateBlock2(val state: Feat166UiModel, val checksum: Int)
data class Feat166StateBlock3(val state: Feat166UiModel, val checksum: Int)
data class Feat166StateBlock4(val state: Feat166UiModel, val checksum: Int)
data class Feat166StateBlock5(val state: Feat166UiModel, val checksum: Int)
data class Feat166StateBlock6(val state: Feat166UiModel, val checksum: Int)
data class Feat166StateBlock7(val state: Feat166UiModel, val checksum: Int)
data class Feat166StateBlock8(val state: Feat166UiModel, val checksum: Int)
data class Feat166StateBlock9(val state: Feat166UiModel, val checksum: Int)
data class Feat166StateBlock10(val state: Feat166UiModel, val checksum: Int)

fun buildFeat166UserItem(user: CoreUser, index: Int): Feat166UserItem1 {
    return Feat166UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat166StateBlock(model: Feat166UiModel): Feat166StateBlock1 {
    return Feat166StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat166UserSummary> {
    val list = java.util.ArrayList<Feat166UserSummary>(users.size)
    for (user in users) {
        list += Feat166UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat166UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat166UiModel {
    val summaries = (0 until count).map {
        Feat166UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat166UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat166UiModel> {
    val models = java.util.ArrayList<Feat166UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat166AnalyticsEvent1(val name: String, val value: String)
data class Feat166AnalyticsEvent2(val name: String, val value: String)
data class Feat166AnalyticsEvent3(val name: String, val value: String)
data class Feat166AnalyticsEvent4(val name: String, val value: String)
data class Feat166AnalyticsEvent5(val name: String, val value: String)
data class Feat166AnalyticsEvent6(val name: String, val value: String)
data class Feat166AnalyticsEvent7(val name: String, val value: String)
data class Feat166AnalyticsEvent8(val name: String, val value: String)
data class Feat166AnalyticsEvent9(val name: String, val value: String)
data class Feat166AnalyticsEvent10(val name: String, val value: String)

fun logFeat166Event1(event: Feat166AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat166Event2(event: Feat166AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat166Event3(event: Feat166AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat166Event4(event: Feat166AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat166Event5(event: Feat166AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat166Event6(event: Feat166AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat166Event7(event: Feat166AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat166Event8(event: Feat166AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat166Event9(event: Feat166AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat166Event10(event: Feat166AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat166Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat166Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat166Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat166Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat166Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat166Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat166Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat166Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat166Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat166Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat166(u: CoreUser): Feat166Projection1 =
    Feat166Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat166Projection1> {
    val list = java.util.ArrayList<Feat166Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat166(u)
    }
    return list
}
