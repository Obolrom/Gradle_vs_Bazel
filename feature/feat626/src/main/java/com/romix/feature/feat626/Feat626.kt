package com.romix.feature.feat626

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat626Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat626UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat626FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat626UserSummary
)

data class Feat626UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat626NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat626Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat626Config = Feat626Config()
) {

    fun loadSnapshot(userId: Long): Feat626NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat626NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat626UserSummary {
        return Feat626UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat626FeedItem> {
        val result = java.util.ArrayList<Feat626FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat626FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat626UiMapper {

    fun mapToUi(model: List<Feat626FeedItem>): Feat626UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat626UiModel(
            header = UiText("Feat626 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat626UiModel =
        Feat626UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat626UiModel =
        Feat626UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat626UiModel =
        Feat626UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat626Service(
    private val repository: Feat626Repository,
    private val uiMapper: Feat626UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat626UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat626UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat626UserItem1(val user: CoreUser, val label: String)
data class Feat626UserItem2(val user: CoreUser, val label: String)
data class Feat626UserItem3(val user: CoreUser, val label: String)
data class Feat626UserItem4(val user: CoreUser, val label: String)
data class Feat626UserItem5(val user: CoreUser, val label: String)
data class Feat626UserItem6(val user: CoreUser, val label: String)
data class Feat626UserItem7(val user: CoreUser, val label: String)
data class Feat626UserItem8(val user: CoreUser, val label: String)
data class Feat626UserItem9(val user: CoreUser, val label: String)
data class Feat626UserItem10(val user: CoreUser, val label: String)

data class Feat626StateBlock1(val state: Feat626UiModel, val checksum: Int)
data class Feat626StateBlock2(val state: Feat626UiModel, val checksum: Int)
data class Feat626StateBlock3(val state: Feat626UiModel, val checksum: Int)
data class Feat626StateBlock4(val state: Feat626UiModel, val checksum: Int)
data class Feat626StateBlock5(val state: Feat626UiModel, val checksum: Int)
data class Feat626StateBlock6(val state: Feat626UiModel, val checksum: Int)
data class Feat626StateBlock7(val state: Feat626UiModel, val checksum: Int)
data class Feat626StateBlock8(val state: Feat626UiModel, val checksum: Int)
data class Feat626StateBlock9(val state: Feat626UiModel, val checksum: Int)
data class Feat626StateBlock10(val state: Feat626UiModel, val checksum: Int)

fun buildFeat626UserItem(user: CoreUser, index: Int): Feat626UserItem1 {
    return Feat626UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat626StateBlock(model: Feat626UiModel): Feat626StateBlock1 {
    return Feat626StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat626UserSummary> {
    val list = java.util.ArrayList<Feat626UserSummary>(users.size)
    for (user in users) {
        list += Feat626UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat626UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat626UiModel {
    val summaries = (0 until count).map {
        Feat626UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat626UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat626UiModel> {
    val models = java.util.ArrayList<Feat626UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat626AnalyticsEvent1(val name: String, val value: String)
data class Feat626AnalyticsEvent2(val name: String, val value: String)
data class Feat626AnalyticsEvent3(val name: String, val value: String)
data class Feat626AnalyticsEvent4(val name: String, val value: String)
data class Feat626AnalyticsEvent5(val name: String, val value: String)
data class Feat626AnalyticsEvent6(val name: String, val value: String)
data class Feat626AnalyticsEvent7(val name: String, val value: String)
data class Feat626AnalyticsEvent8(val name: String, val value: String)
data class Feat626AnalyticsEvent9(val name: String, val value: String)
data class Feat626AnalyticsEvent10(val name: String, val value: String)

fun logFeat626Event1(event: Feat626AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat626Event2(event: Feat626AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat626Event3(event: Feat626AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat626Event4(event: Feat626AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat626Event5(event: Feat626AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat626Event6(event: Feat626AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat626Event7(event: Feat626AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat626Event8(event: Feat626AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat626Event9(event: Feat626AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat626Event10(event: Feat626AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat626Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat626Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat626Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat626Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat626Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat626Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat626Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat626Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat626Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat626Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat626(u: CoreUser): Feat626Projection1 =
    Feat626Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat626Projection1> {
    val list = java.util.ArrayList<Feat626Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat626(u)
    }
    return list
}
