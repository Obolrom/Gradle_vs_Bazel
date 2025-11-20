package com.romix.feature.feat358

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat358Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat358UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat358FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat358UserSummary
)

data class Feat358UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat358NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat358Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat358Config = Feat358Config()
) {

    fun loadSnapshot(userId: Long): Feat358NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat358NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat358UserSummary {
        return Feat358UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat358FeedItem> {
        val result = java.util.ArrayList<Feat358FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat358FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat358UiMapper {

    fun mapToUi(model: List<Feat358FeedItem>): Feat358UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat358UiModel(
            header = UiText("Feat358 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat358UiModel =
        Feat358UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat358UiModel =
        Feat358UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat358UiModel =
        Feat358UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat358Service(
    private val repository: Feat358Repository,
    private val uiMapper: Feat358UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat358UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat358UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat358UserItem1(val user: CoreUser, val label: String)
data class Feat358UserItem2(val user: CoreUser, val label: String)
data class Feat358UserItem3(val user: CoreUser, val label: String)
data class Feat358UserItem4(val user: CoreUser, val label: String)
data class Feat358UserItem5(val user: CoreUser, val label: String)
data class Feat358UserItem6(val user: CoreUser, val label: String)
data class Feat358UserItem7(val user: CoreUser, val label: String)
data class Feat358UserItem8(val user: CoreUser, val label: String)
data class Feat358UserItem9(val user: CoreUser, val label: String)
data class Feat358UserItem10(val user: CoreUser, val label: String)

data class Feat358StateBlock1(val state: Feat358UiModel, val checksum: Int)
data class Feat358StateBlock2(val state: Feat358UiModel, val checksum: Int)
data class Feat358StateBlock3(val state: Feat358UiModel, val checksum: Int)
data class Feat358StateBlock4(val state: Feat358UiModel, val checksum: Int)
data class Feat358StateBlock5(val state: Feat358UiModel, val checksum: Int)
data class Feat358StateBlock6(val state: Feat358UiModel, val checksum: Int)
data class Feat358StateBlock7(val state: Feat358UiModel, val checksum: Int)
data class Feat358StateBlock8(val state: Feat358UiModel, val checksum: Int)
data class Feat358StateBlock9(val state: Feat358UiModel, val checksum: Int)
data class Feat358StateBlock10(val state: Feat358UiModel, val checksum: Int)

fun buildFeat358UserItem(user: CoreUser, index: Int): Feat358UserItem1 {
    return Feat358UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat358StateBlock(model: Feat358UiModel): Feat358StateBlock1 {
    return Feat358StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat358UserSummary> {
    val list = java.util.ArrayList<Feat358UserSummary>(users.size)
    for (user in users) {
        list += Feat358UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat358UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat358UiModel {
    val summaries = (0 until count).map {
        Feat358UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat358UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat358UiModel> {
    val models = java.util.ArrayList<Feat358UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat358AnalyticsEvent1(val name: String, val value: String)
data class Feat358AnalyticsEvent2(val name: String, val value: String)
data class Feat358AnalyticsEvent3(val name: String, val value: String)
data class Feat358AnalyticsEvent4(val name: String, val value: String)
data class Feat358AnalyticsEvent5(val name: String, val value: String)
data class Feat358AnalyticsEvent6(val name: String, val value: String)
data class Feat358AnalyticsEvent7(val name: String, val value: String)
data class Feat358AnalyticsEvent8(val name: String, val value: String)
data class Feat358AnalyticsEvent9(val name: String, val value: String)
data class Feat358AnalyticsEvent10(val name: String, val value: String)

fun logFeat358Event1(event: Feat358AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat358Event2(event: Feat358AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat358Event3(event: Feat358AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat358Event4(event: Feat358AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat358Event5(event: Feat358AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat358Event6(event: Feat358AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat358Event7(event: Feat358AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat358Event8(event: Feat358AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat358Event9(event: Feat358AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat358Event10(event: Feat358AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat358Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat358Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat358Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat358Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat358Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat358Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat358Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat358Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat358Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat358Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat358(u: CoreUser): Feat358Projection1 =
    Feat358Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat358Projection1> {
    val list = java.util.ArrayList<Feat358Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat358(u)
    }
    return list
}
