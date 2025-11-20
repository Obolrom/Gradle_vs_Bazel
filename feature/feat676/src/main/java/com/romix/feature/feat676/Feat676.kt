package com.romix.feature.feat676

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat676Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat676UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat676FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat676UserSummary
)

data class Feat676UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat676NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat676Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat676Config = Feat676Config()
) {

    fun loadSnapshot(userId: Long): Feat676NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat676NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat676UserSummary {
        return Feat676UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat676FeedItem> {
        val result = java.util.ArrayList<Feat676FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat676FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat676UiMapper {

    fun mapToUi(model: List<Feat676FeedItem>): Feat676UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat676UiModel(
            header = UiText("Feat676 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat676UiModel =
        Feat676UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat676UiModel =
        Feat676UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat676UiModel =
        Feat676UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat676Service(
    private val repository: Feat676Repository,
    private val uiMapper: Feat676UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat676UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat676UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat676UserItem1(val user: CoreUser, val label: String)
data class Feat676UserItem2(val user: CoreUser, val label: String)
data class Feat676UserItem3(val user: CoreUser, val label: String)
data class Feat676UserItem4(val user: CoreUser, val label: String)
data class Feat676UserItem5(val user: CoreUser, val label: String)
data class Feat676UserItem6(val user: CoreUser, val label: String)
data class Feat676UserItem7(val user: CoreUser, val label: String)
data class Feat676UserItem8(val user: CoreUser, val label: String)
data class Feat676UserItem9(val user: CoreUser, val label: String)
data class Feat676UserItem10(val user: CoreUser, val label: String)

data class Feat676StateBlock1(val state: Feat676UiModel, val checksum: Int)
data class Feat676StateBlock2(val state: Feat676UiModel, val checksum: Int)
data class Feat676StateBlock3(val state: Feat676UiModel, val checksum: Int)
data class Feat676StateBlock4(val state: Feat676UiModel, val checksum: Int)
data class Feat676StateBlock5(val state: Feat676UiModel, val checksum: Int)
data class Feat676StateBlock6(val state: Feat676UiModel, val checksum: Int)
data class Feat676StateBlock7(val state: Feat676UiModel, val checksum: Int)
data class Feat676StateBlock8(val state: Feat676UiModel, val checksum: Int)
data class Feat676StateBlock9(val state: Feat676UiModel, val checksum: Int)
data class Feat676StateBlock10(val state: Feat676UiModel, val checksum: Int)

fun buildFeat676UserItem(user: CoreUser, index: Int): Feat676UserItem1 {
    return Feat676UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat676StateBlock(model: Feat676UiModel): Feat676StateBlock1 {
    return Feat676StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat676UserSummary> {
    val list = java.util.ArrayList<Feat676UserSummary>(users.size)
    for (user in users) {
        list += Feat676UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat676UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat676UiModel {
    val summaries = (0 until count).map {
        Feat676UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat676UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat676UiModel> {
    val models = java.util.ArrayList<Feat676UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat676AnalyticsEvent1(val name: String, val value: String)
data class Feat676AnalyticsEvent2(val name: String, val value: String)
data class Feat676AnalyticsEvent3(val name: String, val value: String)
data class Feat676AnalyticsEvent4(val name: String, val value: String)
data class Feat676AnalyticsEvent5(val name: String, val value: String)
data class Feat676AnalyticsEvent6(val name: String, val value: String)
data class Feat676AnalyticsEvent7(val name: String, val value: String)
data class Feat676AnalyticsEvent8(val name: String, val value: String)
data class Feat676AnalyticsEvent9(val name: String, val value: String)
data class Feat676AnalyticsEvent10(val name: String, val value: String)

fun logFeat676Event1(event: Feat676AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat676Event2(event: Feat676AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat676Event3(event: Feat676AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat676Event4(event: Feat676AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat676Event5(event: Feat676AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat676Event6(event: Feat676AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat676Event7(event: Feat676AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat676Event8(event: Feat676AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat676Event9(event: Feat676AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat676Event10(event: Feat676AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat676Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat676Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat676Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat676Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat676Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat676Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat676Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat676Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat676Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat676Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat676(u: CoreUser): Feat676Projection1 =
    Feat676Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat676Projection1> {
    val list = java.util.ArrayList<Feat676Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat676(u)
    }
    return list
}
