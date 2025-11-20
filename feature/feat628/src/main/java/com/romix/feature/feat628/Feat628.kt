package com.romix.feature.feat628

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat628Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat628UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat628FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat628UserSummary
)

data class Feat628UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat628NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat628Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat628Config = Feat628Config()
) {

    fun loadSnapshot(userId: Long): Feat628NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat628NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat628UserSummary {
        return Feat628UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat628FeedItem> {
        val result = java.util.ArrayList<Feat628FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat628FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat628UiMapper {

    fun mapToUi(model: List<Feat628FeedItem>): Feat628UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat628UiModel(
            header = UiText("Feat628 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat628UiModel =
        Feat628UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat628UiModel =
        Feat628UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat628UiModel =
        Feat628UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat628Service(
    private val repository: Feat628Repository,
    private val uiMapper: Feat628UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat628UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat628UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat628UserItem1(val user: CoreUser, val label: String)
data class Feat628UserItem2(val user: CoreUser, val label: String)
data class Feat628UserItem3(val user: CoreUser, val label: String)
data class Feat628UserItem4(val user: CoreUser, val label: String)
data class Feat628UserItem5(val user: CoreUser, val label: String)
data class Feat628UserItem6(val user: CoreUser, val label: String)
data class Feat628UserItem7(val user: CoreUser, val label: String)
data class Feat628UserItem8(val user: CoreUser, val label: String)
data class Feat628UserItem9(val user: CoreUser, val label: String)
data class Feat628UserItem10(val user: CoreUser, val label: String)

data class Feat628StateBlock1(val state: Feat628UiModel, val checksum: Int)
data class Feat628StateBlock2(val state: Feat628UiModel, val checksum: Int)
data class Feat628StateBlock3(val state: Feat628UiModel, val checksum: Int)
data class Feat628StateBlock4(val state: Feat628UiModel, val checksum: Int)
data class Feat628StateBlock5(val state: Feat628UiModel, val checksum: Int)
data class Feat628StateBlock6(val state: Feat628UiModel, val checksum: Int)
data class Feat628StateBlock7(val state: Feat628UiModel, val checksum: Int)
data class Feat628StateBlock8(val state: Feat628UiModel, val checksum: Int)
data class Feat628StateBlock9(val state: Feat628UiModel, val checksum: Int)
data class Feat628StateBlock10(val state: Feat628UiModel, val checksum: Int)

fun buildFeat628UserItem(user: CoreUser, index: Int): Feat628UserItem1 {
    return Feat628UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat628StateBlock(model: Feat628UiModel): Feat628StateBlock1 {
    return Feat628StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat628UserSummary> {
    val list = java.util.ArrayList<Feat628UserSummary>(users.size)
    for (user in users) {
        list += Feat628UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat628UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat628UiModel {
    val summaries = (0 until count).map {
        Feat628UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat628UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat628UiModel> {
    val models = java.util.ArrayList<Feat628UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat628AnalyticsEvent1(val name: String, val value: String)
data class Feat628AnalyticsEvent2(val name: String, val value: String)
data class Feat628AnalyticsEvent3(val name: String, val value: String)
data class Feat628AnalyticsEvent4(val name: String, val value: String)
data class Feat628AnalyticsEvent5(val name: String, val value: String)
data class Feat628AnalyticsEvent6(val name: String, val value: String)
data class Feat628AnalyticsEvent7(val name: String, val value: String)
data class Feat628AnalyticsEvent8(val name: String, val value: String)
data class Feat628AnalyticsEvent9(val name: String, val value: String)
data class Feat628AnalyticsEvent10(val name: String, val value: String)

fun logFeat628Event1(event: Feat628AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat628Event2(event: Feat628AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat628Event3(event: Feat628AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat628Event4(event: Feat628AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat628Event5(event: Feat628AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat628Event6(event: Feat628AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat628Event7(event: Feat628AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat628Event8(event: Feat628AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat628Event9(event: Feat628AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat628Event10(event: Feat628AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat628Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat628Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat628Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat628Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat628Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat628Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat628Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat628Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat628Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat628Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat628(u: CoreUser): Feat628Projection1 =
    Feat628Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat628Projection1> {
    val list = java.util.ArrayList<Feat628Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat628(u)
    }
    return list
}
