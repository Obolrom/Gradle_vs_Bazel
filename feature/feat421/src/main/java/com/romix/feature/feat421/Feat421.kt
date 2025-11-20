package com.romix.feature.feat421

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat421Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat421UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat421FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat421UserSummary
)

data class Feat421UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat421NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat421Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat421Config = Feat421Config()
) {

    fun loadSnapshot(userId: Long): Feat421NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat421NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat421UserSummary {
        return Feat421UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat421FeedItem> {
        val result = java.util.ArrayList<Feat421FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat421FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat421UiMapper {

    fun mapToUi(model: List<Feat421FeedItem>): Feat421UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat421UiModel(
            header = UiText("Feat421 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat421UiModel =
        Feat421UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat421UiModel =
        Feat421UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat421UiModel =
        Feat421UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat421Service(
    private val repository: Feat421Repository,
    private val uiMapper: Feat421UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat421UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat421UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat421UserItem1(val user: CoreUser, val label: String)
data class Feat421UserItem2(val user: CoreUser, val label: String)
data class Feat421UserItem3(val user: CoreUser, val label: String)
data class Feat421UserItem4(val user: CoreUser, val label: String)
data class Feat421UserItem5(val user: CoreUser, val label: String)
data class Feat421UserItem6(val user: CoreUser, val label: String)
data class Feat421UserItem7(val user: CoreUser, val label: String)
data class Feat421UserItem8(val user: CoreUser, val label: String)
data class Feat421UserItem9(val user: CoreUser, val label: String)
data class Feat421UserItem10(val user: CoreUser, val label: String)

data class Feat421StateBlock1(val state: Feat421UiModel, val checksum: Int)
data class Feat421StateBlock2(val state: Feat421UiModel, val checksum: Int)
data class Feat421StateBlock3(val state: Feat421UiModel, val checksum: Int)
data class Feat421StateBlock4(val state: Feat421UiModel, val checksum: Int)
data class Feat421StateBlock5(val state: Feat421UiModel, val checksum: Int)
data class Feat421StateBlock6(val state: Feat421UiModel, val checksum: Int)
data class Feat421StateBlock7(val state: Feat421UiModel, val checksum: Int)
data class Feat421StateBlock8(val state: Feat421UiModel, val checksum: Int)
data class Feat421StateBlock9(val state: Feat421UiModel, val checksum: Int)
data class Feat421StateBlock10(val state: Feat421UiModel, val checksum: Int)

fun buildFeat421UserItem(user: CoreUser, index: Int): Feat421UserItem1 {
    return Feat421UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat421StateBlock(model: Feat421UiModel): Feat421StateBlock1 {
    return Feat421StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat421UserSummary> {
    val list = java.util.ArrayList<Feat421UserSummary>(users.size)
    for (user in users) {
        list += Feat421UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat421UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat421UiModel {
    val summaries = (0 until count).map {
        Feat421UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat421UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat421UiModel> {
    val models = java.util.ArrayList<Feat421UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat421AnalyticsEvent1(val name: String, val value: String)
data class Feat421AnalyticsEvent2(val name: String, val value: String)
data class Feat421AnalyticsEvent3(val name: String, val value: String)
data class Feat421AnalyticsEvent4(val name: String, val value: String)
data class Feat421AnalyticsEvent5(val name: String, val value: String)
data class Feat421AnalyticsEvent6(val name: String, val value: String)
data class Feat421AnalyticsEvent7(val name: String, val value: String)
data class Feat421AnalyticsEvent8(val name: String, val value: String)
data class Feat421AnalyticsEvent9(val name: String, val value: String)
data class Feat421AnalyticsEvent10(val name: String, val value: String)

fun logFeat421Event1(event: Feat421AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat421Event2(event: Feat421AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat421Event3(event: Feat421AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat421Event4(event: Feat421AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat421Event5(event: Feat421AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat421Event6(event: Feat421AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat421Event7(event: Feat421AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat421Event8(event: Feat421AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat421Event9(event: Feat421AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat421Event10(event: Feat421AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat421Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat421Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat421Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat421Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat421Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat421Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat421Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat421Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat421Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat421Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat421(u: CoreUser): Feat421Projection1 =
    Feat421Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat421Projection1> {
    val list = java.util.ArrayList<Feat421Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat421(u)
    }
    return list
}
