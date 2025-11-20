package com.romix.feature.feat445

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat445Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat445UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat445FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat445UserSummary
)

data class Feat445UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat445NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat445Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat445Config = Feat445Config()
) {

    fun loadSnapshot(userId: Long): Feat445NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat445NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat445UserSummary {
        return Feat445UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat445FeedItem> {
        val result = java.util.ArrayList<Feat445FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat445FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat445UiMapper {

    fun mapToUi(model: List<Feat445FeedItem>): Feat445UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat445UiModel(
            header = UiText("Feat445 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat445UiModel =
        Feat445UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat445UiModel =
        Feat445UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat445UiModel =
        Feat445UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat445Service(
    private val repository: Feat445Repository,
    private val uiMapper: Feat445UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat445UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat445UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat445UserItem1(val user: CoreUser, val label: String)
data class Feat445UserItem2(val user: CoreUser, val label: String)
data class Feat445UserItem3(val user: CoreUser, val label: String)
data class Feat445UserItem4(val user: CoreUser, val label: String)
data class Feat445UserItem5(val user: CoreUser, val label: String)
data class Feat445UserItem6(val user: CoreUser, val label: String)
data class Feat445UserItem7(val user: CoreUser, val label: String)
data class Feat445UserItem8(val user: CoreUser, val label: String)
data class Feat445UserItem9(val user: CoreUser, val label: String)
data class Feat445UserItem10(val user: CoreUser, val label: String)

data class Feat445StateBlock1(val state: Feat445UiModel, val checksum: Int)
data class Feat445StateBlock2(val state: Feat445UiModel, val checksum: Int)
data class Feat445StateBlock3(val state: Feat445UiModel, val checksum: Int)
data class Feat445StateBlock4(val state: Feat445UiModel, val checksum: Int)
data class Feat445StateBlock5(val state: Feat445UiModel, val checksum: Int)
data class Feat445StateBlock6(val state: Feat445UiModel, val checksum: Int)
data class Feat445StateBlock7(val state: Feat445UiModel, val checksum: Int)
data class Feat445StateBlock8(val state: Feat445UiModel, val checksum: Int)
data class Feat445StateBlock9(val state: Feat445UiModel, val checksum: Int)
data class Feat445StateBlock10(val state: Feat445UiModel, val checksum: Int)

fun buildFeat445UserItem(user: CoreUser, index: Int): Feat445UserItem1 {
    return Feat445UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat445StateBlock(model: Feat445UiModel): Feat445StateBlock1 {
    return Feat445StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat445UserSummary> {
    val list = java.util.ArrayList<Feat445UserSummary>(users.size)
    for (user in users) {
        list += Feat445UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat445UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat445UiModel {
    val summaries = (0 until count).map {
        Feat445UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat445UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat445UiModel> {
    val models = java.util.ArrayList<Feat445UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat445AnalyticsEvent1(val name: String, val value: String)
data class Feat445AnalyticsEvent2(val name: String, val value: String)
data class Feat445AnalyticsEvent3(val name: String, val value: String)
data class Feat445AnalyticsEvent4(val name: String, val value: String)
data class Feat445AnalyticsEvent5(val name: String, val value: String)
data class Feat445AnalyticsEvent6(val name: String, val value: String)
data class Feat445AnalyticsEvent7(val name: String, val value: String)
data class Feat445AnalyticsEvent8(val name: String, val value: String)
data class Feat445AnalyticsEvent9(val name: String, val value: String)
data class Feat445AnalyticsEvent10(val name: String, val value: String)

fun logFeat445Event1(event: Feat445AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat445Event2(event: Feat445AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat445Event3(event: Feat445AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat445Event4(event: Feat445AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat445Event5(event: Feat445AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat445Event6(event: Feat445AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat445Event7(event: Feat445AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat445Event8(event: Feat445AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat445Event9(event: Feat445AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat445Event10(event: Feat445AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat445Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat445Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat445Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat445Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat445Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat445Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat445Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat445Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat445Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat445Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat445(u: CoreUser): Feat445Projection1 =
    Feat445Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat445Projection1> {
    val list = java.util.ArrayList<Feat445Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat445(u)
    }
    return list
}
