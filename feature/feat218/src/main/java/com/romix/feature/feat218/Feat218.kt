package com.romix.feature.feat218

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat218Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat218UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat218FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat218UserSummary
)

data class Feat218UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat218NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat218Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat218Config = Feat218Config()
) {

    fun loadSnapshot(userId: Long): Feat218NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat218NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat218UserSummary {
        return Feat218UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat218FeedItem> {
        val result = java.util.ArrayList<Feat218FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat218FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat218UiMapper {

    fun mapToUi(model: List<Feat218FeedItem>): Feat218UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat218UiModel(
            header = UiText("Feat218 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat218UiModel =
        Feat218UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat218UiModel =
        Feat218UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat218UiModel =
        Feat218UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat218Service(
    private val repository: Feat218Repository,
    private val uiMapper: Feat218UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat218UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat218UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat218UserItem1(val user: CoreUser, val label: String)
data class Feat218UserItem2(val user: CoreUser, val label: String)
data class Feat218UserItem3(val user: CoreUser, val label: String)
data class Feat218UserItem4(val user: CoreUser, val label: String)
data class Feat218UserItem5(val user: CoreUser, val label: String)
data class Feat218UserItem6(val user: CoreUser, val label: String)
data class Feat218UserItem7(val user: CoreUser, val label: String)
data class Feat218UserItem8(val user: CoreUser, val label: String)
data class Feat218UserItem9(val user: CoreUser, val label: String)
data class Feat218UserItem10(val user: CoreUser, val label: String)

data class Feat218StateBlock1(val state: Feat218UiModel, val checksum: Int)
data class Feat218StateBlock2(val state: Feat218UiModel, val checksum: Int)
data class Feat218StateBlock3(val state: Feat218UiModel, val checksum: Int)
data class Feat218StateBlock4(val state: Feat218UiModel, val checksum: Int)
data class Feat218StateBlock5(val state: Feat218UiModel, val checksum: Int)
data class Feat218StateBlock6(val state: Feat218UiModel, val checksum: Int)
data class Feat218StateBlock7(val state: Feat218UiModel, val checksum: Int)
data class Feat218StateBlock8(val state: Feat218UiModel, val checksum: Int)
data class Feat218StateBlock9(val state: Feat218UiModel, val checksum: Int)
data class Feat218StateBlock10(val state: Feat218UiModel, val checksum: Int)

fun buildFeat218UserItem(user: CoreUser, index: Int): Feat218UserItem1 {
    return Feat218UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat218StateBlock(model: Feat218UiModel): Feat218StateBlock1 {
    return Feat218StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat218UserSummary> {
    val list = java.util.ArrayList<Feat218UserSummary>(users.size)
    for (user in users) {
        list += Feat218UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat218UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat218UiModel {
    val summaries = (0 until count).map {
        Feat218UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat218UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat218UiModel> {
    val models = java.util.ArrayList<Feat218UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat218AnalyticsEvent1(val name: String, val value: String)
data class Feat218AnalyticsEvent2(val name: String, val value: String)
data class Feat218AnalyticsEvent3(val name: String, val value: String)
data class Feat218AnalyticsEvent4(val name: String, val value: String)
data class Feat218AnalyticsEvent5(val name: String, val value: String)
data class Feat218AnalyticsEvent6(val name: String, val value: String)
data class Feat218AnalyticsEvent7(val name: String, val value: String)
data class Feat218AnalyticsEvent8(val name: String, val value: String)
data class Feat218AnalyticsEvent9(val name: String, val value: String)
data class Feat218AnalyticsEvent10(val name: String, val value: String)

fun logFeat218Event1(event: Feat218AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat218Event2(event: Feat218AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat218Event3(event: Feat218AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat218Event4(event: Feat218AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat218Event5(event: Feat218AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat218Event6(event: Feat218AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat218Event7(event: Feat218AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat218Event8(event: Feat218AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat218Event9(event: Feat218AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat218Event10(event: Feat218AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat218Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat218Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat218Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat218Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat218Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat218Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat218Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat218Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat218Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat218Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat218(u: CoreUser): Feat218Projection1 =
    Feat218Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat218Projection1> {
    val list = java.util.ArrayList<Feat218Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat218(u)
    }
    return list
}
