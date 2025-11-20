package com.romix.feature.feat518

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat518Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat518UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat518FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat518UserSummary
)

data class Feat518UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat518NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat518Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat518Config = Feat518Config()
) {

    fun loadSnapshot(userId: Long): Feat518NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat518NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat518UserSummary {
        return Feat518UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat518FeedItem> {
        val result = java.util.ArrayList<Feat518FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat518FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat518UiMapper {

    fun mapToUi(model: List<Feat518FeedItem>): Feat518UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat518UiModel(
            header = UiText("Feat518 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat518UiModel =
        Feat518UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat518UiModel =
        Feat518UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat518UiModel =
        Feat518UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat518Service(
    private val repository: Feat518Repository,
    private val uiMapper: Feat518UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat518UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat518UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat518UserItem1(val user: CoreUser, val label: String)
data class Feat518UserItem2(val user: CoreUser, val label: String)
data class Feat518UserItem3(val user: CoreUser, val label: String)
data class Feat518UserItem4(val user: CoreUser, val label: String)
data class Feat518UserItem5(val user: CoreUser, val label: String)
data class Feat518UserItem6(val user: CoreUser, val label: String)
data class Feat518UserItem7(val user: CoreUser, val label: String)
data class Feat518UserItem8(val user: CoreUser, val label: String)
data class Feat518UserItem9(val user: CoreUser, val label: String)
data class Feat518UserItem10(val user: CoreUser, val label: String)

data class Feat518StateBlock1(val state: Feat518UiModel, val checksum: Int)
data class Feat518StateBlock2(val state: Feat518UiModel, val checksum: Int)
data class Feat518StateBlock3(val state: Feat518UiModel, val checksum: Int)
data class Feat518StateBlock4(val state: Feat518UiModel, val checksum: Int)
data class Feat518StateBlock5(val state: Feat518UiModel, val checksum: Int)
data class Feat518StateBlock6(val state: Feat518UiModel, val checksum: Int)
data class Feat518StateBlock7(val state: Feat518UiModel, val checksum: Int)
data class Feat518StateBlock8(val state: Feat518UiModel, val checksum: Int)
data class Feat518StateBlock9(val state: Feat518UiModel, val checksum: Int)
data class Feat518StateBlock10(val state: Feat518UiModel, val checksum: Int)

fun buildFeat518UserItem(user: CoreUser, index: Int): Feat518UserItem1 {
    return Feat518UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat518StateBlock(model: Feat518UiModel): Feat518StateBlock1 {
    return Feat518StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat518UserSummary> {
    val list = java.util.ArrayList<Feat518UserSummary>(users.size)
    for (user in users) {
        list += Feat518UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat518UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat518UiModel {
    val summaries = (0 until count).map {
        Feat518UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat518UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat518UiModel> {
    val models = java.util.ArrayList<Feat518UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat518AnalyticsEvent1(val name: String, val value: String)
data class Feat518AnalyticsEvent2(val name: String, val value: String)
data class Feat518AnalyticsEvent3(val name: String, val value: String)
data class Feat518AnalyticsEvent4(val name: String, val value: String)
data class Feat518AnalyticsEvent5(val name: String, val value: String)
data class Feat518AnalyticsEvent6(val name: String, val value: String)
data class Feat518AnalyticsEvent7(val name: String, val value: String)
data class Feat518AnalyticsEvent8(val name: String, val value: String)
data class Feat518AnalyticsEvent9(val name: String, val value: String)
data class Feat518AnalyticsEvent10(val name: String, val value: String)

fun logFeat518Event1(event: Feat518AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat518Event2(event: Feat518AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat518Event3(event: Feat518AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat518Event4(event: Feat518AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat518Event5(event: Feat518AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat518Event6(event: Feat518AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat518Event7(event: Feat518AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat518Event8(event: Feat518AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat518Event9(event: Feat518AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat518Event10(event: Feat518AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat518Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat518Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat518Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat518Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat518Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat518Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat518Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat518Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat518Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat518Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat518(u: CoreUser): Feat518Projection1 =
    Feat518Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat518Projection1> {
    val list = java.util.ArrayList<Feat518Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat518(u)
    }
    return list
}
