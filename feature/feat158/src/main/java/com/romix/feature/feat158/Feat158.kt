package com.romix.feature.feat158

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat158Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat158UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat158FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat158UserSummary
)

data class Feat158UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat158NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat158Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat158Config = Feat158Config()
) {

    fun loadSnapshot(userId: Long): Feat158NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat158NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat158UserSummary {
        return Feat158UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat158FeedItem> {
        val result = java.util.ArrayList<Feat158FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat158FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat158UiMapper {

    fun mapToUi(model: List<Feat158FeedItem>): Feat158UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat158UiModel(
            header = UiText("Feat158 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat158UiModel =
        Feat158UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat158UiModel =
        Feat158UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat158UiModel =
        Feat158UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat158Service(
    private val repository: Feat158Repository,
    private val uiMapper: Feat158UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat158UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat158UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat158UserItem1(val user: CoreUser, val label: String)
data class Feat158UserItem2(val user: CoreUser, val label: String)
data class Feat158UserItem3(val user: CoreUser, val label: String)
data class Feat158UserItem4(val user: CoreUser, val label: String)
data class Feat158UserItem5(val user: CoreUser, val label: String)
data class Feat158UserItem6(val user: CoreUser, val label: String)
data class Feat158UserItem7(val user: CoreUser, val label: String)
data class Feat158UserItem8(val user: CoreUser, val label: String)
data class Feat158UserItem9(val user: CoreUser, val label: String)
data class Feat158UserItem10(val user: CoreUser, val label: String)

data class Feat158StateBlock1(val state: Feat158UiModel, val checksum: Int)
data class Feat158StateBlock2(val state: Feat158UiModel, val checksum: Int)
data class Feat158StateBlock3(val state: Feat158UiModel, val checksum: Int)
data class Feat158StateBlock4(val state: Feat158UiModel, val checksum: Int)
data class Feat158StateBlock5(val state: Feat158UiModel, val checksum: Int)
data class Feat158StateBlock6(val state: Feat158UiModel, val checksum: Int)
data class Feat158StateBlock7(val state: Feat158UiModel, val checksum: Int)
data class Feat158StateBlock8(val state: Feat158UiModel, val checksum: Int)
data class Feat158StateBlock9(val state: Feat158UiModel, val checksum: Int)
data class Feat158StateBlock10(val state: Feat158UiModel, val checksum: Int)

fun buildFeat158UserItem(user: CoreUser, index: Int): Feat158UserItem1 {
    return Feat158UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat158StateBlock(model: Feat158UiModel): Feat158StateBlock1 {
    return Feat158StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat158UserSummary> {
    val list = java.util.ArrayList<Feat158UserSummary>(users.size)
    for (user in users) {
        list += Feat158UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat158UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat158UiModel {
    val summaries = (0 until count).map {
        Feat158UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat158UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat158UiModel> {
    val models = java.util.ArrayList<Feat158UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat158AnalyticsEvent1(val name: String, val value: String)
data class Feat158AnalyticsEvent2(val name: String, val value: String)
data class Feat158AnalyticsEvent3(val name: String, val value: String)
data class Feat158AnalyticsEvent4(val name: String, val value: String)
data class Feat158AnalyticsEvent5(val name: String, val value: String)
data class Feat158AnalyticsEvent6(val name: String, val value: String)
data class Feat158AnalyticsEvent7(val name: String, val value: String)
data class Feat158AnalyticsEvent8(val name: String, val value: String)
data class Feat158AnalyticsEvent9(val name: String, val value: String)
data class Feat158AnalyticsEvent10(val name: String, val value: String)

fun logFeat158Event1(event: Feat158AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat158Event2(event: Feat158AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat158Event3(event: Feat158AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat158Event4(event: Feat158AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat158Event5(event: Feat158AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat158Event6(event: Feat158AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat158Event7(event: Feat158AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat158Event8(event: Feat158AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat158Event9(event: Feat158AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat158Event10(event: Feat158AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat158Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat158Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat158Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat158Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat158Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat158Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat158Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat158Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat158Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat158Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat158(u: CoreUser): Feat158Projection1 =
    Feat158Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat158Projection1> {
    val list = java.util.ArrayList<Feat158Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat158(u)
    }
    return list
}
