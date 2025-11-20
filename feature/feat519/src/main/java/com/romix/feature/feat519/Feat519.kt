package com.romix.feature.feat519

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat519Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat519UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat519FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat519UserSummary
)

data class Feat519UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat519NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat519Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat519Config = Feat519Config()
) {

    fun loadSnapshot(userId: Long): Feat519NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat519NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat519UserSummary {
        return Feat519UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat519FeedItem> {
        val result = java.util.ArrayList<Feat519FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat519FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat519UiMapper {

    fun mapToUi(model: List<Feat519FeedItem>): Feat519UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat519UiModel(
            header = UiText("Feat519 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat519UiModel =
        Feat519UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat519UiModel =
        Feat519UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat519UiModel =
        Feat519UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat519Service(
    private val repository: Feat519Repository,
    private val uiMapper: Feat519UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat519UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat519UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat519UserItem1(val user: CoreUser, val label: String)
data class Feat519UserItem2(val user: CoreUser, val label: String)
data class Feat519UserItem3(val user: CoreUser, val label: String)
data class Feat519UserItem4(val user: CoreUser, val label: String)
data class Feat519UserItem5(val user: CoreUser, val label: String)
data class Feat519UserItem6(val user: CoreUser, val label: String)
data class Feat519UserItem7(val user: CoreUser, val label: String)
data class Feat519UserItem8(val user: CoreUser, val label: String)
data class Feat519UserItem9(val user: CoreUser, val label: String)
data class Feat519UserItem10(val user: CoreUser, val label: String)

data class Feat519StateBlock1(val state: Feat519UiModel, val checksum: Int)
data class Feat519StateBlock2(val state: Feat519UiModel, val checksum: Int)
data class Feat519StateBlock3(val state: Feat519UiModel, val checksum: Int)
data class Feat519StateBlock4(val state: Feat519UiModel, val checksum: Int)
data class Feat519StateBlock5(val state: Feat519UiModel, val checksum: Int)
data class Feat519StateBlock6(val state: Feat519UiModel, val checksum: Int)
data class Feat519StateBlock7(val state: Feat519UiModel, val checksum: Int)
data class Feat519StateBlock8(val state: Feat519UiModel, val checksum: Int)
data class Feat519StateBlock9(val state: Feat519UiModel, val checksum: Int)
data class Feat519StateBlock10(val state: Feat519UiModel, val checksum: Int)

fun buildFeat519UserItem(user: CoreUser, index: Int): Feat519UserItem1 {
    return Feat519UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat519StateBlock(model: Feat519UiModel): Feat519StateBlock1 {
    return Feat519StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat519UserSummary> {
    val list = java.util.ArrayList<Feat519UserSummary>(users.size)
    for (user in users) {
        list += Feat519UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat519UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat519UiModel {
    val summaries = (0 until count).map {
        Feat519UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat519UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat519UiModel> {
    val models = java.util.ArrayList<Feat519UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat519AnalyticsEvent1(val name: String, val value: String)
data class Feat519AnalyticsEvent2(val name: String, val value: String)
data class Feat519AnalyticsEvent3(val name: String, val value: String)
data class Feat519AnalyticsEvent4(val name: String, val value: String)
data class Feat519AnalyticsEvent5(val name: String, val value: String)
data class Feat519AnalyticsEvent6(val name: String, val value: String)
data class Feat519AnalyticsEvent7(val name: String, val value: String)
data class Feat519AnalyticsEvent8(val name: String, val value: String)
data class Feat519AnalyticsEvent9(val name: String, val value: String)
data class Feat519AnalyticsEvent10(val name: String, val value: String)

fun logFeat519Event1(event: Feat519AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat519Event2(event: Feat519AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat519Event3(event: Feat519AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat519Event4(event: Feat519AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat519Event5(event: Feat519AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat519Event6(event: Feat519AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat519Event7(event: Feat519AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat519Event8(event: Feat519AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat519Event9(event: Feat519AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat519Event10(event: Feat519AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat519Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat519Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat519Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat519Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat519Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat519Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat519Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat519Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat519Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat519Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat519(u: CoreUser): Feat519Projection1 =
    Feat519Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat519Projection1> {
    val list = java.util.ArrayList<Feat519Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat519(u)
    }
    return list
}
