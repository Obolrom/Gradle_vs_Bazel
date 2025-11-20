package com.romix.feature.feat252

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat252Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat252UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat252FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat252UserSummary
)

data class Feat252UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat252NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat252Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat252Config = Feat252Config()
) {

    fun loadSnapshot(userId: Long): Feat252NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat252NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat252UserSummary {
        return Feat252UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat252FeedItem> {
        val result = java.util.ArrayList<Feat252FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat252FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat252UiMapper {

    fun mapToUi(model: List<Feat252FeedItem>): Feat252UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat252UiModel(
            header = UiText("Feat252 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat252UiModel =
        Feat252UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat252UiModel =
        Feat252UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat252UiModel =
        Feat252UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat252Service(
    private val repository: Feat252Repository,
    private val uiMapper: Feat252UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat252UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat252UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat252UserItem1(val user: CoreUser, val label: String)
data class Feat252UserItem2(val user: CoreUser, val label: String)
data class Feat252UserItem3(val user: CoreUser, val label: String)
data class Feat252UserItem4(val user: CoreUser, val label: String)
data class Feat252UserItem5(val user: CoreUser, val label: String)
data class Feat252UserItem6(val user: CoreUser, val label: String)
data class Feat252UserItem7(val user: CoreUser, val label: String)
data class Feat252UserItem8(val user: CoreUser, val label: String)
data class Feat252UserItem9(val user: CoreUser, val label: String)
data class Feat252UserItem10(val user: CoreUser, val label: String)

data class Feat252StateBlock1(val state: Feat252UiModel, val checksum: Int)
data class Feat252StateBlock2(val state: Feat252UiModel, val checksum: Int)
data class Feat252StateBlock3(val state: Feat252UiModel, val checksum: Int)
data class Feat252StateBlock4(val state: Feat252UiModel, val checksum: Int)
data class Feat252StateBlock5(val state: Feat252UiModel, val checksum: Int)
data class Feat252StateBlock6(val state: Feat252UiModel, val checksum: Int)
data class Feat252StateBlock7(val state: Feat252UiModel, val checksum: Int)
data class Feat252StateBlock8(val state: Feat252UiModel, val checksum: Int)
data class Feat252StateBlock9(val state: Feat252UiModel, val checksum: Int)
data class Feat252StateBlock10(val state: Feat252UiModel, val checksum: Int)

fun buildFeat252UserItem(user: CoreUser, index: Int): Feat252UserItem1 {
    return Feat252UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat252StateBlock(model: Feat252UiModel): Feat252StateBlock1 {
    return Feat252StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat252UserSummary> {
    val list = java.util.ArrayList<Feat252UserSummary>(users.size)
    for (user in users) {
        list += Feat252UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat252UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat252UiModel {
    val summaries = (0 until count).map {
        Feat252UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat252UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat252UiModel> {
    val models = java.util.ArrayList<Feat252UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat252AnalyticsEvent1(val name: String, val value: String)
data class Feat252AnalyticsEvent2(val name: String, val value: String)
data class Feat252AnalyticsEvent3(val name: String, val value: String)
data class Feat252AnalyticsEvent4(val name: String, val value: String)
data class Feat252AnalyticsEvent5(val name: String, val value: String)
data class Feat252AnalyticsEvent6(val name: String, val value: String)
data class Feat252AnalyticsEvent7(val name: String, val value: String)
data class Feat252AnalyticsEvent8(val name: String, val value: String)
data class Feat252AnalyticsEvent9(val name: String, val value: String)
data class Feat252AnalyticsEvent10(val name: String, val value: String)

fun logFeat252Event1(event: Feat252AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat252Event2(event: Feat252AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat252Event3(event: Feat252AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat252Event4(event: Feat252AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat252Event5(event: Feat252AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat252Event6(event: Feat252AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat252Event7(event: Feat252AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat252Event8(event: Feat252AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat252Event9(event: Feat252AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat252Event10(event: Feat252AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat252Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat252Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat252Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat252Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat252Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat252Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat252Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat252Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat252Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat252Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat252(u: CoreUser): Feat252Projection1 =
    Feat252Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat252Projection1> {
    val list = java.util.ArrayList<Feat252Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat252(u)
    }
    return list
}
