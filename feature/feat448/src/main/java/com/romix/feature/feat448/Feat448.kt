package com.romix.feature.feat448

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat448Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat448UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat448FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat448UserSummary
)

data class Feat448UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat448NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat448Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat448Config = Feat448Config()
) {

    fun loadSnapshot(userId: Long): Feat448NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat448NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat448UserSummary {
        return Feat448UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat448FeedItem> {
        val result = java.util.ArrayList<Feat448FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat448FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat448UiMapper {

    fun mapToUi(model: List<Feat448FeedItem>): Feat448UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat448UiModel(
            header = UiText("Feat448 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat448UiModel =
        Feat448UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat448UiModel =
        Feat448UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat448UiModel =
        Feat448UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat448Service(
    private val repository: Feat448Repository,
    private val uiMapper: Feat448UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat448UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat448UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat448UserItem1(val user: CoreUser, val label: String)
data class Feat448UserItem2(val user: CoreUser, val label: String)
data class Feat448UserItem3(val user: CoreUser, val label: String)
data class Feat448UserItem4(val user: CoreUser, val label: String)
data class Feat448UserItem5(val user: CoreUser, val label: String)
data class Feat448UserItem6(val user: CoreUser, val label: String)
data class Feat448UserItem7(val user: CoreUser, val label: String)
data class Feat448UserItem8(val user: CoreUser, val label: String)
data class Feat448UserItem9(val user: CoreUser, val label: String)
data class Feat448UserItem10(val user: CoreUser, val label: String)

data class Feat448StateBlock1(val state: Feat448UiModel, val checksum: Int)
data class Feat448StateBlock2(val state: Feat448UiModel, val checksum: Int)
data class Feat448StateBlock3(val state: Feat448UiModel, val checksum: Int)
data class Feat448StateBlock4(val state: Feat448UiModel, val checksum: Int)
data class Feat448StateBlock5(val state: Feat448UiModel, val checksum: Int)
data class Feat448StateBlock6(val state: Feat448UiModel, val checksum: Int)
data class Feat448StateBlock7(val state: Feat448UiModel, val checksum: Int)
data class Feat448StateBlock8(val state: Feat448UiModel, val checksum: Int)
data class Feat448StateBlock9(val state: Feat448UiModel, val checksum: Int)
data class Feat448StateBlock10(val state: Feat448UiModel, val checksum: Int)

fun buildFeat448UserItem(user: CoreUser, index: Int): Feat448UserItem1 {
    return Feat448UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat448StateBlock(model: Feat448UiModel): Feat448StateBlock1 {
    return Feat448StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat448UserSummary> {
    val list = java.util.ArrayList<Feat448UserSummary>(users.size)
    for (user in users) {
        list += Feat448UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat448UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat448UiModel {
    val summaries = (0 until count).map {
        Feat448UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat448UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat448UiModel> {
    val models = java.util.ArrayList<Feat448UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat448AnalyticsEvent1(val name: String, val value: String)
data class Feat448AnalyticsEvent2(val name: String, val value: String)
data class Feat448AnalyticsEvent3(val name: String, val value: String)
data class Feat448AnalyticsEvent4(val name: String, val value: String)
data class Feat448AnalyticsEvent5(val name: String, val value: String)
data class Feat448AnalyticsEvent6(val name: String, val value: String)
data class Feat448AnalyticsEvent7(val name: String, val value: String)
data class Feat448AnalyticsEvent8(val name: String, val value: String)
data class Feat448AnalyticsEvent9(val name: String, val value: String)
data class Feat448AnalyticsEvent10(val name: String, val value: String)

fun logFeat448Event1(event: Feat448AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat448Event2(event: Feat448AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat448Event3(event: Feat448AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat448Event4(event: Feat448AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat448Event5(event: Feat448AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat448Event6(event: Feat448AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat448Event7(event: Feat448AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat448Event8(event: Feat448AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat448Event9(event: Feat448AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat448Event10(event: Feat448AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat448Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat448Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat448Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat448Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat448Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat448Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat448Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat448Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat448Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat448Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat448(u: CoreUser): Feat448Projection1 =
    Feat448Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat448Projection1> {
    val list = java.util.ArrayList<Feat448Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat448(u)
    }
    return list
}
