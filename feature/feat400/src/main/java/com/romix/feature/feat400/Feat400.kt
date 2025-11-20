package com.romix.feature.feat400

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat400Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat400UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat400FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat400UserSummary
)

data class Feat400UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat400NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat400Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat400Config = Feat400Config()
) {

    fun loadSnapshot(userId: Long): Feat400NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat400NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat400UserSummary {
        return Feat400UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat400FeedItem> {
        val result = java.util.ArrayList<Feat400FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat400FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat400UiMapper {

    fun mapToUi(model: List<Feat400FeedItem>): Feat400UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat400UiModel(
            header = UiText("Feat400 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat400UiModel =
        Feat400UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat400UiModel =
        Feat400UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat400UiModel =
        Feat400UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat400Service(
    private val repository: Feat400Repository,
    private val uiMapper: Feat400UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat400UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat400UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat400UserItem1(val user: CoreUser, val label: String)
data class Feat400UserItem2(val user: CoreUser, val label: String)
data class Feat400UserItem3(val user: CoreUser, val label: String)
data class Feat400UserItem4(val user: CoreUser, val label: String)
data class Feat400UserItem5(val user: CoreUser, val label: String)
data class Feat400UserItem6(val user: CoreUser, val label: String)
data class Feat400UserItem7(val user: CoreUser, val label: String)
data class Feat400UserItem8(val user: CoreUser, val label: String)
data class Feat400UserItem9(val user: CoreUser, val label: String)
data class Feat400UserItem10(val user: CoreUser, val label: String)

data class Feat400StateBlock1(val state: Feat400UiModel, val checksum: Int)
data class Feat400StateBlock2(val state: Feat400UiModel, val checksum: Int)
data class Feat400StateBlock3(val state: Feat400UiModel, val checksum: Int)
data class Feat400StateBlock4(val state: Feat400UiModel, val checksum: Int)
data class Feat400StateBlock5(val state: Feat400UiModel, val checksum: Int)
data class Feat400StateBlock6(val state: Feat400UiModel, val checksum: Int)
data class Feat400StateBlock7(val state: Feat400UiModel, val checksum: Int)
data class Feat400StateBlock8(val state: Feat400UiModel, val checksum: Int)
data class Feat400StateBlock9(val state: Feat400UiModel, val checksum: Int)
data class Feat400StateBlock10(val state: Feat400UiModel, val checksum: Int)

fun buildFeat400UserItem(user: CoreUser, index: Int): Feat400UserItem1 {
    return Feat400UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat400StateBlock(model: Feat400UiModel): Feat400StateBlock1 {
    return Feat400StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat400UserSummary> {
    val list = java.util.ArrayList<Feat400UserSummary>(users.size)
    for (user in users) {
        list += Feat400UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat400UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat400UiModel {
    val summaries = (0 until count).map {
        Feat400UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat400UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat400UiModel> {
    val models = java.util.ArrayList<Feat400UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat400AnalyticsEvent1(val name: String, val value: String)
data class Feat400AnalyticsEvent2(val name: String, val value: String)
data class Feat400AnalyticsEvent3(val name: String, val value: String)
data class Feat400AnalyticsEvent4(val name: String, val value: String)
data class Feat400AnalyticsEvent5(val name: String, val value: String)
data class Feat400AnalyticsEvent6(val name: String, val value: String)
data class Feat400AnalyticsEvent7(val name: String, val value: String)
data class Feat400AnalyticsEvent8(val name: String, val value: String)
data class Feat400AnalyticsEvent9(val name: String, val value: String)
data class Feat400AnalyticsEvent10(val name: String, val value: String)

fun logFeat400Event1(event: Feat400AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat400Event2(event: Feat400AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat400Event3(event: Feat400AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat400Event4(event: Feat400AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat400Event5(event: Feat400AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat400Event6(event: Feat400AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat400Event7(event: Feat400AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat400Event8(event: Feat400AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat400Event9(event: Feat400AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat400Event10(event: Feat400AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat400Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat400Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat400Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat400Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat400Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat400Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat400Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat400Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat400Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat400Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat400(u: CoreUser): Feat400Projection1 =
    Feat400Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat400Projection1> {
    val list = java.util.ArrayList<Feat400Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat400(u)
    }
    return list
}
