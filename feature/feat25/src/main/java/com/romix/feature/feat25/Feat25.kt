package com.romix.feature.feat25

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat25Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat25UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat25FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat25UserSummary
)

data class Feat25UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat25NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat25Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat25Config = Feat25Config()
) {

    fun loadSnapshot(userId: Long): Feat25NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat25NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat25UserSummary {
        return Feat25UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat25FeedItem> {
        val result = java.util.ArrayList<Feat25FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat25FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat25UiMapper {

    fun mapToUi(model: List<Feat25FeedItem>): Feat25UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat25UiModel(
            header = UiText("Feat25 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat25UiModel =
        Feat25UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat25UiModel =
        Feat25UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat25UiModel =
        Feat25UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat25Service(
    private val repository: Feat25Repository,
    private val uiMapper: Feat25UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat25UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat25UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat25UserItem1(val user: CoreUser, val label: String)
data class Feat25UserItem2(val user: CoreUser, val label: String)
data class Feat25UserItem3(val user: CoreUser, val label: String)
data class Feat25UserItem4(val user: CoreUser, val label: String)
data class Feat25UserItem5(val user: CoreUser, val label: String)
data class Feat25UserItem6(val user: CoreUser, val label: String)
data class Feat25UserItem7(val user: CoreUser, val label: String)
data class Feat25UserItem8(val user: CoreUser, val label: String)
data class Feat25UserItem9(val user: CoreUser, val label: String)
data class Feat25UserItem10(val user: CoreUser, val label: String)

data class Feat25StateBlock1(val state: Feat25UiModel, val checksum: Int)
data class Feat25StateBlock2(val state: Feat25UiModel, val checksum: Int)
data class Feat25StateBlock3(val state: Feat25UiModel, val checksum: Int)
data class Feat25StateBlock4(val state: Feat25UiModel, val checksum: Int)
data class Feat25StateBlock5(val state: Feat25UiModel, val checksum: Int)
data class Feat25StateBlock6(val state: Feat25UiModel, val checksum: Int)
data class Feat25StateBlock7(val state: Feat25UiModel, val checksum: Int)
data class Feat25StateBlock8(val state: Feat25UiModel, val checksum: Int)
data class Feat25StateBlock9(val state: Feat25UiModel, val checksum: Int)
data class Feat25StateBlock10(val state: Feat25UiModel, val checksum: Int)

fun buildFeat25UserItem(user: CoreUser, index: Int): Feat25UserItem1 {
    return Feat25UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat25StateBlock(model: Feat25UiModel): Feat25StateBlock1 {
    return Feat25StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat25UserSummary> {
    val list = java.util.ArrayList<Feat25UserSummary>(users.size)
    for (user in users) {
        list += Feat25UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat25UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat25UiModel {
    val summaries = (0 until count).map {
        Feat25UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat25UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat25UiModel> {
    val models = java.util.ArrayList<Feat25UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat25AnalyticsEvent1(val name: String, val value: String)
data class Feat25AnalyticsEvent2(val name: String, val value: String)
data class Feat25AnalyticsEvent3(val name: String, val value: String)
data class Feat25AnalyticsEvent4(val name: String, val value: String)
data class Feat25AnalyticsEvent5(val name: String, val value: String)
data class Feat25AnalyticsEvent6(val name: String, val value: String)
data class Feat25AnalyticsEvent7(val name: String, val value: String)
data class Feat25AnalyticsEvent8(val name: String, val value: String)
data class Feat25AnalyticsEvent9(val name: String, val value: String)
data class Feat25AnalyticsEvent10(val name: String, val value: String)

fun logFeat25Event1(event: Feat25AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat25Event2(event: Feat25AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat25Event3(event: Feat25AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat25Event4(event: Feat25AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat25Event5(event: Feat25AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat25Event6(event: Feat25AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat25Event7(event: Feat25AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat25Event8(event: Feat25AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat25Event9(event: Feat25AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat25Event10(event: Feat25AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat25Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat25Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat25Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat25Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat25Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat25Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat25Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat25Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat25Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat25Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat25(u: CoreUser): Feat25Projection1 =
    Feat25Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat25Projection1> {
    val list = java.util.ArrayList<Feat25Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat25(u)
    }
    return list
}
