package com.romix.feature.feat652

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat652Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat652UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat652FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat652UserSummary
)

data class Feat652UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat652NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat652Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat652Config = Feat652Config()
) {

    fun loadSnapshot(userId: Long): Feat652NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat652NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat652UserSummary {
        return Feat652UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat652FeedItem> {
        val result = java.util.ArrayList<Feat652FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat652FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat652UiMapper {

    fun mapToUi(model: List<Feat652FeedItem>): Feat652UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat652UiModel(
            header = UiText("Feat652 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat652UiModel =
        Feat652UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat652UiModel =
        Feat652UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat652UiModel =
        Feat652UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat652Service(
    private val repository: Feat652Repository,
    private val uiMapper: Feat652UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat652UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat652UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat652UserItem1(val user: CoreUser, val label: String)
data class Feat652UserItem2(val user: CoreUser, val label: String)
data class Feat652UserItem3(val user: CoreUser, val label: String)
data class Feat652UserItem4(val user: CoreUser, val label: String)
data class Feat652UserItem5(val user: CoreUser, val label: String)
data class Feat652UserItem6(val user: CoreUser, val label: String)
data class Feat652UserItem7(val user: CoreUser, val label: String)
data class Feat652UserItem8(val user: CoreUser, val label: String)
data class Feat652UserItem9(val user: CoreUser, val label: String)
data class Feat652UserItem10(val user: CoreUser, val label: String)

data class Feat652StateBlock1(val state: Feat652UiModel, val checksum: Int)
data class Feat652StateBlock2(val state: Feat652UiModel, val checksum: Int)
data class Feat652StateBlock3(val state: Feat652UiModel, val checksum: Int)
data class Feat652StateBlock4(val state: Feat652UiModel, val checksum: Int)
data class Feat652StateBlock5(val state: Feat652UiModel, val checksum: Int)
data class Feat652StateBlock6(val state: Feat652UiModel, val checksum: Int)
data class Feat652StateBlock7(val state: Feat652UiModel, val checksum: Int)
data class Feat652StateBlock8(val state: Feat652UiModel, val checksum: Int)
data class Feat652StateBlock9(val state: Feat652UiModel, val checksum: Int)
data class Feat652StateBlock10(val state: Feat652UiModel, val checksum: Int)

fun buildFeat652UserItem(user: CoreUser, index: Int): Feat652UserItem1 {
    return Feat652UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat652StateBlock(model: Feat652UiModel): Feat652StateBlock1 {
    return Feat652StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat652UserSummary> {
    val list = java.util.ArrayList<Feat652UserSummary>(users.size)
    for (user in users) {
        list += Feat652UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat652UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat652UiModel {
    val summaries = (0 until count).map {
        Feat652UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat652UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat652UiModel> {
    val models = java.util.ArrayList<Feat652UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat652AnalyticsEvent1(val name: String, val value: String)
data class Feat652AnalyticsEvent2(val name: String, val value: String)
data class Feat652AnalyticsEvent3(val name: String, val value: String)
data class Feat652AnalyticsEvent4(val name: String, val value: String)
data class Feat652AnalyticsEvent5(val name: String, val value: String)
data class Feat652AnalyticsEvent6(val name: String, val value: String)
data class Feat652AnalyticsEvent7(val name: String, val value: String)
data class Feat652AnalyticsEvent8(val name: String, val value: String)
data class Feat652AnalyticsEvent9(val name: String, val value: String)
data class Feat652AnalyticsEvent10(val name: String, val value: String)

fun logFeat652Event1(event: Feat652AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat652Event2(event: Feat652AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat652Event3(event: Feat652AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat652Event4(event: Feat652AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat652Event5(event: Feat652AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat652Event6(event: Feat652AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat652Event7(event: Feat652AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat652Event8(event: Feat652AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat652Event9(event: Feat652AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat652Event10(event: Feat652AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat652Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat652Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat652Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat652Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat652Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat652Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat652Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat652Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat652Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat652Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat652(u: CoreUser): Feat652Projection1 =
    Feat652Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat652Projection1> {
    val list = java.util.ArrayList<Feat652Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat652(u)
    }
    return list
}
