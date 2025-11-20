package com.romix.feature.feat320

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat320Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat320UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat320FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat320UserSummary
)

data class Feat320UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat320NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat320Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat320Config = Feat320Config()
) {

    fun loadSnapshot(userId: Long): Feat320NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat320NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat320UserSummary {
        return Feat320UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat320FeedItem> {
        val result = java.util.ArrayList<Feat320FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat320FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat320UiMapper {

    fun mapToUi(model: List<Feat320FeedItem>): Feat320UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat320UiModel(
            header = UiText("Feat320 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat320UiModel =
        Feat320UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat320UiModel =
        Feat320UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat320UiModel =
        Feat320UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat320Service(
    private val repository: Feat320Repository,
    private val uiMapper: Feat320UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat320UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat320UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat320UserItem1(val user: CoreUser, val label: String)
data class Feat320UserItem2(val user: CoreUser, val label: String)
data class Feat320UserItem3(val user: CoreUser, val label: String)
data class Feat320UserItem4(val user: CoreUser, val label: String)
data class Feat320UserItem5(val user: CoreUser, val label: String)
data class Feat320UserItem6(val user: CoreUser, val label: String)
data class Feat320UserItem7(val user: CoreUser, val label: String)
data class Feat320UserItem8(val user: CoreUser, val label: String)
data class Feat320UserItem9(val user: CoreUser, val label: String)
data class Feat320UserItem10(val user: CoreUser, val label: String)

data class Feat320StateBlock1(val state: Feat320UiModel, val checksum: Int)
data class Feat320StateBlock2(val state: Feat320UiModel, val checksum: Int)
data class Feat320StateBlock3(val state: Feat320UiModel, val checksum: Int)
data class Feat320StateBlock4(val state: Feat320UiModel, val checksum: Int)
data class Feat320StateBlock5(val state: Feat320UiModel, val checksum: Int)
data class Feat320StateBlock6(val state: Feat320UiModel, val checksum: Int)
data class Feat320StateBlock7(val state: Feat320UiModel, val checksum: Int)
data class Feat320StateBlock8(val state: Feat320UiModel, val checksum: Int)
data class Feat320StateBlock9(val state: Feat320UiModel, val checksum: Int)
data class Feat320StateBlock10(val state: Feat320UiModel, val checksum: Int)

fun buildFeat320UserItem(user: CoreUser, index: Int): Feat320UserItem1 {
    return Feat320UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat320StateBlock(model: Feat320UiModel): Feat320StateBlock1 {
    return Feat320StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat320UserSummary> {
    val list = java.util.ArrayList<Feat320UserSummary>(users.size)
    for (user in users) {
        list += Feat320UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat320UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat320UiModel {
    val summaries = (0 until count).map {
        Feat320UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat320UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat320UiModel> {
    val models = java.util.ArrayList<Feat320UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat320AnalyticsEvent1(val name: String, val value: String)
data class Feat320AnalyticsEvent2(val name: String, val value: String)
data class Feat320AnalyticsEvent3(val name: String, val value: String)
data class Feat320AnalyticsEvent4(val name: String, val value: String)
data class Feat320AnalyticsEvent5(val name: String, val value: String)
data class Feat320AnalyticsEvent6(val name: String, val value: String)
data class Feat320AnalyticsEvent7(val name: String, val value: String)
data class Feat320AnalyticsEvent8(val name: String, val value: String)
data class Feat320AnalyticsEvent9(val name: String, val value: String)
data class Feat320AnalyticsEvent10(val name: String, val value: String)

fun logFeat320Event1(event: Feat320AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat320Event2(event: Feat320AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat320Event3(event: Feat320AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat320Event4(event: Feat320AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat320Event5(event: Feat320AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat320Event6(event: Feat320AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat320Event7(event: Feat320AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat320Event8(event: Feat320AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat320Event9(event: Feat320AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat320Event10(event: Feat320AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat320Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat320Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat320Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat320Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat320Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat320Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat320Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat320Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat320Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat320Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat320(u: CoreUser): Feat320Projection1 =
    Feat320Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat320Projection1> {
    val list = java.util.ArrayList<Feat320Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat320(u)
    }
    return list
}
