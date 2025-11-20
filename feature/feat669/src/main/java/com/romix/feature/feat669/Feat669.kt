package com.romix.feature.feat669

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat669Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat669UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat669FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat669UserSummary
)

data class Feat669UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat669NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat669Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat669Config = Feat669Config()
) {

    fun loadSnapshot(userId: Long): Feat669NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat669NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat669UserSummary {
        return Feat669UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat669FeedItem> {
        val result = java.util.ArrayList<Feat669FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat669FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat669UiMapper {

    fun mapToUi(model: List<Feat669FeedItem>): Feat669UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat669UiModel(
            header = UiText("Feat669 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat669UiModel =
        Feat669UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat669UiModel =
        Feat669UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat669UiModel =
        Feat669UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat669Service(
    private val repository: Feat669Repository,
    private val uiMapper: Feat669UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat669UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat669UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat669UserItem1(val user: CoreUser, val label: String)
data class Feat669UserItem2(val user: CoreUser, val label: String)
data class Feat669UserItem3(val user: CoreUser, val label: String)
data class Feat669UserItem4(val user: CoreUser, val label: String)
data class Feat669UserItem5(val user: CoreUser, val label: String)
data class Feat669UserItem6(val user: CoreUser, val label: String)
data class Feat669UserItem7(val user: CoreUser, val label: String)
data class Feat669UserItem8(val user: CoreUser, val label: String)
data class Feat669UserItem9(val user: CoreUser, val label: String)
data class Feat669UserItem10(val user: CoreUser, val label: String)

data class Feat669StateBlock1(val state: Feat669UiModel, val checksum: Int)
data class Feat669StateBlock2(val state: Feat669UiModel, val checksum: Int)
data class Feat669StateBlock3(val state: Feat669UiModel, val checksum: Int)
data class Feat669StateBlock4(val state: Feat669UiModel, val checksum: Int)
data class Feat669StateBlock5(val state: Feat669UiModel, val checksum: Int)
data class Feat669StateBlock6(val state: Feat669UiModel, val checksum: Int)
data class Feat669StateBlock7(val state: Feat669UiModel, val checksum: Int)
data class Feat669StateBlock8(val state: Feat669UiModel, val checksum: Int)
data class Feat669StateBlock9(val state: Feat669UiModel, val checksum: Int)
data class Feat669StateBlock10(val state: Feat669UiModel, val checksum: Int)

fun buildFeat669UserItem(user: CoreUser, index: Int): Feat669UserItem1 {
    return Feat669UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat669StateBlock(model: Feat669UiModel): Feat669StateBlock1 {
    return Feat669StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat669UserSummary> {
    val list = java.util.ArrayList<Feat669UserSummary>(users.size)
    for (user in users) {
        list += Feat669UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat669UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat669UiModel {
    val summaries = (0 until count).map {
        Feat669UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat669UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat669UiModel> {
    val models = java.util.ArrayList<Feat669UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat669AnalyticsEvent1(val name: String, val value: String)
data class Feat669AnalyticsEvent2(val name: String, val value: String)
data class Feat669AnalyticsEvent3(val name: String, val value: String)
data class Feat669AnalyticsEvent4(val name: String, val value: String)
data class Feat669AnalyticsEvent5(val name: String, val value: String)
data class Feat669AnalyticsEvent6(val name: String, val value: String)
data class Feat669AnalyticsEvent7(val name: String, val value: String)
data class Feat669AnalyticsEvent8(val name: String, val value: String)
data class Feat669AnalyticsEvent9(val name: String, val value: String)
data class Feat669AnalyticsEvent10(val name: String, val value: String)

fun logFeat669Event1(event: Feat669AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat669Event2(event: Feat669AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat669Event3(event: Feat669AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat669Event4(event: Feat669AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat669Event5(event: Feat669AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat669Event6(event: Feat669AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat669Event7(event: Feat669AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat669Event8(event: Feat669AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat669Event9(event: Feat669AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat669Event10(event: Feat669AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat669Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat669Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat669Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat669Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat669Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat669Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat669Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat669Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat669Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat669Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat669(u: CoreUser): Feat669Projection1 =
    Feat669Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat669Projection1> {
    val list = java.util.ArrayList<Feat669Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat669(u)
    }
    return list
}
