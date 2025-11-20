package com.romix.feature.feat516

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat516Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat516UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat516FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat516UserSummary
)

data class Feat516UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat516NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat516Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat516Config = Feat516Config()
) {

    fun loadSnapshot(userId: Long): Feat516NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat516NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat516UserSummary {
        return Feat516UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat516FeedItem> {
        val result = java.util.ArrayList<Feat516FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat516FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat516UiMapper {

    fun mapToUi(model: List<Feat516FeedItem>): Feat516UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat516UiModel(
            header = UiText("Feat516 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat516UiModel =
        Feat516UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat516UiModel =
        Feat516UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat516UiModel =
        Feat516UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat516Service(
    private val repository: Feat516Repository,
    private val uiMapper: Feat516UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat516UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat516UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat516UserItem1(val user: CoreUser, val label: String)
data class Feat516UserItem2(val user: CoreUser, val label: String)
data class Feat516UserItem3(val user: CoreUser, val label: String)
data class Feat516UserItem4(val user: CoreUser, val label: String)
data class Feat516UserItem5(val user: CoreUser, val label: String)
data class Feat516UserItem6(val user: CoreUser, val label: String)
data class Feat516UserItem7(val user: CoreUser, val label: String)
data class Feat516UserItem8(val user: CoreUser, val label: String)
data class Feat516UserItem9(val user: CoreUser, val label: String)
data class Feat516UserItem10(val user: CoreUser, val label: String)

data class Feat516StateBlock1(val state: Feat516UiModel, val checksum: Int)
data class Feat516StateBlock2(val state: Feat516UiModel, val checksum: Int)
data class Feat516StateBlock3(val state: Feat516UiModel, val checksum: Int)
data class Feat516StateBlock4(val state: Feat516UiModel, val checksum: Int)
data class Feat516StateBlock5(val state: Feat516UiModel, val checksum: Int)
data class Feat516StateBlock6(val state: Feat516UiModel, val checksum: Int)
data class Feat516StateBlock7(val state: Feat516UiModel, val checksum: Int)
data class Feat516StateBlock8(val state: Feat516UiModel, val checksum: Int)
data class Feat516StateBlock9(val state: Feat516UiModel, val checksum: Int)
data class Feat516StateBlock10(val state: Feat516UiModel, val checksum: Int)

fun buildFeat516UserItem(user: CoreUser, index: Int): Feat516UserItem1 {
    return Feat516UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat516StateBlock(model: Feat516UiModel): Feat516StateBlock1 {
    return Feat516StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat516UserSummary> {
    val list = java.util.ArrayList<Feat516UserSummary>(users.size)
    for (user in users) {
        list += Feat516UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat516UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat516UiModel {
    val summaries = (0 until count).map {
        Feat516UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat516UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat516UiModel> {
    val models = java.util.ArrayList<Feat516UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat516AnalyticsEvent1(val name: String, val value: String)
data class Feat516AnalyticsEvent2(val name: String, val value: String)
data class Feat516AnalyticsEvent3(val name: String, val value: String)
data class Feat516AnalyticsEvent4(val name: String, val value: String)
data class Feat516AnalyticsEvent5(val name: String, val value: String)
data class Feat516AnalyticsEvent6(val name: String, val value: String)
data class Feat516AnalyticsEvent7(val name: String, val value: String)
data class Feat516AnalyticsEvent8(val name: String, val value: String)
data class Feat516AnalyticsEvent9(val name: String, val value: String)
data class Feat516AnalyticsEvent10(val name: String, val value: String)

fun logFeat516Event1(event: Feat516AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat516Event2(event: Feat516AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat516Event3(event: Feat516AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat516Event4(event: Feat516AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat516Event5(event: Feat516AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat516Event6(event: Feat516AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat516Event7(event: Feat516AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat516Event8(event: Feat516AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat516Event9(event: Feat516AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat516Event10(event: Feat516AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat516Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat516Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat516Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat516Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat516Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat516Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat516Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat516Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat516Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat516Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat516(u: CoreUser): Feat516Projection1 =
    Feat516Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat516Projection1> {
    val list = java.util.ArrayList<Feat516Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat516(u)
    }
    return list
}
