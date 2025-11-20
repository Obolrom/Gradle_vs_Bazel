package com.romix.feature.feat283

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat283Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat283UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat283FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat283UserSummary
)

data class Feat283UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat283NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat283Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat283Config = Feat283Config()
) {

    fun loadSnapshot(userId: Long): Feat283NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat283NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat283UserSummary {
        return Feat283UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat283FeedItem> {
        val result = java.util.ArrayList<Feat283FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat283FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat283UiMapper {

    fun mapToUi(model: List<Feat283FeedItem>): Feat283UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat283UiModel(
            header = UiText("Feat283 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat283UiModel =
        Feat283UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat283UiModel =
        Feat283UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat283UiModel =
        Feat283UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat283Service(
    private val repository: Feat283Repository,
    private val uiMapper: Feat283UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat283UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat283UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat283UserItem1(val user: CoreUser, val label: String)
data class Feat283UserItem2(val user: CoreUser, val label: String)
data class Feat283UserItem3(val user: CoreUser, val label: String)
data class Feat283UserItem4(val user: CoreUser, val label: String)
data class Feat283UserItem5(val user: CoreUser, val label: String)
data class Feat283UserItem6(val user: CoreUser, val label: String)
data class Feat283UserItem7(val user: CoreUser, val label: String)
data class Feat283UserItem8(val user: CoreUser, val label: String)
data class Feat283UserItem9(val user: CoreUser, val label: String)
data class Feat283UserItem10(val user: CoreUser, val label: String)

data class Feat283StateBlock1(val state: Feat283UiModel, val checksum: Int)
data class Feat283StateBlock2(val state: Feat283UiModel, val checksum: Int)
data class Feat283StateBlock3(val state: Feat283UiModel, val checksum: Int)
data class Feat283StateBlock4(val state: Feat283UiModel, val checksum: Int)
data class Feat283StateBlock5(val state: Feat283UiModel, val checksum: Int)
data class Feat283StateBlock6(val state: Feat283UiModel, val checksum: Int)
data class Feat283StateBlock7(val state: Feat283UiModel, val checksum: Int)
data class Feat283StateBlock8(val state: Feat283UiModel, val checksum: Int)
data class Feat283StateBlock9(val state: Feat283UiModel, val checksum: Int)
data class Feat283StateBlock10(val state: Feat283UiModel, val checksum: Int)

fun buildFeat283UserItem(user: CoreUser, index: Int): Feat283UserItem1 {
    return Feat283UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat283StateBlock(model: Feat283UiModel): Feat283StateBlock1 {
    return Feat283StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat283UserSummary> {
    val list = java.util.ArrayList<Feat283UserSummary>(users.size)
    for (user in users) {
        list += Feat283UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat283UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat283UiModel {
    val summaries = (0 until count).map {
        Feat283UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat283UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat283UiModel> {
    val models = java.util.ArrayList<Feat283UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat283AnalyticsEvent1(val name: String, val value: String)
data class Feat283AnalyticsEvent2(val name: String, val value: String)
data class Feat283AnalyticsEvent3(val name: String, val value: String)
data class Feat283AnalyticsEvent4(val name: String, val value: String)
data class Feat283AnalyticsEvent5(val name: String, val value: String)
data class Feat283AnalyticsEvent6(val name: String, val value: String)
data class Feat283AnalyticsEvent7(val name: String, val value: String)
data class Feat283AnalyticsEvent8(val name: String, val value: String)
data class Feat283AnalyticsEvent9(val name: String, val value: String)
data class Feat283AnalyticsEvent10(val name: String, val value: String)

fun logFeat283Event1(event: Feat283AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat283Event2(event: Feat283AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat283Event3(event: Feat283AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat283Event4(event: Feat283AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat283Event5(event: Feat283AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat283Event6(event: Feat283AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat283Event7(event: Feat283AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat283Event8(event: Feat283AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat283Event9(event: Feat283AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat283Event10(event: Feat283AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat283Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat283Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat283Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat283Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat283Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat283Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat283Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat283Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat283Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat283Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat283(u: CoreUser): Feat283Projection1 =
    Feat283Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat283Projection1> {
    val list = java.util.ArrayList<Feat283Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat283(u)
    }
    return list
}
