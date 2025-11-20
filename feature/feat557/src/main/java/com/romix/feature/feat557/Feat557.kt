package com.romix.feature.feat557

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat557Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat557UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat557FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat557UserSummary
)

data class Feat557UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat557NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat557Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat557Config = Feat557Config()
) {

    fun loadSnapshot(userId: Long): Feat557NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat557NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat557UserSummary {
        return Feat557UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat557FeedItem> {
        val result = java.util.ArrayList<Feat557FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat557FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat557UiMapper {

    fun mapToUi(model: List<Feat557FeedItem>): Feat557UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat557UiModel(
            header = UiText("Feat557 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat557UiModel =
        Feat557UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat557UiModel =
        Feat557UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat557UiModel =
        Feat557UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat557Service(
    private val repository: Feat557Repository,
    private val uiMapper: Feat557UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat557UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat557UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat557UserItem1(val user: CoreUser, val label: String)
data class Feat557UserItem2(val user: CoreUser, val label: String)
data class Feat557UserItem3(val user: CoreUser, val label: String)
data class Feat557UserItem4(val user: CoreUser, val label: String)
data class Feat557UserItem5(val user: CoreUser, val label: String)
data class Feat557UserItem6(val user: CoreUser, val label: String)
data class Feat557UserItem7(val user: CoreUser, val label: String)
data class Feat557UserItem8(val user: CoreUser, val label: String)
data class Feat557UserItem9(val user: CoreUser, val label: String)
data class Feat557UserItem10(val user: CoreUser, val label: String)

data class Feat557StateBlock1(val state: Feat557UiModel, val checksum: Int)
data class Feat557StateBlock2(val state: Feat557UiModel, val checksum: Int)
data class Feat557StateBlock3(val state: Feat557UiModel, val checksum: Int)
data class Feat557StateBlock4(val state: Feat557UiModel, val checksum: Int)
data class Feat557StateBlock5(val state: Feat557UiModel, val checksum: Int)
data class Feat557StateBlock6(val state: Feat557UiModel, val checksum: Int)
data class Feat557StateBlock7(val state: Feat557UiModel, val checksum: Int)
data class Feat557StateBlock8(val state: Feat557UiModel, val checksum: Int)
data class Feat557StateBlock9(val state: Feat557UiModel, val checksum: Int)
data class Feat557StateBlock10(val state: Feat557UiModel, val checksum: Int)

fun buildFeat557UserItem(user: CoreUser, index: Int): Feat557UserItem1 {
    return Feat557UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat557StateBlock(model: Feat557UiModel): Feat557StateBlock1 {
    return Feat557StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat557UserSummary> {
    val list = java.util.ArrayList<Feat557UserSummary>(users.size)
    for (user in users) {
        list += Feat557UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat557UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat557UiModel {
    val summaries = (0 until count).map {
        Feat557UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat557UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat557UiModel> {
    val models = java.util.ArrayList<Feat557UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat557AnalyticsEvent1(val name: String, val value: String)
data class Feat557AnalyticsEvent2(val name: String, val value: String)
data class Feat557AnalyticsEvent3(val name: String, val value: String)
data class Feat557AnalyticsEvent4(val name: String, val value: String)
data class Feat557AnalyticsEvent5(val name: String, val value: String)
data class Feat557AnalyticsEvent6(val name: String, val value: String)
data class Feat557AnalyticsEvent7(val name: String, val value: String)
data class Feat557AnalyticsEvent8(val name: String, val value: String)
data class Feat557AnalyticsEvent9(val name: String, val value: String)
data class Feat557AnalyticsEvent10(val name: String, val value: String)

fun logFeat557Event1(event: Feat557AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat557Event2(event: Feat557AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat557Event3(event: Feat557AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat557Event4(event: Feat557AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat557Event5(event: Feat557AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat557Event6(event: Feat557AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat557Event7(event: Feat557AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat557Event8(event: Feat557AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat557Event9(event: Feat557AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat557Event10(event: Feat557AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat557Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat557Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat557Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat557Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat557Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat557Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat557Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat557Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat557Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat557Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat557(u: CoreUser): Feat557Projection1 =
    Feat557Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat557Projection1> {
    val list = java.util.ArrayList<Feat557Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat557(u)
    }
    return list
}
