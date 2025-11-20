package com.romix.feature.feat459

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat459Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat459UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat459FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat459UserSummary
)

data class Feat459UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat459NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat459Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat459Config = Feat459Config()
) {

    fun loadSnapshot(userId: Long): Feat459NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat459NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat459UserSummary {
        return Feat459UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat459FeedItem> {
        val result = java.util.ArrayList<Feat459FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat459FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat459UiMapper {

    fun mapToUi(model: List<Feat459FeedItem>): Feat459UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat459UiModel(
            header = UiText("Feat459 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat459UiModel =
        Feat459UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat459UiModel =
        Feat459UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat459UiModel =
        Feat459UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat459Service(
    private val repository: Feat459Repository,
    private val uiMapper: Feat459UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat459UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat459UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat459UserItem1(val user: CoreUser, val label: String)
data class Feat459UserItem2(val user: CoreUser, val label: String)
data class Feat459UserItem3(val user: CoreUser, val label: String)
data class Feat459UserItem4(val user: CoreUser, val label: String)
data class Feat459UserItem5(val user: CoreUser, val label: String)
data class Feat459UserItem6(val user: CoreUser, val label: String)
data class Feat459UserItem7(val user: CoreUser, val label: String)
data class Feat459UserItem8(val user: CoreUser, val label: String)
data class Feat459UserItem9(val user: CoreUser, val label: String)
data class Feat459UserItem10(val user: CoreUser, val label: String)

data class Feat459StateBlock1(val state: Feat459UiModel, val checksum: Int)
data class Feat459StateBlock2(val state: Feat459UiModel, val checksum: Int)
data class Feat459StateBlock3(val state: Feat459UiModel, val checksum: Int)
data class Feat459StateBlock4(val state: Feat459UiModel, val checksum: Int)
data class Feat459StateBlock5(val state: Feat459UiModel, val checksum: Int)
data class Feat459StateBlock6(val state: Feat459UiModel, val checksum: Int)
data class Feat459StateBlock7(val state: Feat459UiModel, val checksum: Int)
data class Feat459StateBlock8(val state: Feat459UiModel, val checksum: Int)
data class Feat459StateBlock9(val state: Feat459UiModel, val checksum: Int)
data class Feat459StateBlock10(val state: Feat459UiModel, val checksum: Int)

fun buildFeat459UserItem(user: CoreUser, index: Int): Feat459UserItem1 {
    return Feat459UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat459StateBlock(model: Feat459UiModel): Feat459StateBlock1 {
    return Feat459StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat459UserSummary> {
    val list = java.util.ArrayList<Feat459UserSummary>(users.size)
    for (user in users) {
        list += Feat459UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat459UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat459UiModel {
    val summaries = (0 until count).map {
        Feat459UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat459UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat459UiModel> {
    val models = java.util.ArrayList<Feat459UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat459AnalyticsEvent1(val name: String, val value: String)
data class Feat459AnalyticsEvent2(val name: String, val value: String)
data class Feat459AnalyticsEvent3(val name: String, val value: String)
data class Feat459AnalyticsEvent4(val name: String, val value: String)
data class Feat459AnalyticsEvent5(val name: String, val value: String)
data class Feat459AnalyticsEvent6(val name: String, val value: String)
data class Feat459AnalyticsEvent7(val name: String, val value: String)
data class Feat459AnalyticsEvent8(val name: String, val value: String)
data class Feat459AnalyticsEvent9(val name: String, val value: String)
data class Feat459AnalyticsEvent10(val name: String, val value: String)

fun logFeat459Event1(event: Feat459AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat459Event2(event: Feat459AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat459Event3(event: Feat459AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat459Event4(event: Feat459AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat459Event5(event: Feat459AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat459Event6(event: Feat459AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat459Event7(event: Feat459AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat459Event8(event: Feat459AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat459Event9(event: Feat459AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat459Event10(event: Feat459AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat459Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat459Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat459Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat459Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat459Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat459Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat459Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat459Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat459Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat459Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat459(u: CoreUser): Feat459Projection1 =
    Feat459Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat459Projection1> {
    val list = java.util.ArrayList<Feat459Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat459(u)
    }
    return list
}
