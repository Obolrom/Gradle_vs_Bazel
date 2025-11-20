package com.romix.feature.feat262

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat262Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat262UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat262FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat262UserSummary
)

data class Feat262UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat262NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat262Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat262Config = Feat262Config()
) {

    fun loadSnapshot(userId: Long): Feat262NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat262NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat262UserSummary {
        return Feat262UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat262FeedItem> {
        val result = java.util.ArrayList<Feat262FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat262FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat262UiMapper {

    fun mapToUi(model: List<Feat262FeedItem>): Feat262UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat262UiModel(
            header = UiText("Feat262 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat262UiModel =
        Feat262UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat262UiModel =
        Feat262UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat262UiModel =
        Feat262UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat262Service(
    private val repository: Feat262Repository,
    private val uiMapper: Feat262UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat262UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat262UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat262UserItem1(val user: CoreUser, val label: String)
data class Feat262UserItem2(val user: CoreUser, val label: String)
data class Feat262UserItem3(val user: CoreUser, val label: String)
data class Feat262UserItem4(val user: CoreUser, val label: String)
data class Feat262UserItem5(val user: CoreUser, val label: String)
data class Feat262UserItem6(val user: CoreUser, val label: String)
data class Feat262UserItem7(val user: CoreUser, val label: String)
data class Feat262UserItem8(val user: CoreUser, val label: String)
data class Feat262UserItem9(val user: CoreUser, val label: String)
data class Feat262UserItem10(val user: CoreUser, val label: String)

data class Feat262StateBlock1(val state: Feat262UiModel, val checksum: Int)
data class Feat262StateBlock2(val state: Feat262UiModel, val checksum: Int)
data class Feat262StateBlock3(val state: Feat262UiModel, val checksum: Int)
data class Feat262StateBlock4(val state: Feat262UiModel, val checksum: Int)
data class Feat262StateBlock5(val state: Feat262UiModel, val checksum: Int)
data class Feat262StateBlock6(val state: Feat262UiModel, val checksum: Int)
data class Feat262StateBlock7(val state: Feat262UiModel, val checksum: Int)
data class Feat262StateBlock8(val state: Feat262UiModel, val checksum: Int)
data class Feat262StateBlock9(val state: Feat262UiModel, val checksum: Int)
data class Feat262StateBlock10(val state: Feat262UiModel, val checksum: Int)

fun buildFeat262UserItem(user: CoreUser, index: Int): Feat262UserItem1 {
    return Feat262UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat262StateBlock(model: Feat262UiModel): Feat262StateBlock1 {
    return Feat262StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat262UserSummary> {
    val list = java.util.ArrayList<Feat262UserSummary>(users.size)
    for (user in users) {
        list += Feat262UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat262UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat262UiModel {
    val summaries = (0 until count).map {
        Feat262UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat262UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat262UiModel> {
    val models = java.util.ArrayList<Feat262UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat262AnalyticsEvent1(val name: String, val value: String)
data class Feat262AnalyticsEvent2(val name: String, val value: String)
data class Feat262AnalyticsEvent3(val name: String, val value: String)
data class Feat262AnalyticsEvent4(val name: String, val value: String)
data class Feat262AnalyticsEvent5(val name: String, val value: String)
data class Feat262AnalyticsEvent6(val name: String, val value: String)
data class Feat262AnalyticsEvent7(val name: String, val value: String)
data class Feat262AnalyticsEvent8(val name: String, val value: String)
data class Feat262AnalyticsEvent9(val name: String, val value: String)
data class Feat262AnalyticsEvent10(val name: String, val value: String)

fun logFeat262Event1(event: Feat262AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat262Event2(event: Feat262AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat262Event3(event: Feat262AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat262Event4(event: Feat262AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat262Event5(event: Feat262AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat262Event6(event: Feat262AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat262Event7(event: Feat262AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat262Event8(event: Feat262AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat262Event9(event: Feat262AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat262Event10(event: Feat262AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat262Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat262Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat262Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat262Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat262Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat262Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat262Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat262Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat262Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat262Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat262(u: CoreUser): Feat262Projection1 =
    Feat262Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat262Projection1> {
    val list = java.util.ArrayList<Feat262Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat262(u)
    }
    return list
}
