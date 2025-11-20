package com.romix.feature.feat522

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat522Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat522UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat522FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat522UserSummary
)

data class Feat522UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat522NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat522Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat522Config = Feat522Config()
) {

    fun loadSnapshot(userId: Long): Feat522NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat522NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat522UserSummary {
        return Feat522UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat522FeedItem> {
        val result = java.util.ArrayList<Feat522FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat522FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat522UiMapper {

    fun mapToUi(model: List<Feat522FeedItem>): Feat522UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat522UiModel(
            header = UiText("Feat522 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat522UiModel =
        Feat522UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat522UiModel =
        Feat522UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat522UiModel =
        Feat522UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat522Service(
    private val repository: Feat522Repository,
    private val uiMapper: Feat522UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat522UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat522UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat522UserItem1(val user: CoreUser, val label: String)
data class Feat522UserItem2(val user: CoreUser, val label: String)
data class Feat522UserItem3(val user: CoreUser, val label: String)
data class Feat522UserItem4(val user: CoreUser, val label: String)
data class Feat522UserItem5(val user: CoreUser, val label: String)
data class Feat522UserItem6(val user: CoreUser, val label: String)
data class Feat522UserItem7(val user: CoreUser, val label: String)
data class Feat522UserItem8(val user: CoreUser, val label: String)
data class Feat522UserItem9(val user: CoreUser, val label: String)
data class Feat522UserItem10(val user: CoreUser, val label: String)

data class Feat522StateBlock1(val state: Feat522UiModel, val checksum: Int)
data class Feat522StateBlock2(val state: Feat522UiModel, val checksum: Int)
data class Feat522StateBlock3(val state: Feat522UiModel, val checksum: Int)
data class Feat522StateBlock4(val state: Feat522UiModel, val checksum: Int)
data class Feat522StateBlock5(val state: Feat522UiModel, val checksum: Int)
data class Feat522StateBlock6(val state: Feat522UiModel, val checksum: Int)
data class Feat522StateBlock7(val state: Feat522UiModel, val checksum: Int)
data class Feat522StateBlock8(val state: Feat522UiModel, val checksum: Int)
data class Feat522StateBlock9(val state: Feat522UiModel, val checksum: Int)
data class Feat522StateBlock10(val state: Feat522UiModel, val checksum: Int)

fun buildFeat522UserItem(user: CoreUser, index: Int): Feat522UserItem1 {
    return Feat522UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat522StateBlock(model: Feat522UiModel): Feat522StateBlock1 {
    return Feat522StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat522UserSummary> {
    val list = java.util.ArrayList<Feat522UserSummary>(users.size)
    for (user in users) {
        list += Feat522UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat522UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat522UiModel {
    val summaries = (0 until count).map {
        Feat522UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat522UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat522UiModel> {
    val models = java.util.ArrayList<Feat522UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat522AnalyticsEvent1(val name: String, val value: String)
data class Feat522AnalyticsEvent2(val name: String, val value: String)
data class Feat522AnalyticsEvent3(val name: String, val value: String)
data class Feat522AnalyticsEvent4(val name: String, val value: String)
data class Feat522AnalyticsEvent5(val name: String, val value: String)
data class Feat522AnalyticsEvent6(val name: String, val value: String)
data class Feat522AnalyticsEvent7(val name: String, val value: String)
data class Feat522AnalyticsEvent8(val name: String, val value: String)
data class Feat522AnalyticsEvent9(val name: String, val value: String)
data class Feat522AnalyticsEvent10(val name: String, val value: String)

fun logFeat522Event1(event: Feat522AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat522Event2(event: Feat522AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat522Event3(event: Feat522AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat522Event4(event: Feat522AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat522Event5(event: Feat522AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat522Event6(event: Feat522AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat522Event7(event: Feat522AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat522Event8(event: Feat522AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat522Event9(event: Feat522AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat522Event10(event: Feat522AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat522Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat522Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat522Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat522Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat522Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat522Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat522Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat522Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat522Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat522Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat522(u: CoreUser): Feat522Projection1 =
    Feat522Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat522Projection1> {
    val list = java.util.ArrayList<Feat522Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat522(u)
    }
    return list
}
