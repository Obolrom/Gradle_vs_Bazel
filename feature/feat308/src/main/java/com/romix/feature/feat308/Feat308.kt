package com.romix.feature.feat308

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat308Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat308UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat308FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat308UserSummary
)

data class Feat308UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat308NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat308Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat308Config = Feat308Config()
) {

    fun loadSnapshot(userId: Long): Feat308NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat308NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat308UserSummary {
        return Feat308UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat308FeedItem> {
        val result = java.util.ArrayList<Feat308FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat308FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat308UiMapper {

    fun mapToUi(model: List<Feat308FeedItem>): Feat308UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat308UiModel(
            header = UiText("Feat308 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat308UiModel =
        Feat308UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat308UiModel =
        Feat308UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat308UiModel =
        Feat308UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat308Service(
    private val repository: Feat308Repository,
    private val uiMapper: Feat308UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat308UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat308UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat308UserItem1(val user: CoreUser, val label: String)
data class Feat308UserItem2(val user: CoreUser, val label: String)
data class Feat308UserItem3(val user: CoreUser, val label: String)
data class Feat308UserItem4(val user: CoreUser, val label: String)
data class Feat308UserItem5(val user: CoreUser, val label: String)
data class Feat308UserItem6(val user: CoreUser, val label: String)
data class Feat308UserItem7(val user: CoreUser, val label: String)
data class Feat308UserItem8(val user: CoreUser, val label: String)
data class Feat308UserItem9(val user: CoreUser, val label: String)
data class Feat308UserItem10(val user: CoreUser, val label: String)

data class Feat308StateBlock1(val state: Feat308UiModel, val checksum: Int)
data class Feat308StateBlock2(val state: Feat308UiModel, val checksum: Int)
data class Feat308StateBlock3(val state: Feat308UiModel, val checksum: Int)
data class Feat308StateBlock4(val state: Feat308UiModel, val checksum: Int)
data class Feat308StateBlock5(val state: Feat308UiModel, val checksum: Int)
data class Feat308StateBlock6(val state: Feat308UiModel, val checksum: Int)
data class Feat308StateBlock7(val state: Feat308UiModel, val checksum: Int)
data class Feat308StateBlock8(val state: Feat308UiModel, val checksum: Int)
data class Feat308StateBlock9(val state: Feat308UiModel, val checksum: Int)
data class Feat308StateBlock10(val state: Feat308UiModel, val checksum: Int)

fun buildFeat308UserItem(user: CoreUser, index: Int): Feat308UserItem1 {
    return Feat308UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat308StateBlock(model: Feat308UiModel): Feat308StateBlock1 {
    return Feat308StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat308UserSummary> {
    val list = java.util.ArrayList<Feat308UserSummary>(users.size)
    for (user in users) {
        list += Feat308UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat308UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat308UiModel {
    val summaries = (0 until count).map {
        Feat308UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat308UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat308UiModel> {
    val models = java.util.ArrayList<Feat308UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat308AnalyticsEvent1(val name: String, val value: String)
data class Feat308AnalyticsEvent2(val name: String, val value: String)
data class Feat308AnalyticsEvent3(val name: String, val value: String)
data class Feat308AnalyticsEvent4(val name: String, val value: String)
data class Feat308AnalyticsEvent5(val name: String, val value: String)
data class Feat308AnalyticsEvent6(val name: String, val value: String)
data class Feat308AnalyticsEvent7(val name: String, val value: String)
data class Feat308AnalyticsEvent8(val name: String, val value: String)
data class Feat308AnalyticsEvent9(val name: String, val value: String)
data class Feat308AnalyticsEvent10(val name: String, val value: String)

fun logFeat308Event1(event: Feat308AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat308Event2(event: Feat308AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat308Event3(event: Feat308AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat308Event4(event: Feat308AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat308Event5(event: Feat308AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat308Event6(event: Feat308AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat308Event7(event: Feat308AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat308Event8(event: Feat308AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat308Event9(event: Feat308AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat308Event10(event: Feat308AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat308Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat308Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat308Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat308Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat308Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat308Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat308Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat308Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat308Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat308Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat308(u: CoreUser): Feat308Projection1 =
    Feat308Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat308Projection1> {
    val list = java.util.ArrayList<Feat308Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat308(u)
    }
    return list
}
