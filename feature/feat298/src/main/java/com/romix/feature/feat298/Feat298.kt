package com.romix.feature.feat298

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat298Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat298UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat298FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat298UserSummary
)

data class Feat298UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat298NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat298Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat298Config = Feat298Config()
) {

    fun loadSnapshot(userId: Long): Feat298NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat298NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat298UserSummary {
        return Feat298UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat298FeedItem> {
        val result = java.util.ArrayList<Feat298FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat298FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat298UiMapper {

    fun mapToUi(model: List<Feat298FeedItem>): Feat298UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat298UiModel(
            header = UiText("Feat298 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat298UiModel =
        Feat298UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat298UiModel =
        Feat298UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat298UiModel =
        Feat298UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat298Service(
    private val repository: Feat298Repository,
    private val uiMapper: Feat298UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat298UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat298UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat298UserItem1(val user: CoreUser, val label: String)
data class Feat298UserItem2(val user: CoreUser, val label: String)
data class Feat298UserItem3(val user: CoreUser, val label: String)
data class Feat298UserItem4(val user: CoreUser, val label: String)
data class Feat298UserItem5(val user: CoreUser, val label: String)
data class Feat298UserItem6(val user: CoreUser, val label: String)
data class Feat298UserItem7(val user: CoreUser, val label: String)
data class Feat298UserItem8(val user: CoreUser, val label: String)
data class Feat298UserItem9(val user: CoreUser, val label: String)
data class Feat298UserItem10(val user: CoreUser, val label: String)

data class Feat298StateBlock1(val state: Feat298UiModel, val checksum: Int)
data class Feat298StateBlock2(val state: Feat298UiModel, val checksum: Int)
data class Feat298StateBlock3(val state: Feat298UiModel, val checksum: Int)
data class Feat298StateBlock4(val state: Feat298UiModel, val checksum: Int)
data class Feat298StateBlock5(val state: Feat298UiModel, val checksum: Int)
data class Feat298StateBlock6(val state: Feat298UiModel, val checksum: Int)
data class Feat298StateBlock7(val state: Feat298UiModel, val checksum: Int)
data class Feat298StateBlock8(val state: Feat298UiModel, val checksum: Int)
data class Feat298StateBlock9(val state: Feat298UiModel, val checksum: Int)
data class Feat298StateBlock10(val state: Feat298UiModel, val checksum: Int)

fun buildFeat298UserItem(user: CoreUser, index: Int): Feat298UserItem1 {
    return Feat298UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat298StateBlock(model: Feat298UiModel): Feat298StateBlock1 {
    return Feat298StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat298UserSummary> {
    val list = java.util.ArrayList<Feat298UserSummary>(users.size)
    for (user in users) {
        list += Feat298UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat298UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat298UiModel {
    val summaries = (0 until count).map {
        Feat298UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat298UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat298UiModel> {
    val models = java.util.ArrayList<Feat298UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat298AnalyticsEvent1(val name: String, val value: String)
data class Feat298AnalyticsEvent2(val name: String, val value: String)
data class Feat298AnalyticsEvent3(val name: String, val value: String)
data class Feat298AnalyticsEvent4(val name: String, val value: String)
data class Feat298AnalyticsEvent5(val name: String, val value: String)
data class Feat298AnalyticsEvent6(val name: String, val value: String)
data class Feat298AnalyticsEvent7(val name: String, val value: String)
data class Feat298AnalyticsEvent8(val name: String, val value: String)
data class Feat298AnalyticsEvent9(val name: String, val value: String)
data class Feat298AnalyticsEvent10(val name: String, val value: String)

fun logFeat298Event1(event: Feat298AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat298Event2(event: Feat298AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat298Event3(event: Feat298AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat298Event4(event: Feat298AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat298Event5(event: Feat298AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat298Event6(event: Feat298AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat298Event7(event: Feat298AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat298Event8(event: Feat298AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat298Event9(event: Feat298AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat298Event10(event: Feat298AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat298Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat298Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat298Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat298Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat298Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat298Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat298Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat298Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat298Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat298Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat298(u: CoreUser): Feat298Projection1 =
    Feat298Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat298Projection1> {
    val list = java.util.ArrayList<Feat298Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat298(u)
    }
    return list
}
