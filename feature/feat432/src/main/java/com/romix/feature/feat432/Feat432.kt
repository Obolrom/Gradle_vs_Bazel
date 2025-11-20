package com.romix.feature.feat432

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat432Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat432UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat432FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat432UserSummary
)

data class Feat432UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat432NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat432Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat432Config = Feat432Config()
) {

    fun loadSnapshot(userId: Long): Feat432NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat432NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat432UserSummary {
        return Feat432UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat432FeedItem> {
        val result = java.util.ArrayList<Feat432FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat432FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat432UiMapper {

    fun mapToUi(model: List<Feat432FeedItem>): Feat432UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat432UiModel(
            header = UiText("Feat432 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat432UiModel =
        Feat432UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat432UiModel =
        Feat432UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat432UiModel =
        Feat432UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat432Service(
    private val repository: Feat432Repository,
    private val uiMapper: Feat432UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat432UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat432UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat432UserItem1(val user: CoreUser, val label: String)
data class Feat432UserItem2(val user: CoreUser, val label: String)
data class Feat432UserItem3(val user: CoreUser, val label: String)
data class Feat432UserItem4(val user: CoreUser, val label: String)
data class Feat432UserItem5(val user: CoreUser, val label: String)
data class Feat432UserItem6(val user: CoreUser, val label: String)
data class Feat432UserItem7(val user: CoreUser, val label: String)
data class Feat432UserItem8(val user: CoreUser, val label: String)
data class Feat432UserItem9(val user: CoreUser, val label: String)
data class Feat432UserItem10(val user: CoreUser, val label: String)

data class Feat432StateBlock1(val state: Feat432UiModel, val checksum: Int)
data class Feat432StateBlock2(val state: Feat432UiModel, val checksum: Int)
data class Feat432StateBlock3(val state: Feat432UiModel, val checksum: Int)
data class Feat432StateBlock4(val state: Feat432UiModel, val checksum: Int)
data class Feat432StateBlock5(val state: Feat432UiModel, val checksum: Int)
data class Feat432StateBlock6(val state: Feat432UiModel, val checksum: Int)
data class Feat432StateBlock7(val state: Feat432UiModel, val checksum: Int)
data class Feat432StateBlock8(val state: Feat432UiModel, val checksum: Int)
data class Feat432StateBlock9(val state: Feat432UiModel, val checksum: Int)
data class Feat432StateBlock10(val state: Feat432UiModel, val checksum: Int)

fun buildFeat432UserItem(user: CoreUser, index: Int): Feat432UserItem1 {
    return Feat432UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat432StateBlock(model: Feat432UiModel): Feat432StateBlock1 {
    return Feat432StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat432UserSummary> {
    val list = java.util.ArrayList<Feat432UserSummary>(users.size)
    for (user in users) {
        list += Feat432UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat432UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat432UiModel {
    val summaries = (0 until count).map {
        Feat432UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat432UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat432UiModel> {
    val models = java.util.ArrayList<Feat432UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat432AnalyticsEvent1(val name: String, val value: String)
data class Feat432AnalyticsEvent2(val name: String, val value: String)
data class Feat432AnalyticsEvent3(val name: String, val value: String)
data class Feat432AnalyticsEvent4(val name: String, val value: String)
data class Feat432AnalyticsEvent5(val name: String, val value: String)
data class Feat432AnalyticsEvent6(val name: String, val value: String)
data class Feat432AnalyticsEvent7(val name: String, val value: String)
data class Feat432AnalyticsEvent8(val name: String, val value: String)
data class Feat432AnalyticsEvent9(val name: String, val value: String)
data class Feat432AnalyticsEvent10(val name: String, val value: String)

fun logFeat432Event1(event: Feat432AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat432Event2(event: Feat432AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat432Event3(event: Feat432AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat432Event4(event: Feat432AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat432Event5(event: Feat432AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat432Event6(event: Feat432AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat432Event7(event: Feat432AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat432Event8(event: Feat432AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat432Event9(event: Feat432AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat432Event10(event: Feat432AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat432Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat432Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat432Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat432Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat432Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat432Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat432Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat432Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat432Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat432Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat432(u: CoreUser): Feat432Projection1 =
    Feat432Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat432Projection1> {
    val list = java.util.ArrayList<Feat432Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat432(u)
    }
    return list
}
