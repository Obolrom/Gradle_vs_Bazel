package com.romix.feature.feat39

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat39Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat39UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat39FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat39UserSummary
)

data class Feat39UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat39NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat39Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat39Config = Feat39Config()
) {

    fun loadSnapshot(userId: Long): Feat39NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat39NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat39UserSummary {
        return Feat39UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat39FeedItem> {
        val result = java.util.ArrayList<Feat39FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat39FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat39UiMapper {

    fun mapToUi(model: List<Feat39FeedItem>): Feat39UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat39UiModel(
            header = UiText("Feat39 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat39UiModel =
        Feat39UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat39UiModel =
        Feat39UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat39UiModel =
        Feat39UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat39Service(
    private val repository: Feat39Repository,
    private val uiMapper: Feat39UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat39UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat39UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat39UserItem1(val user: CoreUser, val label: String)
data class Feat39UserItem2(val user: CoreUser, val label: String)
data class Feat39UserItem3(val user: CoreUser, val label: String)
data class Feat39UserItem4(val user: CoreUser, val label: String)
data class Feat39UserItem5(val user: CoreUser, val label: String)
data class Feat39UserItem6(val user: CoreUser, val label: String)
data class Feat39UserItem7(val user: CoreUser, val label: String)
data class Feat39UserItem8(val user: CoreUser, val label: String)
data class Feat39UserItem9(val user: CoreUser, val label: String)
data class Feat39UserItem10(val user: CoreUser, val label: String)

data class Feat39StateBlock1(val state: Feat39UiModel, val checksum: Int)
data class Feat39StateBlock2(val state: Feat39UiModel, val checksum: Int)
data class Feat39StateBlock3(val state: Feat39UiModel, val checksum: Int)
data class Feat39StateBlock4(val state: Feat39UiModel, val checksum: Int)
data class Feat39StateBlock5(val state: Feat39UiModel, val checksum: Int)
data class Feat39StateBlock6(val state: Feat39UiModel, val checksum: Int)
data class Feat39StateBlock7(val state: Feat39UiModel, val checksum: Int)
data class Feat39StateBlock8(val state: Feat39UiModel, val checksum: Int)
data class Feat39StateBlock9(val state: Feat39UiModel, val checksum: Int)
data class Feat39StateBlock10(val state: Feat39UiModel, val checksum: Int)

fun buildFeat39UserItem(user: CoreUser, index: Int): Feat39UserItem1 {
    return Feat39UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat39StateBlock(model: Feat39UiModel): Feat39StateBlock1 {
    return Feat39StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat39UserSummary> {
    val list = java.util.ArrayList<Feat39UserSummary>(users.size)
    for (user in users) {
        list += Feat39UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat39UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat39UiModel {
    val summaries = (0 until count).map {
        Feat39UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat39UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat39UiModel> {
    val models = java.util.ArrayList<Feat39UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat39AnalyticsEvent1(val name: String, val value: String)
data class Feat39AnalyticsEvent2(val name: String, val value: String)
data class Feat39AnalyticsEvent3(val name: String, val value: String)
data class Feat39AnalyticsEvent4(val name: String, val value: String)
data class Feat39AnalyticsEvent5(val name: String, val value: String)
data class Feat39AnalyticsEvent6(val name: String, val value: String)
data class Feat39AnalyticsEvent7(val name: String, val value: String)
data class Feat39AnalyticsEvent8(val name: String, val value: String)
data class Feat39AnalyticsEvent9(val name: String, val value: String)
data class Feat39AnalyticsEvent10(val name: String, val value: String)

fun logFeat39Event1(event: Feat39AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat39Event2(event: Feat39AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat39Event3(event: Feat39AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat39Event4(event: Feat39AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat39Event5(event: Feat39AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat39Event6(event: Feat39AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat39Event7(event: Feat39AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat39Event8(event: Feat39AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat39Event9(event: Feat39AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat39Event10(event: Feat39AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat39Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat39Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat39Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat39Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat39Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat39Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat39Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat39Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat39Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat39Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat39(u: CoreUser): Feat39Projection1 =
    Feat39Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat39Projection1> {
    val list = java.util.ArrayList<Feat39Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat39(u)
    }
    return list
}
