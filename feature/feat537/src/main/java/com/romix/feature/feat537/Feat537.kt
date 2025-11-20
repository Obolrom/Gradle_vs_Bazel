package com.romix.feature.feat537

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat537Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat537UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat537FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat537UserSummary
)

data class Feat537UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat537NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat537Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat537Config = Feat537Config()
) {

    fun loadSnapshot(userId: Long): Feat537NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat537NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat537UserSummary {
        return Feat537UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat537FeedItem> {
        val result = java.util.ArrayList<Feat537FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat537FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat537UiMapper {

    fun mapToUi(model: List<Feat537FeedItem>): Feat537UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat537UiModel(
            header = UiText("Feat537 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat537UiModel =
        Feat537UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat537UiModel =
        Feat537UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat537UiModel =
        Feat537UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat537Service(
    private val repository: Feat537Repository,
    private val uiMapper: Feat537UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat537UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat537UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat537UserItem1(val user: CoreUser, val label: String)
data class Feat537UserItem2(val user: CoreUser, val label: String)
data class Feat537UserItem3(val user: CoreUser, val label: String)
data class Feat537UserItem4(val user: CoreUser, val label: String)
data class Feat537UserItem5(val user: CoreUser, val label: String)
data class Feat537UserItem6(val user: CoreUser, val label: String)
data class Feat537UserItem7(val user: CoreUser, val label: String)
data class Feat537UserItem8(val user: CoreUser, val label: String)
data class Feat537UserItem9(val user: CoreUser, val label: String)
data class Feat537UserItem10(val user: CoreUser, val label: String)

data class Feat537StateBlock1(val state: Feat537UiModel, val checksum: Int)
data class Feat537StateBlock2(val state: Feat537UiModel, val checksum: Int)
data class Feat537StateBlock3(val state: Feat537UiModel, val checksum: Int)
data class Feat537StateBlock4(val state: Feat537UiModel, val checksum: Int)
data class Feat537StateBlock5(val state: Feat537UiModel, val checksum: Int)
data class Feat537StateBlock6(val state: Feat537UiModel, val checksum: Int)
data class Feat537StateBlock7(val state: Feat537UiModel, val checksum: Int)
data class Feat537StateBlock8(val state: Feat537UiModel, val checksum: Int)
data class Feat537StateBlock9(val state: Feat537UiModel, val checksum: Int)
data class Feat537StateBlock10(val state: Feat537UiModel, val checksum: Int)

fun buildFeat537UserItem(user: CoreUser, index: Int): Feat537UserItem1 {
    return Feat537UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat537StateBlock(model: Feat537UiModel): Feat537StateBlock1 {
    return Feat537StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat537UserSummary> {
    val list = java.util.ArrayList<Feat537UserSummary>(users.size)
    for (user in users) {
        list += Feat537UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat537UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat537UiModel {
    val summaries = (0 until count).map {
        Feat537UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat537UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat537UiModel> {
    val models = java.util.ArrayList<Feat537UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat537AnalyticsEvent1(val name: String, val value: String)
data class Feat537AnalyticsEvent2(val name: String, val value: String)
data class Feat537AnalyticsEvent3(val name: String, val value: String)
data class Feat537AnalyticsEvent4(val name: String, val value: String)
data class Feat537AnalyticsEvent5(val name: String, val value: String)
data class Feat537AnalyticsEvent6(val name: String, val value: String)
data class Feat537AnalyticsEvent7(val name: String, val value: String)
data class Feat537AnalyticsEvent8(val name: String, val value: String)
data class Feat537AnalyticsEvent9(val name: String, val value: String)
data class Feat537AnalyticsEvent10(val name: String, val value: String)

fun logFeat537Event1(event: Feat537AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat537Event2(event: Feat537AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat537Event3(event: Feat537AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat537Event4(event: Feat537AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat537Event5(event: Feat537AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat537Event6(event: Feat537AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat537Event7(event: Feat537AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat537Event8(event: Feat537AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat537Event9(event: Feat537AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat537Event10(event: Feat537AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat537Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat537Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat537Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat537Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat537Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat537Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat537Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat537Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat537Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat537Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat537(u: CoreUser): Feat537Projection1 =
    Feat537Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat537Projection1> {
    val list = java.util.ArrayList<Feat537Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat537(u)
    }
    return list
}
