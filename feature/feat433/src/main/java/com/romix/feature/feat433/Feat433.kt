package com.romix.feature.feat433

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat433Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat433UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat433FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat433UserSummary
)

data class Feat433UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat433NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat433Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat433Config = Feat433Config()
) {

    fun loadSnapshot(userId: Long): Feat433NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat433NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat433UserSummary {
        return Feat433UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat433FeedItem> {
        val result = java.util.ArrayList<Feat433FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat433FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat433UiMapper {

    fun mapToUi(model: List<Feat433FeedItem>): Feat433UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat433UiModel(
            header = UiText("Feat433 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat433UiModel =
        Feat433UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat433UiModel =
        Feat433UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat433UiModel =
        Feat433UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat433Service(
    private val repository: Feat433Repository,
    private val uiMapper: Feat433UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat433UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat433UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat433UserItem1(val user: CoreUser, val label: String)
data class Feat433UserItem2(val user: CoreUser, val label: String)
data class Feat433UserItem3(val user: CoreUser, val label: String)
data class Feat433UserItem4(val user: CoreUser, val label: String)
data class Feat433UserItem5(val user: CoreUser, val label: String)
data class Feat433UserItem6(val user: CoreUser, val label: String)
data class Feat433UserItem7(val user: CoreUser, val label: String)
data class Feat433UserItem8(val user: CoreUser, val label: String)
data class Feat433UserItem9(val user: CoreUser, val label: String)
data class Feat433UserItem10(val user: CoreUser, val label: String)

data class Feat433StateBlock1(val state: Feat433UiModel, val checksum: Int)
data class Feat433StateBlock2(val state: Feat433UiModel, val checksum: Int)
data class Feat433StateBlock3(val state: Feat433UiModel, val checksum: Int)
data class Feat433StateBlock4(val state: Feat433UiModel, val checksum: Int)
data class Feat433StateBlock5(val state: Feat433UiModel, val checksum: Int)
data class Feat433StateBlock6(val state: Feat433UiModel, val checksum: Int)
data class Feat433StateBlock7(val state: Feat433UiModel, val checksum: Int)
data class Feat433StateBlock8(val state: Feat433UiModel, val checksum: Int)
data class Feat433StateBlock9(val state: Feat433UiModel, val checksum: Int)
data class Feat433StateBlock10(val state: Feat433UiModel, val checksum: Int)

fun buildFeat433UserItem(user: CoreUser, index: Int): Feat433UserItem1 {
    return Feat433UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat433StateBlock(model: Feat433UiModel): Feat433StateBlock1 {
    return Feat433StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat433UserSummary> {
    val list = java.util.ArrayList<Feat433UserSummary>(users.size)
    for (user in users) {
        list += Feat433UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat433UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat433UiModel {
    val summaries = (0 until count).map {
        Feat433UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat433UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat433UiModel> {
    val models = java.util.ArrayList<Feat433UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat433AnalyticsEvent1(val name: String, val value: String)
data class Feat433AnalyticsEvent2(val name: String, val value: String)
data class Feat433AnalyticsEvent3(val name: String, val value: String)
data class Feat433AnalyticsEvent4(val name: String, val value: String)
data class Feat433AnalyticsEvent5(val name: String, val value: String)
data class Feat433AnalyticsEvent6(val name: String, val value: String)
data class Feat433AnalyticsEvent7(val name: String, val value: String)
data class Feat433AnalyticsEvent8(val name: String, val value: String)
data class Feat433AnalyticsEvent9(val name: String, val value: String)
data class Feat433AnalyticsEvent10(val name: String, val value: String)

fun logFeat433Event1(event: Feat433AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat433Event2(event: Feat433AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat433Event3(event: Feat433AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat433Event4(event: Feat433AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat433Event5(event: Feat433AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat433Event6(event: Feat433AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat433Event7(event: Feat433AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat433Event8(event: Feat433AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat433Event9(event: Feat433AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat433Event10(event: Feat433AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat433Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat433Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat433Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat433Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat433Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat433Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat433Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat433Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat433Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat433Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat433(u: CoreUser): Feat433Projection1 =
    Feat433Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat433Projection1> {
    val list = java.util.ArrayList<Feat433Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat433(u)
    }
    return list
}
