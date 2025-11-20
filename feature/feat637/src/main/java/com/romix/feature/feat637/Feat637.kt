package com.romix.feature.feat637

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat637Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat637UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat637FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat637UserSummary
)

data class Feat637UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat637NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat637Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat637Config = Feat637Config()
) {

    fun loadSnapshot(userId: Long): Feat637NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat637NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat637UserSummary {
        return Feat637UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat637FeedItem> {
        val result = java.util.ArrayList<Feat637FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat637FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat637UiMapper {

    fun mapToUi(model: List<Feat637FeedItem>): Feat637UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat637UiModel(
            header = UiText("Feat637 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat637UiModel =
        Feat637UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat637UiModel =
        Feat637UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat637UiModel =
        Feat637UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat637Service(
    private val repository: Feat637Repository,
    private val uiMapper: Feat637UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat637UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat637UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat637UserItem1(val user: CoreUser, val label: String)
data class Feat637UserItem2(val user: CoreUser, val label: String)
data class Feat637UserItem3(val user: CoreUser, val label: String)
data class Feat637UserItem4(val user: CoreUser, val label: String)
data class Feat637UserItem5(val user: CoreUser, val label: String)
data class Feat637UserItem6(val user: CoreUser, val label: String)
data class Feat637UserItem7(val user: CoreUser, val label: String)
data class Feat637UserItem8(val user: CoreUser, val label: String)
data class Feat637UserItem9(val user: CoreUser, val label: String)
data class Feat637UserItem10(val user: CoreUser, val label: String)

data class Feat637StateBlock1(val state: Feat637UiModel, val checksum: Int)
data class Feat637StateBlock2(val state: Feat637UiModel, val checksum: Int)
data class Feat637StateBlock3(val state: Feat637UiModel, val checksum: Int)
data class Feat637StateBlock4(val state: Feat637UiModel, val checksum: Int)
data class Feat637StateBlock5(val state: Feat637UiModel, val checksum: Int)
data class Feat637StateBlock6(val state: Feat637UiModel, val checksum: Int)
data class Feat637StateBlock7(val state: Feat637UiModel, val checksum: Int)
data class Feat637StateBlock8(val state: Feat637UiModel, val checksum: Int)
data class Feat637StateBlock9(val state: Feat637UiModel, val checksum: Int)
data class Feat637StateBlock10(val state: Feat637UiModel, val checksum: Int)

fun buildFeat637UserItem(user: CoreUser, index: Int): Feat637UserItem1 {
    return Feat637UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat637StateBlock(model: Feat637UiModel): Feat637StateBlock1 {
    return Feat637StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat637UserSummary> {
    val list = java.util.ArrayList<Feat637UserSummary>(users.size)
    for (user in users) {
        list += Feat637UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat637UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat637UiModel {
    val summaries = (0 until count).map {
        Feat637UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat637UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat637UiModel> {
    val models = java.util.ArrayList<Feat637UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat637AnalyticsEvent1(val name: String, val value: String)
data class Feat637AnalyticsEvent2(val name: String, val value: String)
data class Feat637AnalyticsEvent3(val name: String, val value: String)
data class Feat637AnalyticsEvent4(val name: String, val value: String)
data class Feat637AnalyticsEvent5(val name: String, val value: String)
data class Feat637AnalyticsEvent6(val name: String, val value: String)
data class Feat637AnalyticsEvent7(val name: String, val value: String)
data class Feat637AnalyticsEvent8(val name: String, val value: String)
data class Feat637AnalyticsEvent9(val name: String, val value: String)
data class Feat637AnalyticsEvent10(val name: String, val value: String)

fun logFeat637Event1(event: Feat637AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat637Event2(event: Feat637AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat637Event3(event: Feat637AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat637Event4(event: Feat637AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat637Event5(event: Feat637AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat637Event6(event: Feat637AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat637Event7(event: Feat637AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat637Event8(event: Feat637AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat637Event9(event: Feat637AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat637Event10(event: Feat637AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat637Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat637Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat637Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat637Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat637Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat637Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat637Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat637Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat637Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat637Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat637(u: CoreUser): Feat637Projection1 =
    Feat637Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat637Projection1> {
    val list = java.util.ArrayList<Feat637Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat637(u)
    }
    return list
}
