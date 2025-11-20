package com.romix.feature.feat466

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat466Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat466UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat466FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat466UserSummary
)

data class Feat466UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat466NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat466Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat466Config = Feat466Config()
) {

    fun loadSnapshot(userId: Long): Feat466NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat466NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat466UserSummary {
        return Feat466UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat466FeedItem> {
        val result = java.util.ArrayList<Feat466FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat466FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat466UiMapper {

    fun mapToUi(model: List<Feat466FeedItem>): Feat466UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat466UiModel(
            header = UiText("Feat466 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat466UiModel =
        Feat466UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat466UiModel =
        Feat466UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat466UiModel =
        Feat466UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat466Service(
    private val repository: Feat466Repository,
    private val uiMapper: Feat466UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat466UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat466UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat466UserItem1(val user: CoreUser, val label: String)
data class Feat466UserItem2(val user: CoreUser, val label: String)
data class Feat466UserItem3(val user: CoreUser, val label: String)
data class Feat466UserItem4(val user: CoreUser, val label: String)
data class Feat466UserItem5(val user: CoreUser, val label: String)
data class Feat466UserItem6(val user: CoreUser, val label: String)
data class Feat466UserItem7(val user: CoreUser, val label: String)
data class Feat466UserItem8(val user: CoreUser, val label: String)
data class Feat466UserItem9(val user: CoreUser, val label: String)
data class Feat466UserItem10(val user: CoreUser, val label: String)

data class Feat466StateBlock1(val state: Feat466UiModel, val checksum: Int)
data class Feat466StateBlock2(val state: Feat466UiModel, val checksum: Int)
data class Feat466StateBlock3(val state: Feat466UiModel, val checksum: Int)
data class Feat466StateBlock4(val state: Feat466UiModel, val checksum: Int)
data class Feat466StateBlock5(val state: Feat466UiModel, val checksum: Int)
data class Feat466StateBlock6(val state: Feat466UiModel, val checksum: Int)
data class Feat466StateBlock7(val state: Feat466UiModel, val checksum: Int)
data class Feat466StateBlock8(val state: Feat466UiModel, val checksum: Int)
data class Feat466StateBlock9(val state: Feat466UiModel, val checksum: Int)
data class Feat466StateBlock10(val state: Feat466UiModel, val checksum: Int)

fun buildFeat466UserItem(user: CoreUser, index: Int): Feat466UserItem1 {
    return Feat466UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat466StateBlock(model: Feat466UiModel): Feat466StateBlock1 {
    return Feat466StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat466UserSummary> {
    val list = java.util.ArrayList<Feat466UserSummary>(users.size)
    for (user in users) {
        list += Feat466UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat466UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat466UiModel {
    val summaries = (0 until count).map {
        Feat466UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat466UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat466UiModel> {
    val models = java.util.ArrayList<Feat466UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat466AnalyticsEvent1(val name: String, val value: String)
data class Feat466AnalyticsEvent2(val name: String, val value: String)
data class Feat466AnalyticsEvent3(val name: String, val value: String)
data class Feat466AnalyticsEvent4(val name: String, val value: String)
data class Feat466AnalyticsEvent5(val name: String, val value: String)
data class Feat466AnalyticsEvent6(val name: String, val value: String)
data class Feat466AnalyticsEvent7(val name: String, val value: String)
data class Feat466AnalyticsEvent8(val name: String, val value: String)
data class Feat466AnalyticsEvent9(val name: String, val value: String)
data class Feat466AnalyticsEvent10(val name: String, val value: String)

fun logFeat466Event1(event: Feat466AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat466Event2(event: Feat466AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat466Event3(event: Feat466AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat466Event4(event: Feat466AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat466Event5(event: Feat466AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat466Event6(event: Feat466AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat466Event7(event: Feat466AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat466Event8(event: Feat466AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat466Event9(event: Feat466AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat466Event10(event: Feat466AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat466Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat466Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat466Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat466Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat466Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat466Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat466Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat466Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat466Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat466Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat466(u: CoreUser): Feat466Projection1 =
    Feat466Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat466Projection1> {
    val list = java.util.ArrayList<Feat466Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat466(u)
    }
    return list
}
