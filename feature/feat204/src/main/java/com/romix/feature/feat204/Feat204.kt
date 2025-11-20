package com.romix.feature.feat204

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat204Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat204UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat204FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat204UserSummary
)

data class Feat204UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat204NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat204Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat204Config = Feat204Config()
) {

    fun loadSnapshot(userId: Long): Feat204NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat204NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat204UserSummary {
        return Feat204UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat204FeedItem> {
        val result = java.util.ArrayList<Feat204FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat204FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat204UiMapper {

    fun mapToUi(model: List<Feat204FeedItem>): Feat204UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat204UiModel(
            header = UiText("Feat204 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat204UiModel =
        Feat204UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat204UiModel =
        Feat204UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat204UiModel =
        Feat204UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat204Service(
    private val repository: Feat204Repository,
    private val uiMapper: Feat204UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat204UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat204UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat204UserItem1(val user: CoreUser, val label: String)
data class Feat204UserItem2(val user: CoreUser, val label: String)
data class Feat204UserItem3(val user: CoreUser, val label: String)
data class Feat204UserItem4(val user: CoreUser, val label: String)
data class Feat204UserItem5(val user: CoreUser, val label: String)
data class Feat204UserItem6(val user: CoreUser, val label: String)
data class Feat204UserItem7(val user: CoreUser, val label: String)
data class Feat204UserItem8(val user: CoreUser, val label: String)
data class Feat204UserItem9(val user: CoreUser, val label: String)
data class Feat204UserItem10(val user: CoreUser, val label: String)

data class Feat204StateBlock1(val state: Feat204UiModel, val checksum: Int)
data class Feat204StateBlock2(val state: Feat204UiModel, val checksum: Int)
data class Feat204StateBlock3(val state: Feat204UiModel, val checksum: Int)
data class Feat204StateBlock4(val state: Feat204UiModel, val checksum: Int)
data class Feat204StateBlock5(val state: Feat204UiModel, val checksum: Int)
data class Feat204StateBlock6(val state: Feat204UiModel, val checksum: Int)
data class Feat204StateBlock7(val state: Feat204UiModel, val checksum: Int)
data class Feat204StateBlock8(val state: Feat204UiModel, val checksum: Int)
data class Feat204StateBlock9(val state: Feat204UiModel, val checksum: Int)
data class Feat204StateBlock10(val state: Feat204UiModel, val checksum: Int)

fun buildFeat204UserItem(user: CoreUser, index: Int): Feat204UserItem1 {
    return Feat204UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat204StateBlock(model: Feat204UiModel): Feat204StateBlock1 {
    return Feat204StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat204UserSummary> {
    val list = java.util.ArrayList<Feat204UserSummary>(users.size)
    for (user in users) {
        list += Feat204UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat204UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat204UiModel {
    val summaries = (0 until count).map {
        Feat204UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat204UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat204UiModel> {
    val models = java.util.ArrayList<Feat204UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat204AnalyticsEvent1(val name: String, val value: String)
data class Feat204AnalyticsEvent2(val name: String, val value: String)
data class Feat204AnalyticsEvent3(val name: String, val value: String)
data class Feat204AnalyticsEvent4(val name: String, val value: String)
data class Feat204AnalyticsEvent5(val name: String, val value: String)
data class Feat204AnalyticsEvent6(val name: String, val value: String)
data class Feat204AnalyticsEvent7(val name: String, val value: String)
data class Feat204AnalyticsEvent8(val name: String, val value: String)
data class Feat204AnalyticsEvent9(val name: String, val value: String)
data class Feat204AnalyticsEvent10(val name: String, val value: String)

fun logFeat204Event1(event: Feat204AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat204Event2(event: Feat204AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat204Event3(event: Feat204AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat204Event4(event: Feat204AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat204Event5(event: Feat204AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat204Event6(event: Feat204AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat204Event7(event: Feat204AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat204Event8(event: Feat204AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat204Event9(event: Feat204AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat204Event10(event: Feat204AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat204Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat204Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat204Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat204Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat204Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat204Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat204Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat204Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat204Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat204Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat204(u: CoreUser): Feat204Projection1 =
    Feat204Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat204Projection1> {
    val list = java.util.ArrayList<Feat204Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat204(u)
    }
    return list
}
