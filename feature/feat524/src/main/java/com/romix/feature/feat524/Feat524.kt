package com.romix.feature.feat524

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat524Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat524UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat524FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat524UserSummary
)

data class Feat524UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat524NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat524Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat524Config = Feat524Config()
) {

    fun loadSnapshot(userId: Long): Feat524NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat524NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat524UserSummary {
        return Feat524UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat524FeedItem> {
        val result = java.util.ArrayList<Feat524FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat524FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat524UiMapper {

    fun mapToUi(model: List<Feat524FeedItem>): Feat524UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat524UiModel(
            header = UiText("Feat524 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat524UiModel =
        Feat524UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat524UiModel =
        Feat524UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat524UiModel =
        Feat524UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat524Service(
    private val repository: Feat524Repository,
    private val uiMapper: Feat524UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat524UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat524UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat524UserItem1(val user: CoreUser, val label: String)
data class Feat524UserItem2(val user: CoreUser, val label: String)
data class Feat524UserItem3(val user: CoreUser, val label: String)
data class Feat524UserItem4(val user: CoreUser, val label: String)
data class Feat524UserItem5(val user: CoreUser, val label: String)
data class Feat524UserItem6(val user: CoreUser, val label: String)
data class Feat524UserItem7(val user: CoreUser, val label: String)
data class Feat524UserItem8(val user: CoreUser, val label: String)
data class Feat524UserItem9(val user: CoreUser, val label: String)
data class Feat524UserItem10(val user: CoreUser, val label: String)

data class Feat524StateBlock1(val state: Feat524UiModel, val checksum: Int)
data class Feat524StateBlock2(val state: Feat524UiModel, val checksum: Int)
data class Feat524StateBlock3(val state: Feat524UiModel, val checksum: Int)
data class Feat524StateBlock4(val state: Feat524UiModel, val checksum: Int)
data class Feat524StateBlock5(val state: Feat524UiModel, val checksum: Int)
data class Feat524StateBlock6(val state: Feat524UiModel, val checksum: Int)
data class Feat524StateBlock7(val state: Feat524UiModel, val checksum: Int)
data class Feat524StateBlock8(val state: Feat524UiModel, val checksum: Int)
data class Feat524StateBlock9(val state: Feat524UiModel, val checksum: Int)
data class Feat524StateBlock10(val state: Feat524UiModel, val checksum: Int)

fun buildFeat524UserItem(user: CoreUser, index: Int): Feat524UserItem1 {
    return Feat524UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat524StateBlock(model: Feat524UiModel): Feat524StateBlock1 {
    return Feat524StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat524UserSummary> {
    val list = java.util.ArrayList<Feat524UserSummary>(users.size)
    for (user in users) {
        list += Feat524UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat524UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat524UiModel {
    val summaries = (0 until count).map {
        Feat524UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat524UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat524UiModel> {
    val models = java.util.ArrayList<Feat524UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat524AnalyticsEvent1(val name: String, val value: String)
data class Feat524AnalyticsEvent2(val name: String, val value: String)
data class Feat524AnalyticsEvent3(val name: String, val value: String)
data class Feat524AnalyticsEvent4(val name: String, val value: String)
data class Feat524AnalyticsEvent5(val name: String, val value: String)
data class Feat524AnalyticsEvent6(val name: String, val value: String)
data class Feat524AnalyticsEvent7(val name: String, val value: String)
data class Feat524AnalyticsEvent8(val name: String, val value: String)
data class Feat524AnalyticsEvent9(val name: String, val value: String)
data class Feat524AnalyticsEvent10(val name: String, val value: String)

fun logFeat524Event1(event: Feat524AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat524Event2(event: Feat524AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat524Event3(event: Feat524AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat524Event4(event: Feat524AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat524Event5(event: Feat524AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat524Event6(event: Feat524AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat524Event7(event: Feat524AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat524Event8(event: Feat524AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat524Event9(event: Feat524AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat524Event10(event: Feat524AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat524Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat524Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat524Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat524Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat524Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat524Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat524Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat524Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat524Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat524Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat524(u: CoreUser): Feat524Projection1 =
    Feat524Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat524Projection1> {
    val list = java.util.ArrayList<Feat524Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat524(u)
    }
    return list
}
