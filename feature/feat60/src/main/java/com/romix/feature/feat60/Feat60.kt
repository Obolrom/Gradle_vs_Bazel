package com.romix.feature.feat60

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat60Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat60UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat60FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat60UserSummary
)

data class Feat60UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat60NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat60Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat60Config = Feat60Config()
) {

    fun loadSnapshot(userId: Long): Feat60NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat60NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat60UserSummary {
        return Feat60UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat60FeedItem> {
        val result = java.util.ArrayList<Feat60FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat60FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat60UiMapper {

    fun mapToUi(model: List<Feat60FeedItem>): Feat60UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat60UiModel(
            header = UiText("Feat60 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat60UiModel =
        Feat60UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat60UiModel =
        Feat60UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat60UiModel =
        Feat60UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat60Service(
    private val repository: Feat60Repository,
    private val uiMapper: Feat60UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat60UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat60UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat60UserItem1(val user: CoreUser, val label: String)
data class Feat60UserItem2(val user: CoreUser, val label: String)
data class Feat60UserItem3(val user: CoreUser, val label: String)
data class Feat60UserItem4(val user: CoreUser, val label: String)
data class Feat60UserItem5(val user: CoreUser, val label: String)
data class Feat60UserItem6(val user: CoreUser, val label: String)
data class Feat60UserItem7(val user: CoreUser, val label: String)
data class Feat60UserItem8(val user: CoreUser, val label: String)
data class Feat60UserItem9(val user: CoreUser, val label: String)
data class Feat60UserItem10(val user: CoreUser, val label: String)

data class Feat60StateBlock1(val state: Feat60UiModel, val checksum: Int)
data class Feat60StateBlock2(val state: Feat60UiModel, val checksum: Int)
data class Feat60StateBlock3(val state: Feat60UiModel, val checksum: Int)
data class Feat60StateBlock4(val state: Feat60UiModel, val checksum: Int)
data class Feat60StateBlock5(val state: Feat60UiModel, val checksum: Int)
data class Feat60StateBlock6(val state: Feat60UiModel, val checksum: Int)
data class Feat60StateBlock7(val state: Feat60UiModel, val checksum: Int)
data class Feat60StateBlock8(val state: Feat60UiModel, val checksum: Int)
data class Feat60StateBlock9(val state: Feat60UiModel, val checksum: Int)
data class Feat60StateBlock10(val state: Feat60UiModel, val checksum: Int)

fun buildFeat60UserItem(user: CoreUser, index: Int): Feat60UserItem1 {
    return Feat60UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat60StateBlock(model: Feat60UiModel): Feat60StateBlock1 {
    return Feat60StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat60UserSummary> {
    val list = java.util.ArrayList<Feat60UserSummary>(users.size)
    for (user in users) {
        list += Feat60UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat60UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat60UiModel {
    val summaries = (0 until count).map {
        Feat60UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat60UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat60UiModel> {
    val models = java.util.ArrayList<Feat60UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat60AnalyticsEvent1(val name: String, val value: String)
data class Feat60AnalyticsEvent2(val name: String, val value: String)
data class Feat60AnalyticsEvent3(val name: String, val value: String)
data class Feat60AnalyticsEvent4(val name: String, val value: String)
data class Feat60AnalyticsEvent5(val name: String, val value: String)
data class Feat60AnalyticsEvent6(val name: String, val value: String)
data class Feat60AnalyticsEvent7(val name: String, val value: String)
data class Feat60AnalyticsEvent8(val name: String, val value: String)
data class Feat60AnalyticsEvent9(val name: String, val value: String)
data class Feat60AnalyticsEvent10(val name: String, val value: String)

fun logFeat60Event1(event: Feat60AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat60Event2(event: Feat60AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat60Event3(event: Feat60AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat60Event4(event: Feat60AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat60Event5(event: Feat60AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat60Event6(event: Feat60AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat60Event7(event: Feat60AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat60Event8(event: Feat60AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat60Event9(event: Feat60AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat60Event10(event: Feat60AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat60Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat60Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat60Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat60Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat60Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat60Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat60Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat60Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat60Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat60Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat60(u: CoreUser): Feat60Projection1 =
    Feat60Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat60Projection1> {
    val list = java.util.ArrayList<Feat60Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat60(u)
    }
    return list
}
