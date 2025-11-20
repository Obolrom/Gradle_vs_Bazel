package com.romix.feature.feat353

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat353Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat353UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat353FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat353UserSummary
)

data class Feat353UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat353NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat353Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat353Config = Feat353Config()
) {

    fun loadSnapshot(userId: Long): Feat353NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat353NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat353UserSummary {
        return Feat353UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat353FeedItem> {
        val result = java.util.ArrayList<Feat353FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat353FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat353UiMapper {

    fun mapToUi(model: List<Feat353FeedItem>): Feat353UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat353UiModel(
            header = UiText("Feat353 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat353UiModel =
        Feat353UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat353UiModel =
        Feat353UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat353UiModel =
        Feat353UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat353Service(
    private val repository: Feat353Repository,
    private val uiMapper: Feat353UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat353UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat353UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat353UserItem1(val user: CoreUser, val label: String)
data class Feat353UserItem2(val user: CoreUser, val label: String)
data class Feat353UserItem3(val user: CoreUser, val label: String)
data class Feat353UserItem4(val user: CoreUser, val label: String)
data class Feat353UserItem5(val user: CoreUser, val label: String)
data class Feat353UserItem6(val user: CoreUser, val label: String)
data class Feat353UserItem7(val user: CoreUser, val label: String)
data class Feat353UserItem8(val user: CoreUser, val label: String)
data class Feat353UserItem9(val user: CoreUser, val label: String)
data class Feat353UserItem10(val user: CoreUser, val label: String)

data class Feat353StateBlock1(val state: Feat353UiModel, val checksum: Int)
data class Feat353StateBlock2(val state: Feat353UiModel, val checksum: Int)
data class Feat353StateBlock3(val state: Feat353UiModel, val checksum: Int)
data class Feat353StateBlock4(val state: Feat353UiModel, val checksum: Int)
data class Feat353StateBlock5(val state: Feat353UiModel, val checksum: Int)
data class Feat353StateBlock6(val state: Feat353UiModel, val checksum: Int)
data class Feat353StateBlock7(val state: Feat353UiModel, val checksum: Int)
data class Feat353StateBlock8(val state: Feat353UiModel, val checksum: Int)
data class Feat353StateBlock9(val state: Feat353UiModel, val checksum: Int)
data class Feat353StateBlock10(val state: Feat353UiModel, val checksum: Int)

fun buildFeat353UserItem(user: CoreUser, index: Int): Feat353UserItem1 {
    return Feat353UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat353StateBlock(model: Feat353UiModel): Feat353StateBlock1 {
    return Feat353StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat353UserSummary> {
    val list = java.util.ArrayList<Feat353UserSummary>(users.size)
    for (user in users) {
        list += Feat353UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat353UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat353UiModel {
    val summaries = (0 until count).map {
        Feat353UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat353UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat353UiModel> {
    val models = java.util.ArrayList<Feat353UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat353AnalyticsEvent1(val name: String, val value: String)
data class Feat353AnalyticsEvent2(val name: String, val value: String)
data class Feat353AnalyticsEvent3(val name: String, val value: String)
data class Feat353AnalyticsEvent4(val name: String, val value: String)
data class Feat353AnalyticsEvent5(val name: String, val value: String)
data class Feat353AnalyticsEvent6(val name: String, val value: String)
data class Feat353AnalyticsEvent7(val name: String, val value: String)
data class Feat353AnalyticsEvent8(val name: String, val value: String)
data class Feat353AnalyticsEvent9(val name: String, val value: String)
data class Feat353AnalyticsEvent10(val name: String, val value: String)

fun logFeat353Event1(event: Feat353AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat353Event2(event: Feat353AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat353Event3(event: Feat353AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat353Event4(event: Feat353AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat353Event5(event: Feat353AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat353Event6(event: Feat353AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat353Event7(event: Feat353AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat353Event8(event: Feat353AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat353Event9(event: Feat353AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat353Event10(event: Feat353AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat353Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat353Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat353Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat353Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat353Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat353Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat353Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat353Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat353Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat353Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat353(u: CoreUser): Feat353Projection1 =
    Feat353Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat353Projection1> {
    val list = java.util.ArrayList<Feat353Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat353(u)
    }
    return list
}
