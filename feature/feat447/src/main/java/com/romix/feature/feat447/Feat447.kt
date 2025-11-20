package com.romix.feature.feat447

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat447Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat447UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat447FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat447UserSummary
)

data class Feat447UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat447NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat447Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat447Config = Feat447Config()
) {

    fun loadSnapshot(userId: Long): Feat447NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat447NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat447UserSummary {
        return Feat447UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat447FeedItem> {
        val result = java.util.ArrayList<Feat447FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat447FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat447UiMapper {

    fun mapToUi(model: List<Feat447FeedItem>): Feat447UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat447UiModel(
            header = UiText("Feat447 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat447UiModel =
        Feat447UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat447UiModel =
        Feat447UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat447UiModel =
        Feat447UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat447Service(
    private val repository: Feat447Repository,
    private val uiMapper: Feat447UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat447UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat447UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat447UserItem1(val user: CoreUser, val label: String)
data class Feat447UserItem2(val user: CoreUser, val label: String)
data class Feat447UserItem3(val user: CoreUser, val label: String)
data class Feat447UserItem4(val user: CoreUser, val label: String)
data class Feat447UserItem5(val user: CoreUser, val label: String)
data class Feat447UserItem6(val user: CoreUser, val label: String)
data class Feat447UserItem7(val user: CoreUser, val label: String)
data class Feat447UserItem8(val user: CoreUser, val label: String)
data class Feat447UserItem9(val user: CoreUser, val label: String)
data class Feat447UserItem10(val user: CoreUser, val label: String)

data class Feat447StateBlock1(val state: Feat447UiModel, val checksum: Int)
data class Feat447StateBlock2(val state: Feat447UiModel, val checksum: Int)
data class Feat447StateBlock3(val state: Feat447UiModel, val checksum: Int)
data class Feat447StateBlock4(val state: Feat447UiModel, val checksum: Int)
data class Feat447StateBlock5(val state: Feat447UiModel, val checksum: Int)
data class Feat447StateBlock6(val state: Feat447UiModel, val checksum: Int)
data class Feat447StateBlock7(val state: Feat447UiModel, val checksum: Int)
data class Feat447StateBlock8(val state: Feat447UiModel, val checksum: Int)
data class Feat447StateBlock9(val state: Feat447UiModel, val checksum: Int)
data class Feat447StateBlock10(val state: Feat447UiModel, val checksum: Int)

fun buildFeat447UserItem(user: CoreUser, index: Int): Feat447UserItem1 {
    return Feat447UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat447StateBlock(model: Feat447UiModel): Feat447StateBlock1 {
    return Feat447StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat447UserSummary> {
    val list = java.util.ArrayList<Feat447UserSummary>(users.size)
    for (user in users) {
        list += Feat447UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat447UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat447UiModel {
    val summaries = (0 until count).map {
        Feat447UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat447UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat447UiModel> {
    val models = java.util.ArrayList<Feat447UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat447AnalyticsEvent1(val name: String, val value: String)
data class Feat447AnalyticsEvent2(val name: String, val value: String)
data class Feat447AnalyticsEvent3(val name: String, val value: String)
data class Feat447AnalyticsEvent4(val name: String, val value: String)
data class Feat447AnalyticsEvent5(val name: String, val value: String)
data class Feat447AnalyticsEvent6(val name: String, val value: String)
data class Feat447AnalyticsEvent7(val name: String, val value: String)
data class Feat447AnalyticsEvent8(val name: String, val value: String)
data class Feat447AnalyticsEvent9(val name: String, val value: String)
data class Feat447AnalyticsEvent10(val name: String, val value: String)

fun logFeat447Event1(event: Feat447AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat447Event2(event: Feat447AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat447Event3(event: Feat447AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat447Event4(event: Feat447AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat447Event5(event: Feat447AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat447Event6(event: Feat447AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat447Event7(event: Feat447AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat447Event8(event: Feat447AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat447Event9(event: Feat447AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat447Event10(event: Feat447AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat447Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat447Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat447Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat447Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat447Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat447Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat447Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat447Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat447Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat447Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat447(u: CoreUser): Feat447Projection1 =
    Feat447Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat447Projection1> {
    val list = java.util.ArrayList<Feat447Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat447(u)
    }
    return list
}
