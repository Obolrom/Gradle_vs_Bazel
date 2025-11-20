package com.romix.feature.feat560

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat560Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat560UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat560FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat560UserSummary
)

data class Feat560UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat560NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat560Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat560Config = Feat560Config()
) {

    fun loadSnapshot(userId: Long): Feat560NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat560NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat560UserSummary {
        return Feat560UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat560FeedItem> {
        val result = java.util.ArrayList<Feat560FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat560FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat560UiMapper {

    fun mapToUi(model: List<Feat560FeedItem>): Feat560UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat560UiModel(
            header = UiText("Feat560 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat560UiModel =
        Feat560UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat560UiModel =
        Feat560UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat560UiModel =
        Feat560UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat560Service(
    private val repository: Feat560Repository,
    private val uiMapper: Feat560UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat560UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat560UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat560UserItem1(val user: CoreUser, val label: String)
data class Feat560UserItem2(val user: CoreUser, val label: String)
data class Feat560UserItem3(val user: CoreUser, val label: String)
data class Feat560UserItem4(val user: CoreUser, val label: String)
data class Feat560UserItem5(val user: CoreUser, val label: String)
data class Feat560UserItem6(val user: CoreUser, val label: String)
data class Feat560UserItem7(val user: CoreUser, val label: String)
data class Feat560UserItem8(val user: CoreUser, val label: String)
data class Feat560UserItem9(val user: CoreUser, val label: String)
data class Feat560UserItem10(val user: CoreUser, val label: String)

data class Feat560StateBlock1(val state: Feat560UiModel, val checksum: Int)
data class Feat560StateBlock2(val state: Feat560UiModel, val checksum: Int)
data class Feat560StateBlock3(val state: Feat560UiModel, val checksum: Int)
data class Feat560StateBlock4(val state: Feat560UiModel, val checksum: Int)
data class Feat560StateBlock5(val state: Feat560UiModel, val checksum: Int)
data class Feat560StateBlock6(val state: Feat560UiModel, val checksum: Int)
data class Feat560StateBlock7(val state: Feat560UiModel, val checksum: Int)
data class Feat560StateBlock8(val state: Feat560UiModel, val checksum: Int)
data class Feat560StateBlock9(val state: Feat560UiModel, val checksum: Int)
data class Feat560StateBlock10(val state: Feat560UiModel, val checksum: Int)

fun buildFeat560UserItem(user: CoreUser, index: Int): Feat560UserItem1 {
    return Feat560UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat560StateBlock(model: Feat560UiModel): Feat560StateBlock1 {
    return Feat560StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat560UserSummary> {
    val list = java.util.ArrayList<Feat560UserSummary>(users.size)
    for (user in users) {
        list += Feat560UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat560UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat560UiModel {
    val summaries = (0 until count).map {
        Feat560UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat560UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat560UiModel> {
    val models = java.util.ArrayList<Feat560UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat560AnalyticsEvent1(val name: String, val value: String)
data class Feat560AnalyticsEvent2(val name: String, val value: String)
data class Feat560AnalyticsEvent3(val name: String, val value: String)
data class Feat560AnalyticsEvent4(val name: String, val value: String)
data class Feat560AnalyticsEvent5(val name: String, val value: String)
data class Feat560AnalyticsEvent6(val name: String, val value: String)
data class Feat560AnalyticsEvent7(val name: String, val value: String)
data class Feat560AnalyticsEvent8(val name: String, val value: String)
data class Feat560AnalyticsEvent9(val name: String, val value: String)
data class Feat560AnalyticsEvent10(val name: String, val value: String)

fun logFeat560Event1(event: Feat560AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat560Event2(event: Feat560AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat560Event3(event: Feat560AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat560Event4(event: Feat560AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat560Event5(event: Feat560AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat560Event6(event: Feat560AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat560Event7(event: Feat560AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat560Event8(event: Feat560AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat560Event9(event: Feat560AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat560Event10(event: Feat560AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat560Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat560Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat560Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat560Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat560Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat560Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat560Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat560Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat560Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat560Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat560(u: CoreUser): Feat560Projection1 =
    Feat560Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat560Projection1> {
    val list = java.util.ArrayList<Feat560Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat560(u)
    }
    return list
}
