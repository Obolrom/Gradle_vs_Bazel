package com.romix.feature.feat444

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat444Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat444UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat444FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat444UserSummary
)

data class Feat444UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat444NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat444Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat444Config = Feat444Config()
) {

    fun loadSnapshot(userId: Long): Feat444NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat444NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat444UserSummary {
        return Feat444UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat444FeedItem> {
        val result = java.util.ArrayList<Feat444FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat444FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat444UiMapper {

    fun mapToUi(model: List<Feat444FeedItem>): Feat444UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat444UiModel(
            header = UiText("Feat444 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat444UiModel =
        Feat444UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat444UiModel =
        Feat444UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat444UiModel =
        Feat444UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat444Service(
    private val repository: Feat444Repository,
    private val uiMapper: Feat444UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat444UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat444UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat444UserItem1(val user: CoreUser, val label: String)
data class Feat444UserItem2(val user: CoreUser, val label: String)
data class Feat444UserItem3(val user: CoreUser, val label: String)
data class Feat444UserItem4(val user: CoreUser, val label: String)
data class Feat444UserItem5(val user: CoreUser, val label: String)
data class Feat444UserItem6(val user: CoreUser, val label: String)
data class Feat444UserItem7(val user: CoreUser, val label: String)
data class Feat444UserItem8(val user: CoreUser, val label: String)
data class Feat444UserItem9(val user: CoreUser, val label: String)
data class Feat444UserItem10(val user: CoreUser, val label: String)

data class Feat444StateBlock1(val state: Feat444UiModel, val checksum: Int)
data class Feat444StateBlock2(val state: Feat444UiModel, val checksum: Int)
data class Feat444StateBlock3(val state: Feat444UiModel, val checksum: Int)
data class Feat444StateBlock4(val state: Feat444UiModel, val checksum: Int)
data class Feat444StateBlock5(val state: Feat444UiModel, val checksum: Int)
data class Feat444StateBlock6(val state: Feat444UiModel, val checksum: Int)
data class Feat444StateBlock7(val state: Feat444UiModel, val checksum: Int)
data class Feat444StateBlock8(val state: Feat444UiModel, val checksum: Int)
data class Feat444StateBlock9(val state: Feat444UiModel, val checksum: Int)
data class Feat444StateBlock10(val state: Feat444UiModel, val checksum: Int)

fun buildFeat444UserItem(user: CoreUser, index: Int): Feat444UserItem1 {
    return Feat444UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat444StateBlock(model: Feat444UiModel): Feat444StateBlock1 {
    return Feat444StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat444UserSummary> {
    val list = java.util.ArrayList<Feat444UserSummary>(users.size)
    for (user in users) {
        list += Feat444UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat444UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat444UiModel {
    val summaries = (0 until count).map {
        Feat444UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat444UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat444UiModel> {
    val models = java.util.ArrayList<Feat444UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat444AnalyticsEvent1(val name: String, val value: String)
data class Feat444AnalyticsEvent2(val name: String, val value: String)
data class Feat444AnalyticsEvent3(val name: String, val value: String)
data class Feat444AnalyticsEvent4(val name: String, val value: String)
data class Feat444AnalyticsEvent5(val name: String, val value: String)
data class Feat444AnalyticsEvent6(val name: String, val value: String)
data class Feat444AnalyticsEvent7(val name: String, val value: String)
data class Feat444AnalyticsEvent8(val name: String, val value: String)
data class Feat444AnalyticsEvent9(val name: String, val value: String)
data class Feat444AnalyticsEvent10(val name: String, val value: String)

fun logFeat444Event1(event: Feat444AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat444Event2(event: Feat444AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat444Event3(event: Feat444AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat444Event4(event: Feat444AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat444Event5(event: Feat444AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat444Event6(event: Feat444AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat444Event7(event: Feat444AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat444Event8(event: Feat444AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat444Event9(event: Feat444AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat444Event10(event: Feat444AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat444Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat444Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat444Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat444Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat444Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat444Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat444Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat444Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat444Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat444Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat444(u: CoreUser): Feat444Projection1 =
    Feat444Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat444Projection1> {
    val list = java.util.ArrayList<Feat444Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat444(u)
    }
    return list
}
